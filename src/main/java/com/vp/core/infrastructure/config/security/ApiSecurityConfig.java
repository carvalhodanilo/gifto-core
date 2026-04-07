package com.vp.core.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vp.core.application.security.AuthenticatedUser;
import com.vp.core.infrastructure.security.DatabaseScopeClaimsEnricher;
import com.vp.core.infrastructure.security.JwtToAuthenticationConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = "app.security", name = "enabled", havingValue = "true")
public class ApiSecurityConfig {

    @Bean
    Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(
            final SecurityProperties securityProperties,
            final DatabaseScopeClaimsEnricher scopeClaimsEnricher
    ) {
        final var rolesClientId = Optional.ofNullable(securityProperties.keycloak())
                .map(SecurityProperties.KeycloakProperties::rolesClientId)
                .orElse(null);
        final var base = new JwtToAuthenticationConverter(rolesClientId);
        return jwt -> {
            final var auth = base.convert(jwt);
            final var principal = auth.getPrincipal();
            if (principal instanceof AuthenticatedUser au) {
                final var enriched = scopeClaimsEnricher.enrich(au);
                return new UsernamePasswordAuthenticationToken(enriched, auth.getCredentials(), auth.getAuthorities());
            }
            return auth;
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            final HttpSecurity http,
            final SecurityProperties securityProperties,
            final ObjectMapper objectMapper,
            final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter
    ) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(SecurityErrorHandlers.authenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(SecurityErrorHandlers.accessDeniedHandler(objectMapper))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(final SecurityProperties props) {
        final var configuration = new CorsConfiguration();
        final var cors = props.cors();

        if (cors != null) {
            configuration.setAllowedOrigins(nullSafeList(cors.allowedOrigins()));
            configuration.setAllowedOriginPatterns(nullSafeList(cors.allowedOriginPatterns()));
            configuration.setAllowedMethods(nullSafeList(cors.allowedMethods()));
            configuration.setAllowedHeaders(nullSafeList(cors.allowedHeaders()));
            configuration.setExposedHeaders(nullSafeList(cors.exposedHeaders()));
            configuration.setAllowCredentials(cors.allowCredentials());
            if (cors.maxAgeSeconds() != null) {
                configuration.setMaxAge(cors.maxAgeSeconds());
            }
        } else {
            configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(List.of("*"));
            configuration.setExposedHeaders(List.of("*"));
            configuration.setAllowCredentials(false);
            configuration.setMaxAge(3600L);
        }

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static List<String> nullSafeList(final List<String> value) {
        return value == null ? List.of() : value;
    }
}
