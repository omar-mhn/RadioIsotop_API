package com.projecte.radioisotopo.DTO;

import jakarta.validation.constraints.NotBlank;

public record FamiliarDTO(
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotBlank(message = "El apellido es obligatorio") String apellido,
    @NotBlank(message = "El numero de telefono es obligatorio") String numTelefono,
    @NotBlank(message = "El numero de documento es obligatorio") String numDocumento,
    String tipoDocumento,
    String tarjetaSanitaria
) {}
