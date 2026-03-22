    package com.projecte.radioisotopo.Model;



    import java.sql.Timestamp;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.JoinColumn;
    import jakarta.persistence.ManyToOne;
    import jakarta.persistence.Table;

    @Entity
    @Table(name="Telemetria")
    @SQLDelete(sql = "UPDATE Telemetria SET activo = false WHERE id = ?")
    @SQLRestriction("activo = true")
    public class Telemetria {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "fecha_hora",nullable = false)
        private Timestamp fechaHora;

        @Column(name = "frecuencia_cardiaca")
        private Integer frecuenciaCardiaca;

        @Column(name = "pasos_acumulados",nullable = false)
        private int pasosAcumulados;

        @Column()
        private Double temperatura;

        @Column(name = "radiacion_actual", nullable = false)
        private double radiacionActual;

        @Column(nullable = false)
        private boolean activo = true;

        @ManyToOne
        @JoinColumn(name="id_tratamiento",nullable = false)
        private Tratamiento tratamiento;

        public Telemetria(Timestamp fechaHora, Integer frecuenciaCardiaca, int pasosAcumulados,
                Double temperatura, double radiacionActual, Tratamiento tratamiento) {
            this.fechaHora = fechaHora;
            this.frecuenciaCardiaca = frecuenciaCardiaca;
            this.pasosAcumulados = pasosAcumulados;
            this.temperatura = temperatura;
            this.radiacionActual = radiacionActual;
            this.tratamiento = tratamiento;
            this.activo =true;
        }

        public Telemetria() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Timestamp getFechaHora() {
            return fechaHora;
        }

        public void setFechaHora(Timestamp fechaHora) {
            this.fechaHora = fechaHora;
        }

        public Integer getFrecuenciaCardiaca() {
            return frecuenciaCardiaca;
        }

        public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
            this.frecuenciaCardiaca = frecuenciaCardiaca;
        }

        public int getPasosAcumulados() {
            return pasosAcumulados;
        }

        public void setPasosAcumulados(int pasosAcumulados) {
            this.pasosAcumulados = pasosAcumulados;
        }

        public Double getTemperatura() {
            return temperatura;
        }

        public void setTemperatura(Double temperatura) {
            this.temperatura = temperatura;
        }

        public double getRadiacionActual() {
            return radiacionActual;
        }

        public void setRadiacionActual(double radiacionActual) {
            this.radiacionActual = radiacionActual;
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
