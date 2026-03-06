package com.projecte.radioisotopo.Model;


import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="Paciente")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String apellido;

    @Column(name = "num_telefono" ,length = 20, nullable = false,unique = true)
    private String numTelefono;


    @Column()
    private String email;

    @Column(length = 20, nullable = false,unique = true)
    private String dni;

    @Column(name = "tarjeta_sanitaria",length = 25,unique = true)
    private String tarjetaSanitaria;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column
    private Double peso;

    @Column
    private Double altura;

    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    
    @ManyToMany
    @JoinTable(
        name = "Contacto", // El nombre exacto de la tabla intermedia en tu base de datos SQL
        joinColumns = @JoinColumn(name = "id_paciente"), // La columna que apunta a ESTA clase (Paciente)
        inverseJoinColumns = @JoinColumn(name = "id_familiar") // La columna que apunta a la OTRA clase (Familiar)
    )
    private List<Familiar> familiares; // Una lista porque son "Muchos" familiares


    public Paciente(Long id, String nombre, String apellido, String numTelefono, String email, String dni,
            String tarjetaSanitaria, Date fechaNacimiento, Double peso, Double altura, Departamento departamento,
            List<Familiar> familiares) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numTelefono = numTelefono;
        this.email = email;
        this.dni = dni;
        this.tarjetaSanitaria = tarjetaSanitaria;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.altura = altura;
        this.departamento = departamento;
        this.familiares = familiares;
    }

    public Paciente() {
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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public List<Familiar> getFamiliares() {
        return familiares;
    }

    public void setFamiliares(List<Familiar> familiares) {
        this.familiares = familiares;
    }
    
}
