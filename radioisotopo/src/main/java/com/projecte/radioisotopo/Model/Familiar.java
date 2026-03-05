package com.projecte.radioisotopo.Model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Familiar")
public class Familiar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String apellido;

    @Column(name="num_telefono",length = 20, nullable = false,unique = true)
    private String numTelefono;


    @Column(length = 100)
    private String email;

    @Column(length = 20, nullable = false,unique = true)
    private String dni;

    @Column(name="tarjeta_sanitaria",length = 50,unique = true)
    private String tarjetaSanitaria;

    // Le decimos que mire la variable "familiares" que acabamos de crear en la clase Paciente,
    // porque allí ya están todas las reglas de la tabla "Contacto".

    @ManyToMany(mappedBy = "familiares")
    private List<Paciente> pacientes; // Una lista porque son "Muchos" pacientes

    public Familiar(Long id, String nombre, String apellido, String numTelefono, String email, String dni,
            String tarjetaSanitaria) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numTelefono = numTelefono;
        this.email = email;
        this.dni = dni;
        this.tarjetaSanitaria = tarjetaSanitaria;
    }

    public Familiar() {
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTarjetaSanitaria() {
        return tarjetaSanitaria;
    }

    public void setTarjetaSanitaria(String tarjetaSanitaria) {
        this.tarjetaSanitaria = tarjetaSanitaria;
    }
    
}
