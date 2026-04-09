package com.projecte.radioisotopo.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DepartamentoDTO(
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotBlank(message = "El centro es obligatorio") String centro,
    @NotBlank(message = "La ubicacion es obligatoria") String ubicacion,
    @NotBlank(message = "El email es obligatorio") @Email(message = "Debe ser un email valido") String email
) {}
