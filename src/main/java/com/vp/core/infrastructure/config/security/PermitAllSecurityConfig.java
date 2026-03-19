package com.vp.core.infrastructure.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Mantém o comportamento atual do projeto quando a segurança está desabilitada.
 * Útil para desenvolvimento/bootstrapping, mas por padrão deve ser ligado no profile local.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "false", matchIfMissing = true)
public class PermitAllSecurityConfig {

    @Bean
    SecurityFilterChain permitAllSecurityFilterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll()
                )
                .build();
    }
}
