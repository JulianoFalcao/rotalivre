-- Rota Livre: base sintética em H2, não estatísticas da cidade.
SELECT category, COUNT(*) AS total FROM barrier_report GROUP BY category ORDER BY total DESC;
SELECT status, COUNT(*) AS total FROM barrier_report GROUP BY status;
SELECT COUNT(*) AS total_reports,
       SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END) AS resolved_reports,
       100.0 * SUM(CASE WHEN status = 'RESOLVED' THEN 1 ELSE 0 END)
         / NULLIF(COUNT(*), 0) AS resolved_percent
FROM barrier_report;
SELECT e.id, e.label, COUNT(r.id) AS active_reports
FROM network_edge e JOIN barrier_report r ON r.edge_id=e.id
WHERE r.status IN ('PENDING','CONFIRMED')
GROUP BY e.id, e.label ORDER BY active_reports DESC, e.id LIMIT 5;
SELECT DISTINCT edge_id FROM barrier_report
WHERE blocks_path=TRUE AND status IN ('PENDING','CONFIRMED');
-- Escolher o ID existente; não expor actor/reporter em saídas públicas.
SELECT report_id, previous_status, new_status, note, created_at
FROM report_history ORDER BY report_id, created_at, id;
