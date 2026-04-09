package com.projecte.radioisotopo.Model;


import java.sql.Timestamp;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Alerta")
@SQLDelete(sql = "UPDATE Alerta SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String mensaje;

    @Column(name ="fecha_hora", nullable = false)
    private Timestamp fechaHora;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name="id_tratamiento",nullable = false)
    private Tratamiento tratamiento;

    public Alerta(Long id, Tipo tipo, String mensaje, Timestamp fechaHora, Tratamiento tratamiento) {
        this.id = id;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
        this.tratamiento = tratamiento;
        this.activo = true;
    }

    public Alerta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
