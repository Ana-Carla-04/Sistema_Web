package br.edu.ufersa.SIPA.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(contentType -> {})
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ==========================================
                        // ENDPOINTS PÚBLICOS (apenas autenticação e criação de conta)
                        // ==========================================
                        .requestMatchers(HttpMethod.POST, "/SIPA/login").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/SIPA/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/SIPA/cadastro").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ==========================================
                        // ENDPOINTS PROTEGIDOS (exigem JWT válido)
                        // ==========================================
                        .requestMatchers("/SIPA/cadastro/**").authenticated()
                        .requestMatchers("/SIPA/plantios", "/SIPA/plantios/**").authenticated()
                        .requestMatchers("/SIPA/custos", "/SIPA/custos/**").authenticated()
                        .requestMatchers("/SIPA/tarefas", "/SIPA/tarefas/**").authenticated()
                        .requestMatchers("/SIPA/dashboard", "/SIPA/dashboard/**").authenticated()
                        .requestMatchers("/SIPA/analise-financeira", "/SIPA/analise-financeira/**").authenticated()
                        .requestMatchers("/SIPA/historico", "/SIPA/historico/**").authenticated()
                        .requestMatchers("/SIPA/colheita", "/SIPA/colheita/**").authenticated()

                        // ==========================================
                        // QUALQUER OUTRA REQUISIÇÃO (fallback seguro)
                        // ==========================================
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
