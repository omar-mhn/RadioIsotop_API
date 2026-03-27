package com.projecte.radioisotopo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorDTO(
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotBlank(message = "El apellido es obligatorio") String apellido,
    String numColegiado,
    String fotoPerfil,
    @NotNull(message = "El ID del departamento es obligatorio") Long departamentoId
) {}
