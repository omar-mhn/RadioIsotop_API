package com.projecte.radioisotopo.Model;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
@SQLDelete(sql = "UPDATE Reloj SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
public class Reloj {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=50,unique=true,nullable=false)
    private String imei;

    @Column(name = "mac_address",length = 50, unique = true, nullable = false)
    private String macAddress;

    @Column(name = "id_android", length = 100, unique = true)
    private String idAndroid;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reloj")
    private EstadoReloj estadoReloj;

    @Column(name = "bateria_actual")
    private int bateriaActual;

    @Column(nullable = false)
    private boolean activo = true;

    public Reloj(String imei, String macAddress, String idAndroid, EstadoReloj estadoReloj, int bateriaActual) {
        
        this.imei = imei;
        this.macAddress = macAddress;
        this.idAndroid = idAndroid;
        this.estadoReloj = estadoReloj;
        this.bateriaActual = bateriaActual;
        this.activo =true;
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

    public String getIdAndroid() {
        return idAndroid;
    }

    public void setIdAndroid(String idAndroid) {
        this.idAndroid = idAndroid;
    }

    public EstadoReloj getEstadoReloj() {
        return estadoReloj;
    }

    public void setEstadoReloj(EstadoReloj estado) {
        this.estadoReloj = estado;
    }

    public int getBateriaActual() {
        return bateriaActual;
    }

    public void setBateriaActual(int bateriaActual) {
        this.bateriaActual = bateriaActual;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    
    
}
