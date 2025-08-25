package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "UpdateCourseDto", description = "Datos para actualizar un curso (todos los campos son opcionales)")
public class UpdateCourseDto {
    @Size(max = 150, message = "El nombre no debe superar los 150 caracteres")
    @Schema(description = "Nombre del curso", example = "Programación en Java", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Size(max = 10, message = "Las siglas no deben superar los 10 caracteres")
    @Schema(description = "Siglas del curso", example = "PROG-JAVA", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String initials;

    @Schema(description = "Estado del curso", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean status;
}
