package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.Course;
import org.example.model.Student;
import org.springframework.data.annotation.Reference;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EnrollmentDto", description = "Información completa de la matricula")
public class EnrollmentDto {
    @Schema(description = "ID único de la matrícula", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "Fecha de matrícula", example = "2025-08-23T19:51:42", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dateEnrollment;

    @Schema(description = "Estudiante matriculado", requiredMode = Schema.RequiredMode.REQUIRED)
    @Reference(value = Student.class)
    private Student student;

    @Schema(description = "Cursos matriculados", requiredMode = Schema.RequiredMode.REQUIRED)
    @Reference(value = Course.class)
    private List<Course> courses;

    @Schema(description = "Estado de la matrícula (activo/inactivo)", example = "false")
    private Boolean status;
}
