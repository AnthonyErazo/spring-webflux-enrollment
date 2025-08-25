package org.example.service;

import org.example.model.User;
import reactor.core.publisher.Mono;

public interface IUserService extends ICRUD<User,String>{
    Mono<User> findByUsername(String username);
    Mono<Boolean> validateUser(String username, String password);
    Mono<String> getIdByUsername(String username);
}
