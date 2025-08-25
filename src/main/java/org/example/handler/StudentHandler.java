package org.example.handler;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.example.dto.CreateStudentDto;
import org.example.dto.StudentDto;
import org.example.dto.UpdateStudentDto;
import org.example.exception.BusinessException;
import org.example.model.Student;
import org.example.service.IStudentService;
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
public class StudentHandler {

    private final IStudentService service;
    private final ModelMapper modelMapper;
    private final RequestValidator requestValidator;

    public StudentHandler(IStudentService service,
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
                .body(service.findAll().map(this::convertToDto), StudentDto.class);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Mono<ServerResponse> findAllOrderedByAgeAsc(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAllByOrderByAgeAsc().map(this::convertToDto), StudentDto.class);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Mono<ServerResponse> findAllOrderedByAgeDesc(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAllByOrderByAgeDesc().map(this::convertToDto), StudentDto.class);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Estudiante no encontrado con ID: " + id)))
                .map(this::convertToDto)
                .flatMap(result ->
                        ServerResponse
                            .ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromValue(result))
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<CreateStudentDto> monoCreateDTO = request.bodyToMono(CreateStudentDto.class);
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

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<UpdateStudentDto> monoUpdateDTO = request.bodyToMono(UpdateStudentDto.class);

        return monoUpdateDTO
                .flatMap(dto -> {
                    if (dto.getNames() == null && dto.getSurnames() == null &&
                            dto.getDni() == null && dto.getAge() == null) {
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
                        return Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Estudiante no encontrado con ID: " + id));
                    }
                });
    }

    private StudentDto convertToDto(Student model) {
        return modelMapper.map(model, StudentDto.class);
    }

    private Student convertCreateDtoToDocument(CreateStudentDto dto) {
        return modelMapper.map(dto, Student.class);
    }

    private Student convertUpdateDtoToDocument(UpdateStudentDto dto) {
        return modelMapper.map(dto, Student.class);
    }
}
