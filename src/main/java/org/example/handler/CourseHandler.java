package org.example.handler;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.example.dto.CourseDto;
import org.example.dto.CreateCourseDto;
import org.example.dto.UpdateCourseDto;
import org.example.exception.BusinessException;
import org.example.model.Course;
import org.example.service.ICourseService;
import org.example.validator.RequestValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@SecurityRequirement(name = "Bearer")
public class CourseHandler {
    private final ICourseService service;
    private final ModelMapper modelMapper;
    private final RequestValidator requestValidator;

    public CourseHandler(ICourseService service,
                        @Qualifier("defaultMapper") ModelMapper modelMapper,
                        RequestValidator requestValidator) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.requestValidator = requestValidator;
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Mono<ServerResponse> findAll(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), CourseDto.class);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Curso no encontrado con ID: " + id)))
                .map(this::convertToDto)
                .flatMap(result ->
                        ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromValue(result))
                );
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<CreateCourseDto> monoCreateDTO = request.bodyToMono(CreateCourseDto.class);
        return monoCreateDTO
                .flatMap(requestValidator::validate)
                .flatMap(dto -> service.save(convertCreateDtoToDocument(dto)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .created(URI.create(request.uri().toString().concat("/").concat(e.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                );
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<UpdateCourseDto> monoUpdateDTO = request.bodyToMono(UpdateCourseDto.class);

        return monoUpdateDTO
                .flatMap(dto -> {
                    if (dto.getName() == null && dto.getInitials() == null && dto.getStatus() == null) {
                        return Mono.error(new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Al menos un campo debe ser proporcionado para la actualización"));
                    }
                    return Mono.just(dto);
                })
                .flatMap(requestValidator::validate)
                .flatMap(dto -> service.update(id, convertUpdateDtoToDocument(dto)))
                .map(this::convertToDto)
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(e))
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.delete(id)
                .flatMap(result -> {
                    if(result){
                        return ServerResponse.noContent().build();
                    }else{
                        return Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Curso no encontrado con ID: " + id));
                    }
                });
    }

    private CourseDto convertToDto(Course model) {
        return modelMapper.map(model, CourseDto.class);
    }

    private Course convertCreateDtoToDocument(CreateCourseDto dto) {
        return modelMapper.map(dto, Course.class);
    }

    private Course convertUpdateDtoToDocument(UpdateCourseDto dto) {
        Course course = modelMapper.map(dto, Course.class);
        if (dto.getStatus() == null) course.setStatus(null);
        return course;
    }
}
