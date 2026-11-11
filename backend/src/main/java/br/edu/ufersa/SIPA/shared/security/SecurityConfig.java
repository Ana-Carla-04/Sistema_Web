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
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ==========================================
                        // ENDPOINTS PÚBLICOS (não precisam de login)
                        // ==========================================
                        .requestMatchers(HttpMethod.POST, "/SIPA/login").permitAll()
                        .requestMatchers("/SIPA/login/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/SIPA/cadastro").permitAll()
                        .requestMatchers("/SIPA/cadastro/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/SIPA/usuarios").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ==========================================
                        // ENDPOINTS PROTEGIDOS (precisam de login)
                        // ==========================================
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
