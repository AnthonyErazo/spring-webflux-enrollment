package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(req -> {
                    // Endpoints públicos (sin autenticación)
                    req.pathMatchers("/v1/auth/**").permitAll();
                    
                    // Swagger/OpenAPI (acceso libre)
                    req.pathMatchers("/swagger-ui.html").permitAll();
                    req.pathMatchers("/swagger-ui/**").permitAll();
                    req.pathMatchers("/v3/api-docs/**").permitAll();
                    req.pathMatchers("/v3/api-docs").permitAll();
                    req.pathMatchers("/webjars/**").permitAll();
                    req.pathMatchers("/swagger-resources/**").permitAll();
                    req.pathMatchers("/configuration/**").permitAll();
                    
                    // Todos los demás endpoints requieren autenticación
                    req.anyExchange().authenticated();
                })
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, authEx) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                            
                            return exchange.getResponse().writeWith(
                                    Mono.error(new BusinessException(HttpStatus.UNAUTHORIZED, "No autorizado"))
                            );
                        })
                        .accessDeniedHandler((exchange, accessEx) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                            
                            return exchange.getResponse().writeWith(
                                    Mono.error(new BusinessException(HttpStatus.FORBIDDEN, "Acceso denegado"))
                            );
                        })
                )
                .build();
    }
}
