package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CreateEnrollmentDto", description = "Datos para crear una matrícula")
public class CreateEnrollmentDto {
    @Schema(description = "Fecha de matrícula", example = "2025-08-23T19:51:42", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime dateEnrollment;

    @Schema(description = "ID del estudiante", example = "507f1f77bcf86cd799439011", requiredMode = Schema.RequiredMode.REQUIRED)
    private String studentId;

    @Schema(description = "Lista de IDs de cursos", example = "[\"507f1f77bcf86cd799439012\", \"507f1f77bcf86cd799439013\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> courseIds;

    @Schema(description = "Estado de la matrícula (opcional, por defecto true)", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean status;
}

