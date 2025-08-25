package org.example.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "courses")
public class Course {
    @Id
    @UUID
    @EqualsAndHashCode.Include
    private String id;

    @Field("nombre")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar los 150 caracteres")
    private String name;

    @Field("siglas")
    @NotBlank(message = "Las siglas son obligatorias")
    @Size(max = 10, message = "Las siglas no deben superar los 10 caracteres")
    @Indexed(unique = true)
    @EqualsAndHashCode.Include
    private String initials;

    @Field("estado")
    @Builder.Default
    private Boolean status = false;
}
