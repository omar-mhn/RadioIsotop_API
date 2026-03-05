package com.projecte.radioisotopo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name= "Reloj")
public class Reloj {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=50,unique=true,nullable=false)
    private String imei;

    @Column(name = "mac_address",length = 50, unique = true, nullable = false)
    private String macAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reloj")
    private EstadoReloj estado;

    @Column(name = "bateria_actual")
    private int bateriaActual;

    public Reloj(Long id, String imei, String macAddress, EstadoReloj estadoReloj, int bateriaActual) {
        this.id = id;
        this.imei = imei;
        this.macAddress = macAddress;
        this.estado = estadoReloj;
        this.bateriaActual = bateriaActual;
    }

    public Reloj() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public EstadoReloj getEstadoReloj() {
        return estado;
    }

    public void setEstadoReoj(EstadoReloj estado) {
        this.estado = estado;
    }

    public int getBateriaActual() {
        return bateriaActual;
    }

    public void setBateriaActual(int bateriaActual) {
        this.bateriaActual = bateriaActual;
    }

    
}
