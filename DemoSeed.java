package br.com.rotalivre;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoSeed implements CommandLineRunner {
    private final JdbcTemplate jdbc;
    public DemoSeed(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    @Override @Transactional
    public void run(String... args) {
        if(jdbc.queryForObject("SELECT COUNT(*) FROM network_node",Integer.class)>0) return;
        String[] labels={"Terminal · referência","Travessia A · demo","Farmácia · demo","Comércio · demo","Praça · demo","Travessia B · demo","Travessia C · demo","Saúde · demo","Ponto de ônibus · demo","Travessia D · demo","Serviços · demo","Destino alternativo · demo"};
        for(int i=0;i<12;i++) jdbc.update("INSERT INTO network_node(id,label,x,y,kind,synthetic) VALUES(?,?,?,?,?,?)",i+1,labels[i],95+(i%4)*180,90+(i/4)*160,i==0||i==8?"TRANSPORT":i==2||i==7||i==10?"SERVICE":"CROSSING",true);
        int[][] links={{1,2,80},{2,3,90},{3,4,80},{5,6,100},{6,7,110},{7,8,90},{9,10,80},{10,11,90},{11,12,100},{1,5,100},{2,6,110},{3,7,100},{4,8,120},{5,9,100},{6,10,90},{7,11,110},{8,12,100},{2,5,150},{6,9,160},{7,12,160}};
        long now=System.currentTimeMillis();
        for(int i=0;i<links.length;i++) {
            int id=i+1;String status=id==19?"UNKNOWN":"KNOWN";Long date=id==19?null:id==3?now-90L*86_400_000:now;
            jdbc.update("INSERT INTO network_edge(id,from_node,to_node,label,distance_m,width_cm,slope_pct,step_free,surface,condition_status,verified_at,source,bidirectional,synthetic) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",id,links[i][0],links[i][1],String.format("Trecho %02d · demonstração",id),links[i][2],id==18?95:120,id==18?4.5:3.0,id!=20,"REGULAR",status,date,"Fixture sintética — não observação de campo",true,true);
        }
        jdbc.update("INSERT INTO barrier_report(edge_id,category,description,blocks_path,status,created_at,updated_at,reporter,synthetic) VALUES(?,?,?,?,?,?,?,?,?)",19,"PISO","Exemplo sintético: condições do piso ainda desconhecidas.",false,"CONFIRMED",now,now,"seed",true);
        jdbc.update("INSERT INTO barrier_report(edge_id,category,description,blocks_path,status,created_at,updated_at,reporter,synthetic) VALUES(?,?,?,?,?,?,?,?,?)",17,"OBRA","Exemplo sintético: uma obra bloqueia este trecho da rede de teste.",true,"PENDING",now,now,"seed",true);
        for(var id:jdbc.queryForList("SELECT id FROM barrier_report",Long.class)) jdbc.update("INSERT INTO report_history(report_id,previous_status,new_status,note,actor,created_at) VALUES(?,?,?,?,?,?)",id,null,id==1?"CONFIRMED":"PENDING","Carga inicial de demonstração, sem vistoria física.","seed",now);
        jdbc.update("INSERT INTO transport_option VALUES(?,?,?,?,?,?,?,?,?)",1,1,"Ônibus adaptado · exemplo","BUS","INFORMED","Informação fictícia para demonstrar a conexão. Sem posição ou previsão em tempo real.","Fixture sintética; futura integração Olho Vivo",now,true);
        jdbc.update("INSERT INTO transport_option VALUES(?,?,?,?,?,?,?,?,?)",2,9,"Ponto próximo · exemplo","BUS","UNKNOWN","Condições de embarque, elevador e espaço disponíveis não foram verificadas.","Fixture sintética",now,true);
    }
}
