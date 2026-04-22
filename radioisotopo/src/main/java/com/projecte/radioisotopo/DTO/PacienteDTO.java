package com.projecte.radioisotopo.DTO;

import jakarta.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.List;

public record PacienteDTO(
    @NotBlank(message = "El nombre es obligatorio") String nombre,
    @NotBlank(message = "El apellido es obligatorio") String apellido,
    @NotBlank(message = "El numero de telefono es obligatorio") String numTelefono,
    @NotBlank(message = "El numero de documento es obligatorio") String numDocumento,
    String tipoDocumento,
    String tarjetaSanitaria,
    Date fechaNacimiento,
    Double peso,
    Double altura,
    Long departamentoId,
    List<Long> familiarIds
) {}
