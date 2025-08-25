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
@Schema(name = "RoleDto", description = "Datos necesarios para registrar un nuevo rol")
public class RoleDto {
    @Schema(description = "ID del rol", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "Nombre del rol", example = "ROLE_USER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}