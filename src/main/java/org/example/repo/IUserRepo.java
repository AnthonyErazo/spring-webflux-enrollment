package org.example.repo;

import org.example.model.User;
import reactor.core.publisher.Mono;

public interface IUserRepo extends IGenericRepo<User,String>{
    Mono<User> findByUsername(String username);
}