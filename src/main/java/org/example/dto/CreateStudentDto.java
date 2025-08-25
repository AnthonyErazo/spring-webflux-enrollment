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
@Schema(name = "CreateStudentDto", description = "Datos necesarios para crear un nuevo estudiante")
public class CreateStudentDto {
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no deben superar los 100 caracteres")
    @Schema(description = "Nombres del estudiante", example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String names;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no deben superar los 100 caracteres")
    @Schema(description = "Apellidos del estudiante", example = "Pérez García", requiredMode = Schema.RequiredMode.REQUIRED)
    private String surnames;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    @Pattern(regexp = "\\d+", message = "El DNI debe contener solo números")
    @Schema(description = "DNI del estudiante (8 dígitos)", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dni;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Max(value = 120, message = "La edad debe ser menor a 120")
    @Schema(description = "Edad del estudiante", example = "25", minimum = "1", maximum = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer age;
}
