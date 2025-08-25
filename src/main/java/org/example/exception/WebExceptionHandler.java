package org.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Order(-1)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest req) {
        Throwable ex = getError(req);
        LocalDateTime now = LocalDateTime.now();
        CustomErrorResponse errorResponse;
        HttpStatus status;

        String requestUri = req.uri().toString();
        String httpMethod = req.method().name();

        switch (ex) {
            case BusinessException businessException -> {
                logger.error("Business error [{}] on {} {}: {}",
                    businessException.getErrorCode().name(), httpMethod, requestUri, businessException.getMessage(), businessException);

                errorResponse = new CustomErrorResponse(
                        businessException.getMessage(),
                        now
                );
                status = businessException.getErrorCode();
            }
//            case AccessDeniedException accessDeniedException -> {
//                logger.error("Generate Code [FORBIDDEN] on {} {}: {}",
//                        httpMethod, requestUri, accessDeniedException.getMessage(), accessDeniedException);
//                errorResponse = new CustomErrorResponse(
//                        "Acceso denegado. No tienes permisos para realizar esta operación",
//                        now
//                );
//                status = HttpStatus.FORBIDDEN;
//            }
//            case AuthenticationException authenticationException -> {
//                logger.error("Business error [UNAUTHORIZED] on {} {}: {}",
//                        httpMethod, requestUri, authenticationException.getMessage(), authenticationException);
//                errorResponse = new CustomErrorResponse(
//                        "No autorizado. Token inválido o expirado",
//                        now
//                );
//                status = HttpStatus.UNAUTHORIZED;
//            }
            case ResponseStatusException responseStatusException -> {
                logger.error("Response status error [{}] on {} {}: {}",
                        HttpStatus.valueOf(responseStatusException.getStatusCode().value()).name(), httpMethod, requestUri, responseStatusException.getReason(), responseStatusException);

                errorResponse = new CustomErrorResponse(
                        responseStatusException.getReason() != null ? responseStatusException.getReason() : "Error de validación",
                        now
                );
                status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            }
            case IllegalArgumentException ignored -> {
                logger.error("Invalid argument error [BAD_REQUEST] on {} {}: {}",
                    httpMethod, requestUri, ex.getMessage(), ex);

                errorResponse = new CustomErrorResponse(
                        ex.getMessage(),
                        now
                );
                status = HttpStatus.BAD_REQUEST;
            }
            default -> {
                logger.error("Internal server error [INTERNAL_SERVER_ERROR] on {} {}: {}",
                    httpMethod, requestUri, ex.getMessage(), ex);

                errorResponse = new CustomErrorResponse(
                        "Error interno del servidor",
                        now
                );
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }
}
