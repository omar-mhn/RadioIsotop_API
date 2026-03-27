package com.projecte.radioisotopo.DTO;

import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;

public record TelemetriaDTO(
    @NotNull(message = "La fecha y hora son obligatorias") Timestamp fechaHora,
    Integer frecuenciaCardiaca,
    @NotNull(message = "Los pasos acumulados son obligatorios") Integer pasosAcumulados,
    Double temperatura,
    @NotNull(message = "La radiacion actual es obligatoria") Double radiacionActual,
    @NotNull(message = "El ID del tratamiento es obligatorio") Long tratamientoId
) {}
