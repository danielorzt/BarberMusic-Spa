package com.sena.barberspa.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El correo electrónico no puede estar vacío") @Email(message = "El formato del correo electrónico no es válido") String email,
        @NotBlank(message = "La contraseña no puede estar vacía") String password) {
}