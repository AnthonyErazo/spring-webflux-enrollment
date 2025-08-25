package org.example.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T t){
        if(t == null){
            return Mono.error(new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "El request no puede ser nulo"));
        }

        Set<ConstraintViolation<T>> constraints = validator.validate(t);

        if(constraints == null || constraints.isEmpty()){
            return Mono.just(t);
        }

        String errorMessage = constraints.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return Mono.error(new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Errores de validación: " + errorMessage));
    }
}
