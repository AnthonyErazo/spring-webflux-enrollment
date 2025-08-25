package org.example.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.dto.*;
import org.example.handler.AuthHandler;
import org.example.handler.CourseHandler;
import org.example.handler.EnrollmentHandler;
import org.example.handler.StudentHandler;
import org.example.dto.LoginDto;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    private static final String STUDENT_TAG = "Gestion de Estudiantes";
    private static final String COURSE_TAG = "Gestion de Cursos";
    private static final String ENROLLMENT_TAG = "Gestion de Matriculas";
    private static final String AUTH_TAG = "Autenticación";

    @Bean("studentRoutes")
    @RouterOperations({
            @RouterOperation(
                    path = "/v1/students",
                    method = RequestMethod.GET,
                    beanClass = StudentHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "getAllStudents",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Listar todos los estudiantes",
                            description = "Obtiene una lista completa de todos los estudiantes registrados en el sistema",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida exitosamente",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(type = "array", implementation = StudentDto.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/students/{id}",
                    method = RequestMethod.GET,
                    beanClass = StudentHandler.class,
                    beanMethod = "findById",
                    operation = @Operation(
                            operationId = "getStudentById",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Buscar estudiante por ID",
                            description = "Recupera la información de un estudiante específico mediante su identificador único",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único del estudiante", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estudiante encontrado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
                                    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/students",
                    method = RequestMethod.POST,
                    beanClass = StudentHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "createStudent",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Crear nuevo estudiante",
                            description = "Registra un nuevo estudiante en el sistema con la información proporcionada",
                            requestBody = @RequestBody(
                                    description = "Información del estudiante a crear",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateStudentDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/students/{id}",
                    method = RequestMethod.PUT,
                    beanClass = StudentHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "updateStudent",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Actualizar estudiante existente",
                            description = "Modifica la información de un estudiante existente. Todos los campos son opcionales, pero al menos uno debe ser proporcionado",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único del estudiante a actualizar", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            requestBody = @RequestBody(
                                    description = "Campos del estudiante a actualizar (todos opcionales)",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateStudentDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Estudiante actualizado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/students/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = StudentHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteStudent",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Eliminar estudiante",
                            description = "Elimina permanentemente un estudiante del sistema",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único del estudiante a eliminar", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Estudiante eliminado exitosamente"),
                                    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/students/age/asc",
                    method = RequestMethod.GET,
                    beanClass = StudentHandler.class,
                    beanMethod = "findAllOrderedByAgeAsc",
                    operation = @Operation(
                            operationId = "getAllByOrderByAgeAsc",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Listar todos los estudiantes ordenados ascendente por edad",
                            description = "Obtiene una lista completa de todos los estudiantes registrados en el sistema ordenados de forma ascendente por edad",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida exitosamente",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(type = "array", implementation = StudentDto.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/students/age/desc",
                    method = RequestMethod.GET,
                    beanClass = StudentHandler.class,
                    beanMethod = "findAllOrderedByAgeDesc",
                    operation = @Operation(
                            operationId = "getAllByOrderByAgeDesc",
                            tags = {STUDENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Listar todos los estudiantes ordenados descendente por edad",
                            description = "Obtiene una lista completa de todos los estudiantes registrados en el sistema ordenados de forma descendente por edad",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de estudiantes obtenida exitosamente",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(type = "array", implementation = StudentDto.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> studentRoutes(StudentHandler handler){
        return route(GET("/v1/students"), handler::findAll)
                .andRoute(GET("/v1/students/{id}"), handler::findById)
                .andRoute(POST("/v1/students"), handler::save)
                .andRoute(PUT("/v1/students/{id}"), handler::update)
                .andRoute(DELETE("/v1/students/{id}"), handler::delete)
                .andRoute(GET("/v1/students/age/asc"), handler::findAllOrderedByAgeAsc)
                .andRoute(GET("/v1/students/age/desc"), handler::findAllOrderedByAgeDesc);
    }

    @Bean("courseRoutes")
    @RouterOperations({
            @RouterOperation(
                    path = "/v1/courses",
                    method = RequestMethod.GET,
                    beanClass = CourseHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "getAllCourses",
                            tags = {COURSE_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Listar todos los cursos",
                            description = "Obtiene una lista completa de todos los cursos disponibles en el sistema",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(type = "array", implementation = CourseDto.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/courses/{id}",
                    method = RequestMethod.GET,
                    beanClass = CourseHandler.class,
                    beanMethod = "findById",
                    operation = @Operation(
                            operationId = "getCourseById",
                            tags = {COURSE_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Buscar curso por ID",
                            description = "Recupera la información de un curso específico mediante su identificador único",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único del curso", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Curso encontrado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDto.class))),
                                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/courses",
                    method = RequestMethod.POST,
                    beanClass = CourseHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "createCourse",
                            tags = {COURSE_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Crear nuevo curso",
                            description = "Registra un nuevo curso en el sistema con la información proporcionada",
                            requestBody = @RequestBody(
                                    description = "Información del curso a crear",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateCourseDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Curso creado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/courses/{id}",
                    method = RequestMethod.PUT,
                    beanClass = CourseHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "updateCourse",
                            tags = {COURSE_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Actualizar curso existente",
                            description = "Modifica la información de un curso existente. Todos los campos son opcionales, pero al menos uno debe ser proporcionado",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único del curso a actualizar", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            requestBody = @RequestBody(
                                    description = "Campos del curso a actualizar (todos opcionales)",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateCourseDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/courses/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = CourseHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteCourse",
                            tags = {COURSE_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Eliminar curso",
                            description = "Elimina permanentemente un curso del sistema",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único del curso a eliminar", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente"),
                                    @ApiResponse(responseCode = "404", description = "Curso no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> courseRoutes(CourseHandler handler){
        return route(GET("/v1/courses"), handler::findAll)
                .andRoute(GET("/v1/courses/{id}"), handler::findById)
                .andRoute(POST("/v1/courses"), handler::save)
                .andRoute(PUT("/v1/courses/{id}"), handler::update)
                .andRoute(DELETE("/v1/courses/{id}"), handler::delete);
    }

    @Bean("enrollmentRoutes")
    @RouterOperations({
            @RouterOperation(
                    path = "/v1/enrollments",
                    method = RequestMethod.GET,
                    beanClass = CourseHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "getAllEnrollments",
                            tags = {ENROLLMENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Listar todas las matriculas",
                            description = "Obtiene una lista completa de todas las matriculas disponibles en el sistema",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Lista de matriculas obtenida exitosamente",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(type = "array", implementation = EnrollmentDto.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/enrollments/{id}",
                    method = RequestMethod.GET,
                    beanClass = CourseHandler.class,
                    beanMethod = "findById",
                    operation = @Operation(
                            operationId = "getEnrollmentById",
                            tags = {ENROLLMENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Buscar matricula por ID",
                            description = "Recupera la información de una matricula específico mediante su identificador único",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único de la matricula", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Matricula encontrado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentDto.class))),
                                    @ApiResponse(responseCode = "404", description = "Matricula no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/enrollments",
                    method = RequestMethod.POST,
                    beanClass = CourseHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "createEnrollment",
                            tags = {ENROLLMENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Crear nueva matricula",
                            description = "Registra una nueva matricula en el sistema con la información proporcionada",
                            requestBody = @RequestBody(
                                    description = "Información de la matricula a crear",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateEnrollmentDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Matricula creado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o incompletos")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/enrollments/{id}",
                    method = RequestMethod.PUT,
                    beanClass = CourseHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "updateEnrollment",
                            tags = {ENROLLMENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Actualizar matricula existente",
                            description = "Modifica la información de una matricula existente. Todos los campos son opcionales, pero al menos uno debe ser proporcionado",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único de la matricula a actualizar", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            requestBody = @RequestBody(
                                    description = "Campos de la matricula a actualizar (todos opcionales)",
                                    required = true,
                                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateEnrollmentDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Matricula actualizado exitosamente",
                                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                                    @ApiResponse(responseCode = "404", description = "Matricula no encontrado")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/enrollments/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = CourseHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "deleteEnrollment",
                            tags = {ENROLLMENT_TAG},
                            security =  @SecurityRequirement(name = "Bearer"),
                            summary = "Eliminar matricula",
                            description = "Elimina permanentemente una matricula del sistema",
                            parameters = {
                                    @Parameter(name = "id", description = "Identificador único de la matricula a eliminar", required = true, in = ParameterIn.PATH,
                                            schema = @Schema(type = "string", example = "507f1f77bcf86cd799439011"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Matricula eliminado exitosamente"),
                                    @ApiResponse(responseCode = "404", description = "Matricula no encontrado")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> matriculaRoutes(EnrollmentHandler handler){
        return route(GET("/v1/enrollments"), handler::findAll)
                .andRoute(GET("/v1/enrollments/{id}"), handler::findById)
                .andRoute(POST("/v1/enrollments"), handler::save)
                .andRoute(PUT("/v1/enrollments/{id}"), handler::update)
                .andRoute(DELETE("/v1/enrollments/{id}"), handler::delete);
    }

    @Bean("authRoutes")
    @RouterOperations({
            @RouterOperation(
                    path = "/v1/auth/login",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            tags = {AUTH_TAG},
                            summary = "Iniciar sesión",
                            description = "Autentica a un usuario y devuelve un token JWT",
                            requestBody = @RequestBody(
                                    description = "Credenciales de usuario",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LoginDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = LoginResponseDto.class))),
                                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/auth/register",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "register",
                    operation = @Operation(
                            operationId = "register",
                            tags = {AUTH_TAG},
                            summary = "Registrar un Usuario",
                            description = "Registrar a un usuario",
                            requestBody = @RequestBody(
                                    description = "Datos de usuario",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = RegisterDto.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(type = "object", example = "{\"message\": \"Autenticación exitosa\"}")))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/v1/auth/roles",
                    method = RequestMethod.GET,
                    beanClass = AuthHandler.class,
                    beanMethod = "findAllRoles",
                    operation = @Operation(
                            operationId = "getAllRoles",
                            tags = {AUTH_TAG},
                            summary = "Obtener roles",
                            description = "obtener todos los roles disponibles",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Roles obtenidos exitosamente",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = RoleDto.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRoutes(AuthHandler authHandler) {
        return route(POST("/v1/auth/login"), authHandler::login)
                .andRoute(POST("/v1/auth/register"), authHandler::register)
                .andRoute(GET("/v1/auth/roles"), authHandler::findAllRoles);
    }
}



