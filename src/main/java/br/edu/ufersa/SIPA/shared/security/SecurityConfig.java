package br.edu.ufersa.SIPA.shared.security;

import org.springframework.context.annotation.Bean; // Importa a anotacao @Bean, que indica que o metodo abaixo produz um bean gerenciado pelo Spring.
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Esta classe configura a seguranca da aplicacao, definindo quais endpoints sao publicos e quais exigem autenticacao.
@Configuration
public class SecurityConfig {
    // Define a cadeia de filtros de seguranca, especificando as regras de autorizacao para diferentes endpoints.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Não precisam de estar autenticados para acessar
                .requestMatchers("/SIPA/login/**").permitAll()
                // talvez precise ajustar somente o nome dessa rota, mas a ideia é que o cadastro de usuario seja publico
                .requestMatchers("/SIPA/usuarios/**").permitAll()

                // precisam de estar autenticados para acessar
                .requestMatchers("/SIPA/dashboard/**").authenticated()
                .requestMatchers("/SIPA/analise-financeira/**").authenticated()
                .requestMatchers("/SIPA/historico/**").authenticated()
                // .anyRequest().permitAll()// permite qualquer outra requisicao sem autenticacao
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
