package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Role;
import org.example.service.IRoleService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final IRoleService roleService;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            log.info("Iniciando inserción de roles por defecto...");

            String[] defaultRoles = {"ADMIN", "STUDENT", "TEACHER"};

            Flux.fromArray(defaultRoles)
                .flatMap(roleName ->
                    roleService.findAll()
                        .filter(role -> role.getName().equals(roleName))
                        .hasElements()
                        .flatMap(exists -> {
                            if (!exists) {
                                log.info("Creando rol: {}", roleName);
                                Role newRole = Role.builder()
                                    .name(roleName)
                                    .build();
                                return roleService.save(newRole);
                            } else {
                                log.info("Rol {} ya existe", roleName);
                                return roleService.findAll()
                                    .filter(role -> role.getName().equals(roleName))
                                    .next();
                            }
                        })
                )
                .doOnNext(role -> log.info("Rol procesado: {}", role.getName()))
                .doOnComplete(() -> log.info("Inicialización de roles completada"))
                .doOnError(error -> log.error("Error al inicializar roles: ", error))
                .subscribe();
        };
    }
}
