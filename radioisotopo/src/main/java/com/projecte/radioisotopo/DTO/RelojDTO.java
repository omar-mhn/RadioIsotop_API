package com.projecte.radioisotopo.DTO;

import com.projecte.radioisotopo.Model.EstadoReloj;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RelojDTO(
    @NotBlank(message = "El IMEI es obligatorio") String imei,
    @NotBlank(message = "La direccion MAC es obligatoria") String macAddress,
    String idAndroid,
    @NotNull(message = "El estado del reloj es obligatorio") EstadoReloj estadoReloj,
    int bateriaActual
) {}
