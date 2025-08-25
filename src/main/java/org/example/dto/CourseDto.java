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
@Schema(name = "CourseDto", description = "Información completa del curso")
public class CourseDto {
    @Schema(description = "ID único del curso", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "Nombre del curso", example = "Programación en Java", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Siglas del curso", example = "PROG-JAVA", requiredMode = Schema.RequiredMode.REQUIRED)
    private String initials;

    @Schema(description = "Estado del curso", example = "true")
    private Boolean status;
}
