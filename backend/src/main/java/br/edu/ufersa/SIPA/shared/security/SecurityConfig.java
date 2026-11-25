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
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ==========================================
                        // ENDPOINTS PÚBLICOS (autenticação, cadastro e recuperação de senha em 2 etapas)
                        // ==========================================
                        .requestMatchers(HttpMethod.POST, "/SIPA/login").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/SIPA/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/SIPA/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/SIPA/auth/forgot-password", "/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/SIPA/auth/reset-password/**", "/auth/reset-password/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/SIPA/auth/reset-password", "/auth/reset-password").permitAll()
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
        return new BCryptPasswordEncoder(12);
    }
}
