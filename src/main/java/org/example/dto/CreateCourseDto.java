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
@Schema(name = "CreateCourseDto", description = "Datos necesarios para crear un nuevo curso")
public class CreateCourseDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar los 150 caracteres")
    @Schema(description = "Nombre del curso", example = "Programación en Java", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Las siglas son obligatorias")
    @Size(max = 10, message = "Las siglas no deben superar los 10 caracteres")
    @Schema(description = "Siglas del curso", example = "PROG-JAVA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String initials;

    @Schema(description = "Estado del curso", example = "true", defaultValue = "false")
    private Boolean status;
}
