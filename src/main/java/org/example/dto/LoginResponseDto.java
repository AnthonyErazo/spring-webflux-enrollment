package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDto(@JsonProperty(value = "access_token") String token) {
}
