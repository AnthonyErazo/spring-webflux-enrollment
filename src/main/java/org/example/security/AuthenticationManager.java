package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.service.IUserService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final IUserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String userId;

        try {
            userId = jwtUtil.extractUserId(authToken);
        } catch (Exception e) {
            return Mono.empty();
        }

        if (userId != null && jwtUtil.validateToken(authToken, userId)) {
            return userService.findById(userId)
                    .map(user -> {
                        List<String> roleNames = user.getRoles().stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toList());
                        
                        List<SimpleGrantedAuthority> authorities = roleNames.stream()
                                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                                .collect(Collectors.toList());

                        return (Authentication) new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                authorities
                        );
                    })
                    .switchIfEmpty(Mono.empty());
        } else {
            return Mono.empty();
        }
    }
}
