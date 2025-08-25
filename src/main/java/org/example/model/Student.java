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
@Document(collection = "students")
public class Student {
    @Id
    @UUID
    @EqualsAndHashCode.Include
    private String id;

    @Field("nombres")
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no deben superar los 100 caracteres")
    private String names;

    @Field("apellidos")
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no deben superar los 100 caracteres")
    private String surnames;

    @Field("dni")
    @NotBlank(message = "El DNI es obligatorio")
    @EqualsAndHashCode.Include
    @Indexed(unique = true)
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 digitos")
    @Pattern(regexp = "\\d+", message = "El DNI debe contener solo números")
    private String dni;

    @Field("edad")
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 1, message = "La edad debe ser mayor a 0")
    @Max(value = 120, message = "La edad debe ser menor a 120")
    private Integer age;
}
