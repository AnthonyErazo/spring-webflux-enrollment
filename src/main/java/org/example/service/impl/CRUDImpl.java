package org.example.service.impl;

import org.example.exception.BusinessException;
import org.example.repo.IGenericRepo;
import org.example.service.ICRUD;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
@RequiredArgsConstructor
public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<T> save(T t) {
        return checkUniqueFields(t)
                .flatMap(isUnique -> {
                    if (!isUnique) {
                        return Mono.error(new BusinessException(HttpStatus.CONFLICT, "Ya existe un registro con los mismos datos únicos"));
                    }
                    return getRepo().save(t);
                })
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof BusinessException) {
                        return ex;
                    }
                    return new BusinessException(HttpStatus.BAD_REQUEST, "Error al guardar la entidad: " + ex.getMessage());
                });
    }

    @Override
    public Mono<T> update(ID id, T t) {
        return getRepo().findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Entidad no encontrada con ID: " + id)))
                .flatMap(existingEntity -> checkUniqueFieldsForUpdate(t, id, existingEntity.getClass())
                        .flatMap(isUnique -> {
                            if (!isUnique) {
                                return Mono.error(new BusinessException(HttpStatus.CONFLICT, "Ya existe otro registro con los mismos datos únicos"));
                            }
                            try {
                                updateNonNullFields(t, existingEntity);
                                return getRepo().save(existingEntity);
                            } catch (Exception e) {
                                return Mono.error(new BusinessException(HttpStatus.BAD_REQUEST, "Error al actualizar la entidad: " + e.getMessage()));
                            }
                        }))
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof BusinessException) {
                        return ex;
                    }
                    return new BusinessException(HttpStatus.BAD_REQUEST, "Error al actualizar la entidad: " + ex.getMessage());
                });
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> delete(ID id) {
        return getRepo().findById(id)
                .hasElement()
                .flatMap(result -> {
                    if (result) {
                        return getRepo().deleteById(id).thenReturn(true);
                    } else {
                        return Mono.just(false);
                    }
                })
                .onErrorMap(Exception.class, ex ->
                    new BusinessException(HttpStatus.BAD_REQUEST, "Error al eliminar la entidad: " + ex.getMessage())
                );
    }

    private Mono<Boolean> checkUniqueFields(T entity) {
        try {
            Class<?> entityClass = entity.getClass();
            Field[] fields = entityClass.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(org.springframework.data.mongodb.core.index.Indexed.class)) {
                    org.springframework.data.mongodb.core.index.Indexed indexedAnnotation =
                        field.getAnnotation(org.springframework.data.mongodb.core.index.Indexed.class);

                    if (indexedAnnotation.unique()) {
                        field.setAccessible(true);
                        Object fieldValue = field.get(entity);

                        if (fieldValue != null) {
                            Query query = new Query(Criteria.where(field.getName()).is(fieldValue));
                            return mongoTemplate.exists(query, entityClass)
                                    .map(exists -> !exists);
                        }
                    }
                }
            }

            return Mono.just(true);
        } catch (Exception e){
            return Mono.error(new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al validar campos únicos: " + e.getMessage()));
        }
    }

    private Mono<Boolean> checkUniqueFieldsForUpdate(T entity, ID id, Class<?> entityClass) {
        try {
            Field[] fields = entityClass.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(org.springframework.data.mongodb.core.index.Indexed.class)) {
                    org.springframework.data.mongodb.core.index.Indexed indexedAnnotation =
                        field.getAnnotation(org.springframework.data.mongodb.core.index.Indexed.class);

                    if (indexedAnnotation.unique()) {
                        field.setAccessible(true);
                        Object fieldValue = field.get(entity);

                        if (fieldValue != null) {
                            Query query = new Query(Criteria.where(field.getName()).is(fieldValue)
                                    .and("id").ne(id));
                            return mongoTemplate.exists(query, entityClass)
                                    .map(exists -> !exists);
                        }
                    }
                }
            }

            return Mono.just(true);
        } catch (Exception e){
            return Mono.error(new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al validar campos únicos para actualización: " + e.getMessage()));
        }
    }

    private void updateNonNullFields(T source, T target) {
        try {
            Class<?> entityClass = source.getClass();
            Field[] fields = entityClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(source);

                if (value != null && !field.getName().equals("id")) {
                    field.set(target, value);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Error al actualizar campos: " + e.getMessage());
        }
    }
}
