package br.com.rotalivre;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import br.com.rotalivre.Models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:pilot;DB_CLOSE_DELAY=-1","app.citizen-password=Test-Citizen-Only-9823","app.admin-password=Test-Admin-Only-12347"})
@AutoConfigureMockMvc
@Transactional
class PilotApiTest {
    @Autowired MockMvc mvc;
    @Autowired PilotRepository repo;
    @Autowired ObjectMapper mapper;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
    final String report="{\"edgeId\":12,\"category\":\"OBRA\",\"description\":\"Obra fictícia bloqueando a passagem.\",\"blocksPath\":true}";
    @Test void publicHealthAndNetwork() throws Exception {mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.backend").value("Java 21 / Spring Boot"));mvc.perform(get("/api/network")).andExpect(jsonPath("$.nodes.length()").value(12)).andExpect(jsonPath("$.edges.length()").value(20));}
    @Test void publicRoute() throws Exception {mvc.perform(get("/api/route").param("from","1").param("to","8")).andExpect(status().isOk()).andExpect(jsonPath("$.found").value(true)).andExpect(jsonPath("$.synthetic").value(true)).andExpect(jsonPath("$.algorithm").value("Dijkstra — Java 21"));}
    @Test void invalidNodeRejected() throws Exception {mvc.perform(get("/api/route").param("from","1").param("to","99")).andExpect(status().isBadRequest());}
    @Test void malformedNumericRejected() throws Exception {mvc.perform(get("/api/route").param("from","oops").param("to","8")).andExpect(status().isBadRequest());}
    @Test void anonymousCannotWrite() throws Exception {mvc.perform(post("/api/reports").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(report)).andExpect(status().isUnauthorized());}
    @Test void csrfRequired() throws Exception {mvc.perform(post("/api/reports").with(user("cidadao").roles("CITIZEN")).contentType(MediaType.APPLICATION_JSON).content(report)).andExpect(status().isForbidden());}
    @Test void citizenInsertReadAndDetour() throws Exception {
        var before=RoutePlanner.calculate(repo.nodes(),repo.edges(),repo.blockedEdges(),1,8,new Constraints(90,6,30),System.currentTimeMillis());
        var result=mvc.perform(post("/api/reports").with(user("cidadao").roles("CITIZEN")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(report)).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING")).andReturn();
        long id=mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();assertEquals(12,repo.report(id).edgeId());assertEquals(1,repo.history(id).size());
        var after=RoutePlanner.calculate(repo.nodes(),repo.edges(),repo.blockedEdges(),1,8,new Constraints(90,6,30),System.currentTimeMillis());assertTrue(after.found());assertTrue(after.distanceM()>before.distanceM());
    }
    @Test void citizenCannotModerate() throws Exception {mvc.perform(patch("/api/reports/1").with(user("cidadao").roles("CITIZEN")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"RESOLVED\",\"note\":\"Revisão fictícia.\"}")).andExpect(status().isForbidden());}
    @Test void adminReviewCreatesHistory() throws Exception {var r=repo.create(new CreateReport(12,"OBRA","Registro sintético para revisão.",true),"cidadao");mvc.perform(patch("/api/reports/"+r.id()).with(user("moderador").roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"RESOLVED\",\"note\":\"Obra resolvida apenas no teste.\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("RESOLVED"));assertEquals(2,repo.history(r.id()).size());assertFalse(repo.blockedEdges().contains(12));}
    @Test void otherActiveReportsKeepEdgeBlocked() throws Exception {var a=repo.create(new CreateReport(12,"OBRA","Primeiro bloqueio fictício.",true),"cidadao");repo.create(new CreateReport(12,"OBSTRUCAO","Segundo bloqueio fictício.",true),"cidadao");repo.update(a.id(),new UpdateReport("RESOLVED","Apenas o primeiro foi resolvido."),"moderador");assertTrue(repo.blockedEdges().contains(12));}
    @Test void finalStatusCannotBeOverwritten() throws Exception {var a=repo.create(new CreateReport(12,"OBRA","Registro fictício finalizável.",true),"cidadao");repo.update(a.id(),new UpdateReport("RESOLVED","Resolução fictícia no teste."),"moderador");mvc.perform(patch("/api/reports/"+a.id()).with(user("moderador").roles("ADMIN")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CONFIRMED\",\"note\":\"Tentativa de reabrir final.\"}")).andExpect(status().isConflict());}
    @Test void emptyAndInvalidReportRejected() throws Exception {mvc.perform(post("/api/reports").with(user("cidadao").roles("CITIZEN")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"edgeId\":1,\"category\":\"INVALID\",\"description\":\"\",\"blocksPath\":true}")).andExpect(status().isBadRequest());}
    @Test void missingEdgeRejected() throws Exception {mvc.perform(post("/api/reports").with(user("cidadao").roles("CITIZEN")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(report.replace("12","999"))).andExpect(status().isNotFound());}
    @Test void emptyStatsHaveNoInventedPercentage() throws Exception {jdbc.update("DELETE FROM report_history");jdbc.update("DELETE FROM barrier_report");mvc.perform(get("/api/stats")).andExpect(status().isOk()).andExpect(jsonPath("$.totalReports").value(0)).andExpect(jsonPath("$.resolvedPercent").isEmpty()).andExpect(jsonPath("$.byCategory.length()").value(0));}
    @Test void actorNotExposed() throws Exception {mvc.perform(get("/api/reports")).andExpect(status().isOk()).andExpect(jsonPath("$[0].reporter").doesNotExist());mvc.perform(get("/api/reports/1/history").with(user("cidadao").roles("CITIZEN"))).andExpect(jsonPath("$[0].actor").doesNotExist());}
    @Test void publicStatsFromSql() throws Exception {mvc.perform(get("/api/stats")).andExpect(status().isOk()).andExpect(jsonPath("$.totalReports").value(2)).andExpect(jsonPath("$.synthetic").value(true)).andExpect(jsonPath("$.byCategory.length()").value(2));}
    @Test void securityHeadersPresent() throws Exception {mvc.perform(get("/api/health")).andExpect(header().string("X-Content-Type-Options","nosniff")).andExpect(header().exists("Content-Security-Policy")).andExpect(header().string("X-Frame-Options","DENY"));}
    @Test void actualPasswordLogin() throws Exception {mvc.perform(post("/login").with(csrf()).param("username","cidadao").param("password","Test-Citizen-Only-9823")).andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));}
    @Test void invalidPasswordRejected() throws Exception {mvc.perform(post("/login").with(csrf()).param("username","cidadao").param("password","Incorrect-test-password")).andExpect(status().isUnauthorized());}
    @Test void anonymousHistoryDenied() throws Exception {mvc.perform(get("/api/reports/1/history")).andExpect(status().isUnauthorized());}
}
