package com.projecte.radioisotopo.DTO;

import com.projecte.radioisotopo.Model.EstadoTratamiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

public record TratamientoDTO(
    @NotBlank(message = "El tipo de isotopo es obligatorio") String tipoIsotopo,
    @NotNull(message = "La dosis inicial es obligatoria") Double dosisInicial,
    @NotNull(message = "La fecha de administracion es obligatoria") Timestamp fechaAdministracion,
    Timestamp fechaFinalEstimada,
    EstadoTratamiento estadoTratamiento,
    @NotNull(message = "El ID del paciente es obligatorio") Long pacienteId,
    @NotNull(message = "El ID del reloj es obligatorio") Long relojId,
    @NotNull(message = "El ID del doctor es obligatorio") Long doctorId
) {}
