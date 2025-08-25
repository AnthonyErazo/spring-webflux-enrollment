package org.example.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RegisterDto", description = "Datos necesarios para registrar un nuevo usuario")
public class RegisterDto {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre de usuario debe tener entre 5 y 100 caracteres")
    @Schema(description = "Nombre de usuario", example = "usuario12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 5, max = 100, message = "La contraseña debe tener entre 5 y 100 caracteres")
    @Schema(description = "Contraseña del usuario", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Estado del usuario", example = "true", defaultValue = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean status;

    @Schema(description = "Roles del usuario", example = "[ \"550e8400-e29b-41d4-a716-446655440000\", \"550e8400-e29b-41d4-a716-446655440001\" ]")
    private List<String> roles;
}