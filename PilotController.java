package br.com.rotalivre;

import static br.com.rotalivre.Models.*;
import java.util.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api")
public class PilotController {
    private final PilotRepository repo;
    public PilotController(PilotRepository repo) { this.repo=repo; }
    @GetMapping("/health") public Map<String,Object> health() {
        return Map.of("status","ok","application","Rota Livre","backend","Java 21 / Spring Boot","database","H2 persistente","nodes",repo.nodes().size(),"synthetic",true);
    }
    @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token) {
        return Map.of("token",token.getToken(),"headerName",token.getHeaderName(),"parameterName",token.getParameterName());
    }
    @GetMapping("/me") public Map<String,Object> me(Authentication auth) {
        if(auth==null||auth instanceof AnonymousAuthenticationToken) return Map.of("authenticated",false,"admin",false);
        return Map.of("authenticated",true,"username",auth.getName(),"admin",auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN")));
    }
    @GetMapping("/network") @Transactional(readOnly=true)
    public Network network() {
        return new Network(repo.nodes(),repo.edges(),new ArrayList<>(repo.blockedEdges()),"Santo Amaro · rede sintética v1","Zona Sul de São Paulo · Terminal Santo Amaro como referência, sem geometria cadastral",
            List.of("Esquema não georreferenciado; não é um GPS nem um mapa real de calçadas.","Pontos, distâncias e condições de demonstração não certificam acessibilidade."));
    }
    @GetMapping("/route") @Transactional(readOnly=true)
    public RouteResult route(@RequestParam int from,@RequestParam int to,
        @RequestParam(defaultValue="90") int minWidthCm,@RequestParam(defaultValue="6") double maxSlopePct) {
        return RoutePlanner.calculate(repo.nodes(),repo.edges(),repo.blockedEdges(),from,to,new Constraints(minWidthCm,maxSlopePct,30),System.currentTimeMillis());
    }
    @GetMapping("/reports") public List<Report> reports() { return repo.reports(); }
    @PostMapping("/reports") public ResponseEntity<Report> create(@Valid @RequestBody CreateReport request, Authentication auth) {
        var report=repo.create(request,auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }
    @PatchMapping("/reports/{id}") public Report update(@PathVariable long id,@Valid @RequestBody UpdateReport request,Authentication auth) {
        return repo.update(id,request,auth.getName());
    }
    @GetMapping("/reports/{id}/history") public List<History> history(@PathVariable long id) {return repo.history(id);}
    @GetMapping("/transport") public List<Transport> transport() {return repo.transport();}
    @GetMapping("/stats") @Transactional(readOnly=true) public Map<String,Object> stats() {return repo.stats();}
}
