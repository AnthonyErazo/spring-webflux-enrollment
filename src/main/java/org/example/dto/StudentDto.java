package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "StudentDto", description = "Información completa del estudiante")
public class StudentDto {
    @Schema(description = "ID único del estudiante", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "Nombres del estudiante", example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String names;

    @Schema(description = "Apellidos del estudiante", example = "Pérez García", requiredMode = Schema.RequiredMode.REQUIRED)
    private String surnames;

    @Schema(description = "DNI del estudiante", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dni;

    @Schema(description = "Edad del estudiante", example = "25", minimum = "1", maximum = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer age;
}
