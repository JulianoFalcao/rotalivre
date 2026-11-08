package br.com.rotalivre;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(@Value("${app.citizen-password}") String citizen,@Value("${app.admin-password}") String admin, BCryptPasswordEncoder encoder) {
        if(citizen.length()<14||admin.length()<14||citizen.equals(admin))
            throw new IllegalStateException("Defina duas senhas de demonstração distintas com pelo menos 14 caracteres pelas variáveis de ambiente.");
        return new InMemoryUserDetailsManager(
            User.withUsername("cidadao").password(encoder.encode(citizen)).roles("CITIZEN").build(),
            User.withUsername("moderador").password(encoder.encode(admin)).roles("ADMIN").build());
    }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a->a
            .requestMatchers(HttpMethod.GET,"/","/index.html","/app.js","/style.css","/favicon.svg","/api/csrf","/api/me","/api/health","/api/network","/api/route","/api/reports","/api/transport","/api/stats").permitAll()
            .requestMatchers(HttpMethod.POST,"/api/reports").authenticated()
            .requestMatchers(HttpMethod.PATCH,"/api/reports/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET,"/api/reports/*/history").authenticated()
            .requestMatchers("/login","/error").permitAll().anyRequest().denyAll())
            .formLogin(f->f.loginPage("/").loginProcessingUrl("/login")
                .successHandler((req,res,auth)->{res.setContentType("application/json");res.getWriter().write("{\"ok\":true}");})
                .failureHandler((req,res,e)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Credenciais inválidas.\"}");}).permitAll())
            .logout(l->l.logoutUrl("/logout").logoutSuccessHandler((req,res,auth)->{res.setContentType("application/json");res.getWriter().write("{\"ok\":true}");}))
            .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{
                res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Entre para registrar ou revisar relatos.\"}");
            }).accessDeniedHandler((req,res,ex)->{
                res.setStatus(403);res.setContentType("application/json");res.getWriter().write("{\"message\":\"Operação não autorizada ou token CSRF inválido.\"}");
            }))
            .headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; font-src 'self'; frame-ancestors 'none'; object-src 'none'; base-uri 'self'; form-action 'self'"))
                .referrerPolicy(r->r.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)));
        return http.build();
    }
}
