package org.example.handler;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.example.dto.*;
import org.example.exception.BusinessException;
import org.example.model.Course;
import org.example.model.Enrollment;
import org.example.model.Student;
import org.example.service.ICourseService;
import org.example.service.IEnrollmentService;
import org.example.service.IStudentService;
import org.example.validator.RequestValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@SecurityRequirement(name = "Bearer")
public class EnrollmentHandler {
    private final IEnrollmentService service;
    private final IStudentService studentService;
    private final ICourseService courseService;
    private final ModelMapper modelMapper;
    private final RequestValidator modelValidator;

    public EnrollmentHandler(IEnrollmentService service,
                             IStudentService studentService,
                             ICourseService courseService,
                             @Qualifier("defaultMapper") ModelMapper modelMapper,
                             RequestValidator requestValidator){
        this.service = service;
        this.studentService = studentService;
        this.courseService = courseService;
        this.modelMapper = modelMapper;
        this.modelValidator = requestValidator;
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    public Mono<ServerResponse> findAll(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll().map(this::convertToDto), EnrollmentDto.class);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STUDENT')")
    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return service.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Matricula no encontrado con ID: " + id)))
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
        Mono<CreateEnrollmentDto> monoCreateDTO = request.bodyToMono(CreateEnrollmentDto.class);
        return monoCreateDTO
                .flatMap(modelValidator::validate)
                .flatMap(dto -> {
                    if (dto.getCourseIds() == null || dto.getCourseIds().isEmpty()) {
                        return Mono.error(new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Debe proporcionar al menos un curso para la matrícula"));
                    }
                    List<String> uniqueCourseIds = dto.getCourseIds().stream().distinct().toList();
                    Mono<Student> studentMono = studentService.findById(dto.getStudentId())
                        .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Estudiante no encontrado con ID: " + dto.getStudentId())));
                    Mono<List<Course>> coursesMono = Flux.fromIterable(uniqueCourseIds)
                        .flatMap(courseId -> courseService.findById(courseId)
                            .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Curso no encontrado con ID: " + courseId))))
                        .collectList();
                    Boolean status = dto.getStatus() != null ? dto.getStatus() : true;
                    LocalDateTime dateEnrollment = dto.getDateEnrollment() != null ? dto.getDateEnrollment() : LocalDateTime.now();
                    return Mono.zip(studentMono, coursesMono)
                        .flatMap(tuple -> {
                            Enrollment enrollment = Enrollment.builder()
                                .student(tuple.getT1())
                                .courses(tuple.getT2())
                                .status(status)
                                .dateEnrollment(dateEnrollment)
                                .build();
                            return service.save(enrollment);
                        });
                })
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
        Mono<UpdateEnrollmentDto> monoUpdateDTO = request.bodyToMono(UpdateEnrollmentDto.class);

        return monoUpdateDTO
                .flatMap(dto -> {
                    if ((dto.getCourseIds() == null || dto.getCourseIds().isEmpty()) && dto.getStatus() == null && dto.getDateEnrollment() == null) {
                        return Mono.error(new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY, "Al menos un campo debe ser proporcionado para la actualización"));
                    }
                    if (dto.getCourseIds() != null) {
                        List<String> uniqueCourseIds = dto.getCourseIds().stream().distinct().toList();
                        dto.setCourseIds(uniqueCourseIds);
                    }
                    return Mono.just(dto);
                })
                .flatMap(modelValidator::validate)
                .flatMap(dto -> service.findById(id)
                        .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Matrícula no encontrada con ID: " + id)))
                        .flatMap(existing -> {
                            Mono<List<Course>> coursesMono;
                            if (dto.getCourseIds() != null && !dto.getCourseIds().isEmpty()) {
                                coursesMono = Flux.fromIterable(dto.getCourseIds())
                                        .flatMap(courseId -> courseService.findById(courseId)
                                                .switchIfEmpty(Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Curso no encontrado con ID: " + courseId))))
                                        .collectList();
                            } else {
                                coursesMono = Mono.just(existing.getCourses());
                            }
                            Boolean status = dto.getStatus() != null ? dto.getStatus() : existing.getStatus();
                            LocalDateTime localDateTime = dto.getDateEnrollment() != null ? dto.getDateEnrollment() : existing.getDateEnrollment();
                            return coursesMono.map(courses -> Enrollment.builder()
                                    .id(existing.getId())
                                    .student(existing.getStudent())
                                    .courses(courses)
                                    .status(status)
                                    .dateEnrollment(localDateTime)
                                    .build());
                        })
                        .flatMap(enrollment -> service.update(id, enrollment))
                )
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
                        return Mono.error(new BusinessException(HttpStatus.NOT_FOUND, "Matrícula no encontrada con ID: " + id));
                    }
                });
    }

    private EnrollmentDto convertToDto(Enrollment model) {
        return modelMapper.map(model, EnrollmentDto.class);
    }
}
