package org.example.handler;

import org.example.exception.BusinessException;
import org.example.dto.RoleDto;
import org.example.dto.RegisterDto;
import org.example.dto.LoginDto;
import org.example.dto.LoginResponseDto;
import org.example.model.Role;
import org.example.model.User;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.example.service.IUserService;
import org.example.service.IRoleService;
import org.example.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.example.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
    private final IUserService userService;
    private final IRoleService roleService;
    private final ModelMapper modelMapper;
    private final RequestValidator requestValidator;
    private final JwtUtil jwtUtil;

    public AuthHandler(IUserService userService,
                        IRoleService roleService,
                        @Qualifier("defaultMapper") ModelMapper modelMapper,
                        RequestValidator requestValidator,
                        JwtUtil jwtUtil) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.requestValidator = requestValidator;
        this.jwtUtil = jwtUtil;
    }

    @PreAuthorize("permitAll()")
    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginDto.class)
                .flatMap(requestValidator::validate)
                .flatMap(loginDto -> userService.validateUser(loginDto.getUsername(), loginDto.getPassword())
                        .flatMap(isValid -> isValid ? 
                                userService.getIdByUsername(loginDto.getUsername()) :
                                Mono.error(new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
                        .flatMap(userId -> {
                            String token = jwtUtil.generateToken(userId);
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(new LoginResponseDto(token));
                        }));
    }

    @PreAuthorize("permitAll()")
    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(RegisterDto.class)
                .flatMap(requestValidator::validate)
                .flatMap(registerDto -> {
                    if (registerDto.getRoles() == null || registerDto.getRoles().isEmpty()) {
                        return userService.save(User.builder()
                                .username(registerDto.getUsername())
                                .password(registerDto.getPassword())
                                .roles(List.of())
                                .status(registerDto.getStatus() != null ? registerDto.getStatus() : true)
                                .build());
                    }
                    
                    return Flux.fromIterable(registerDto.getRoles().stream().distinct().toList())
                            .flatMap(roleId -> roleService.findById(roleId)
                                    .onErrorMap(ex -> new BusinessException(HttpStatus.BAD_REQUEST, "Rol no encontrado: " + roleId)))
                            .collectList()
                            .flatMap(roles -> userService.save(User.builder()
                                    .username(registerDto.getUsername())
                                    .password(registerDto.getPassword())
                                    .roles(roles)
                                    .status(registerDto.getStatus() != null ? registerDto.getStatus() : true)
                                    .build()));
                })
                .flatMap(savedUser -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Usuario registrado exitosamente");
                    return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response);
                });
    }

    @PreAuthorize("permitAll()")
    public Mono<ServerResponse> findAllRoles(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(roleService.findAll().map(this::convertToDto), RoleDto.class);
    }

    private RoleDto convertToDto(Role model) {
        return modelMapper.map(model, RoleDto.class);
    }
}