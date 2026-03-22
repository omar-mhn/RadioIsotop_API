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
    @Table(name="Tratamientos")
    @SQLDelete(sql = "UPDATE Tratamientos SET activo = false WHERE id = ?")
    @SQLRestriction("activo = true")
    public class Tratamiento {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "tipo_isotopo", length = 50, nullable = false)
        private String tipoIsotopo;

        @Column(name = "dosis_inicial",nullable = false)
        private Double dosisInicial;

        @Column(name="fecha_administracion",nullable = false)
        private Timestamp fechaAdministracion;

        @Column(name = "fecha_final_estimada")
        private Timestamp fechaFinalEstimada;

        @Enumerated(EnumType.STRING)
        @Column(name = "estado_tratamiento")
        private EstadoTratamiento estadoTratamiento;

        @Column(nullable = false)
        private boolean activo = true;

        @ManyToOne()
        @JoinColumn(name = "id_paciente",nullable = false)
        private Paciente paciente;

        @ManyToOne()
        @JoinColumn(name = "id_reloj",nullable = false)
        private Reloj reloj;

        @ManyToOne
        @JoinColumn(name = "id_doctor", nullable = false)
        private Doctor doctor; // El médico que supervisa el tratamiento

        

        public Tratamiento(String tipoIsotopo, Double dosisInicial, Timestamp fechaAdministracion,
                Timestamp fechaFinalEstimada, EstadoTratamiento estadoTratamiento, Paciente paciente, Reloj reloj,
                Doctor doctor) {
            this.tipoIsotopo = tipoIsotopo;
            this.dosisInicial = dosisInicial;
            this.fechaAdministracion = fechaAdministracion;
            this.fechaFinalEstimada = fechaFinalEstimada;
            this.estadoTratamiento = estadoTratamiento;
            this.paciente = paciente;
            this.reloj = reloj;
            this.doctor = doctor;
            this.activo = true;
        }

        public Tratamiento() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTipoIsotopo() {
            return tipoIsotopo;
        }

        public void setTipoIsotopo(String tipoIsotopo) {
            this.tipoIsotopo = tipoIsotopo;
        }

        public Double getDosisInicial() {
            return dosisInicial;
        }

        public void setDosisInicial(Double dosisInicial) {
            this.dosisInicial = dosisInicial;
        }

        public Timestamp getFechaAdministracion() {
            return fechaAdministracion;
        }

        public void setFechaAdministracion(Timestamp fechaAdministracion) {
            this.fechaAdministracion = fechaAdministracion;
        }

        public Timestamp getFechaFinalEstimada() {
            return fechaFinalEstimada;
        }

        public void setFechaFinalEstimada(Timestamp fechaFinalEstimada) {
            this.fechaFinalEstimada = fechaFinalEstimada;
        }

        public EstadoTratamiento getEstadoTratamiento() {
            return estadoTratamiento;
        }

        public void setEstadoTratamiento(EstadoTratamiento estadoTratamiento) {
            this.estadoTratamiento = estadoTratamiento;
        }

        public Paciente getPaciente() {
            return paciente;
        }

        public void setPaciente(Paciente paciente) {
            this.paciente = paciente;
        }

        public Reloj getReloj() {
            return reloj;
        }

        public void setReloj(Reloj reloj) {
            this.reloj = reloj;
        }

        public Doctor getDoctor() {
            return doctor;
        }

        public void setDoctor(Doctor doctor) {
            this.doctor = doctor;
        }

        public boolean isActivo() {
            return activo;
        }

        public void setActivo(boolean activo) {
            this.activo = activo;
        }
        
        
        
    }
