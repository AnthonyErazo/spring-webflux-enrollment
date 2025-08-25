package org.example.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "enrollments")
public class Enrollment {
    @Id
    @UUID
    @EqualsAndHashCode.Include
    private String id;

    @Field("fechaMatricula")
    @Builder.Default
    private LocalDateTime dateEnrollment = LocalDateTime.now();

    @NotNull
    @EqualsAndHashCode.Include
    @Field("estudiante")
    private Student student;

    @NotEmpty
    @Field("cursos")
    private List<Course> courses;

    @Field("estado")
    @Builder.Default
    private Boolean status = true;

    public boolean agregarCurso(Course curso) {
        if (!courses.contains(curso)) {
            courses.add(curso);
            return true;
        }
        return false;
    }

    public boolean quitarCurso(Course curso) {
        return courses.remove(curso);
    }
}
