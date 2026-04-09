package com.projecte.radioisotopo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlertaDTO(
    @NotNull(message = "El tipo es obligatorio") String tipo,
    @NotBlank(message = "El mensaje es obligatorio") String mensaje,
    @NotNull(message = "El tratamiento es obligatorio") Long tratamientoId
) {}
