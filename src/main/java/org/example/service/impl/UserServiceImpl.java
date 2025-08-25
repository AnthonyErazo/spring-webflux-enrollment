package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.model.User;
import org.example.repo.IGenericRepo;
import org.example.repo.IUserRepo;
import org.example.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User, String> implements IUserService {

    private final IUserRepo repo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    protected IGenericRepo<User, String> getRepo() {
        return repo;
    }

    @Override
    public Mono<User> save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return getRepo().save(user)
                .onErrorMap(ex -> {
                    if (ex instanceof BusinessException) {
                        return ex;
                    }
                    return new BusinessException(HttpStatus.BAD_REQUEST, "Error al guardar la entidad: " + ex.getMessage());
                });
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    @Override
    public Mono<Boolean> validateUser(String username, String password) {
        return findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<String> getIdByUsername(String username) {
        return findByUsername(username)
                .map(User::getId);
    }
}
