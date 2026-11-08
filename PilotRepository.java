package br.com.rotalivre;

import static br.com.rotalivre.Models.*;
import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Repository
public class PilotRepository {
    private final JdbcTemplate jdbc;
    public PilotRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public List<Node> nodes() {
        return jdbc.query("SELECT * FROM network_node ORDER BY id", (r,i) ->
            new Node(r.getInt("id"),r.getString("label"),r.getInt("x"),r.getInt("y"),r.getString("kind"),r.getBoolean("synthetic")));
    }
    public List<Edge> edges() {
        return jdbc.query("SELECT * FROM network_edge ORDER BY id", (r,i) -> {
            long time=r.getLong("verified_at"); Long stamp=r.wasNull()?null:time;
            return new Edge(r.getInt("id"),r.getInt("from_node"),r.getInt("to_node"),r.getString("label"),
                r.getDouble("distance_m"),r.getInt("width_cm"),r.getDouble("slope_pct"),r.getBoolean("step_free"),
                r.getString("surface"),r.getString("condition_status"),stamp,r.getString("source"),r.getBoolean("bidirectional"),r.getBoolean("synthetic"));
        });
    }
    public Set<Integer> blockedEdges() {
        return new HashSet<>(jdbc.queryForList("SELECT DISTINCT edge_id FROM barrier_report WHERE blocks_path=TRUE AND status IN ('PENDING','CONFIRMED')",Integer.class));
    }
    private Report mapReport(ResultSet r, int i) throws SQLException {
        return new Report(r.getLong("id"),r.getInt("edge_id"),r.getString("category"),r.getString("description"),
            r.getBoolean("blocks_path"),r.getString("status"),r.getLong("created_at"),r.getLong("updated_at"),r.getBoolean("synthetic"));
    }
    public List<Report> reports() {
        return jdbc.query("SELECT * FROM barrier_report ORDER BY created_at DESC, id DESC LIMIT 200",this::mapReport);
    }
    public Report report(long id) {
        var found=jdbc.query("SELECT * FROM barrier_report WHERE id=?",this::mapReport,id);
        if(found.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Relato não encontrado.");
        return found.getFirst();
    }
    public List<History> history(long id) {
        report(id);
        return jdbc.query("SELECT * FROM report_history WHERE report_id=? ORDER BY created_at,id",(r,i)->
            new History(r.getLong("id"),r.getLong("report_id"),r.getString("previous_status"),r.getString("new_status"),r.getString("note"),r.getLong("created_at")),id);
    }
    @Transactional
    public Report create(CreateReport input, String actor) {
        if(input.description().trim().length()<5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Descreva a barreira com pelo menos cinco caracteres.");
        if(jdbc.queryForObject("SELECT COUNT(*) FROM network_edge WHERE id=?",Integer.class,input.edgeId())!=1)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Trecho não encontrado.");
        long now=System.currentTimeMillis(); var key=new GeneratedKeyHolder();
        jdbc.update(c->{
            var p=c.prepareStatement("INSERT INTO barrier_report(edge_id,category,description,blocks_path,status,created_at,updated_at,reporter,synthetic) VALUES(?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            p.setInt(1,input.edgeId());p.setString(2,input.category());p.setString(3,input.description().trim());p.setBoolean(4,input.blocksPath());
            p.setString(5,"PENDING");p.setLong(6,now);p.setLong(7,now);p.setString(8,actor);p.setBoolean(9,true);return p;
        },key);
        long id=Objects.requireNonNull(key.getKey()).longValue();
        jdbc.update("INSERT INTO report_history(report_id,previous_status,new_status,note,actor,created_at) VALUES(?,?,?,?,?,?)",id,null,"PENDING","Relato de demonstração criado; condição física não verificada.",actor,now);
        return report(id);
    }
    @Transactional
    public Report update(long id, UpdateReport input, String actor) {
        var found=jdbc.query("SELECT * FROM barrier_report WHERE id=? FOR UPDATE",this::mapReport,id);
        if(found.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Relato não encontrado.");
        var old=found.getFirst();
        if(Set.of("REJECTED","RESOLVED").contains(old.status()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Relato finalizado; registre uma nova ocorrência se necessário.");
        if(old.status().equals(input.status())) throw new ResponseStatusException(HttpStatus.CONFLICT,"O relato já possui essa situação.");
        if(input.note().trim().length()<5) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Informe a justificativa da revisão.");
        long now=System.currentTimeMillis();
        jdbc.update("UPDATE barrier_report SET status=?,updated_at=? WHERE id=?",input.status(),now,id);
        jdbc.update("INSERT INTO report_history(report_id,previous_status,new_status,note,actor,created_at) VALUES(?,?,?,?,?,?)",id,old.status(),input.status(),input.note().trim(),actor,now);
        return report(id);
    }
    public List<Transport> transport() {
        return jdbc.query("SELECT * FROM transport_option ORDER BY id",(r,i)->new Transport(r.getInt("id"),r.getInt("node_id"),r.getString("label"),r.getString("mode"),r.getString("accessibility"),r.getString("information"),r.getString("source"),r.getLong("updated_at"),r.getBoolean("synthetic")));
    }
    public Map<String,Object> stats() {
        var out=new LinkedHashMap<String,Object>();
        int total=jdbc.queryForObject("SELECT COUNT(*) FROM barrier_report",Integer.class);
        int active=jdbc.queryForObject("SELECT COUNT(*) FROM barrier_report WHERE status IN ('PENDING','CONFIRMED')",Integer.class);
        int resolved=jdbc.queryForObject("SELECT COUNT(*) FROM barrier_report WHERE status='RESOLVED'",Integer.class);
        out.put("totalReports",total);out.put("activeReports",active);out.put("resolvedReports",resolved);
        out.put("resolvedPercent",total==0?null:Math.round(resolved*10000.0/total)/100.0);
        out.put("blockedEdges",blockedEdges().size());out.put("networkNodes",nodes().size());out.put("networkEdges",edges().size());
        out.put("byCategory",jdbc.queryForList("SELECT category AS \"category\", COUNT(*) AS \"total\" FROM barrier_report GROUP BY category ORDER BY COUNT(*) DESC,category"));
        out.put("byStatus",jdbc.queryForList("SELECT status AS \"status\", COUNT(*) AS \"total\" FROM barrier_report GROUP BY status ORDER BY status"));
        out.put("criticalEdges",jdbc.queryForList("SELECT e.id AS \"edgeId\", e.label AS \"label\", COUNT(r.id) AS \"total\" FROM network_edge e JOIN barrier_report r ON r.edge_id=e.id WHERE r.status IN ('PENDING','CONFIRMED') GROUP BY e.id,e.label ORDER BY COUNT(r.id) DESC,e.id LIMIT 5"));
        out.put("generatedAt",System.currentTimeMillis());out.put("synthetic",true);
        out.put("warning","Indicadores de base sintética: não representam acessibilidade ou atendimento real de São Paulo.");
        return out;
    }
}
