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
@Schema(name = "UpdateEnrollmentDto", description = "Datos para actualizar una matrícula")
public class UpdateEnrollmentDto {
    @Schema(description = "Fecha de matrícula", example = "2025-08-23T19:51:42")
    private LocalDateTime dateEnrollment;

    @Schema(description = "Lista de IDs de cursos (opcional)", example = "[\"507f1f77bcf86cd799439012\", \"507f1f77bcf86cd799439013\"]")
    private List<String> courseIds;

    @Schema(description = "Estado de la matrícula (opcional)", example = "true")
    private Boolean status;
}

