package org.example.dto;

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
@Schema(name = "LoginDto", description = "Datos necesarios para iniciar sesión")
public class LoginDto {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 150, message = "El nombre de usuario no debe superar los 150 caracteres")
    @Schema(description = "Nombre de usuario", example = "usuario12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 10, message = "La contraseña no debe superar los 10 caracteres")
    @Schema(description = "Contraseña del usuario", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}