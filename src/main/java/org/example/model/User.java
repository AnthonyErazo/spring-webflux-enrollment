package org.example.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "users")
public class User{

    @Id
    @UUID
    @EqualsAndHashCode.Include
    private String id;

    @Field("usuario")
    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 5, max = 100, message = "El usuario no debe superar los 150 caracteres")
    private String username;

    @Field("contrasena")
    @NotBlank(message = "La contraseña es obligatorio")
    @Size(min = 5, max = 100, message = "La contraseña no debe superar los 150 caracteres")
    private String password;

    @Field("roles")
    private List<Role> roles;

    @Field("status")
    private boolean status;
}
