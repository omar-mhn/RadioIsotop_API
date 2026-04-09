package com.projecte.radioisotopo.Model;


import java.sql.Date;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
@SQLDelete(sql = "UPDATE Paciente SET activo = false WHERE id = ?")
@SQLRestriction("activo = true")
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

    @Column(name = "num_documento", length = 20, nullable = false, unique = true)
    private String numDocumento; 

    @Column(name = "tipo_documento")
    private String tipoDocumento;
    
    @Column(name = "tarjeta_sanitaria",length = 25,unique = true)
    private String tarjetaSanitaria;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Column(name = "foto_perfil") 
    private String fotoPerfil;

    @Column(name = "id_usuario", unique = true, nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private boolean activo = true;

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


   

    

    public Paciente(String nombre, String apellido, String numTelefono, String numDocumento,
            String tipoDocumento, String tarjetaSanitaria, Date fechaNacimiento, String fotoPerfil, Double peso,
            Double altura, Departamento departamento, List<Familiar> familiares) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numTelefono = numTelefono;
        this.numDocumento = numDocumento;
        this.tipoDocumento = tipoDocumento;
        this.tarjetaSanitaria = tarjetaSanitaria;
        this.fechaNacimiento = fechaNacimiento;
        this.fotoPerfil = fotoPerfil;
        this.peso = peso;
        this.altura = altura;
        this.departamento = departamento;
        this.familiares = familiares;
        this.activo = true;
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

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    
    
}
