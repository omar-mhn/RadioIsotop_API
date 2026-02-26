package com.projecte.radioisotopo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "Departamento")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false,unique = true)
    private String nombre; 

    @Column(length = 50, nullable = false)
    private String centro;

    @Column(length = 50, nullable = false, unique = true)
    private String ubicacion;

    @Column(length = 100, nullable = false,unique = true)
    private String email;

    

    public Departamento() {
    }

    public Departamento(Long id, String nombre, String centro, String ubicacion, String email) {
        this.id = id;
        this.nombre = nombre;
        this.centro = centro;
        this.ubicacion = ubicacion;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
