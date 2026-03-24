        package com.projecte.radioisotopo.Model;
        import jakarta.persistence.Entity;
        import jakarta.persistence.EnumType;
        import jakarta.persistence.Enumerated;
        import jakarta.persistence.Table;
        import jakarta.persistence.Id;
        import jakarta.persistence.JoinColumn;
        import jakarta.persistence.ManyToOne;
        import jakarta.persistence.GeneratedValue;
        import jakarta.persistence.GenerationType;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;

        @Entity
        @Table(name ="Doctor")
        @SQLDelete(sql = "UPDATE Doctor SET activo = false WHERE id = ?")
        @SQLRestriction("activo = true")
        public class Doctor {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            @Column(length = 100, nullable = false)
            private String nombre;

            @Column(length = 100, nullable = false)
            private String apellido;

            @Column(name="num_colegiado",length = 50)
            private String numColegiado;

            @Column(name = "foto_perfil") 
            private String fotoPerfil;

            @Column(nullable = false)
            private boolean activo = true;

            @ManyToOne
            @JoinColumn(name = "id_departamento")
            private Departamento departamento;

            

            

            public Doctor( String nombre, String apellido, String email, String numColegiado,
                    String fotoPerfil, Departamento departamento) {
                this.nombre = nombre;
                this.apellido = apellido;
                this.numColegiado = numColegiado;
                this.fotoPerfil = fotoPerfil;
                this.departamento = departamento;
                this.activo = true;
            }
            public Doctor() {
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

            public String getNumColegiado() {
                return numColegiado;
            }

            public void setNumColegiado(String num_colegiado) {
                this.numColegiado = num_colegiado;
            }
            public Departamento getDepartamento() {
                return departamento;
            }

            public void setDepartamento(Departamento departamento) {
                this.departamento = departamento;
            }

            public boolean isActivo() {
                return activo;
            }

            public void setActivo(boolean activo) {
                this.activo = activo;
            }
            public String getFotoPerfil() {
                return fotoPerfil;
            }
            public void setFotoPerfil(String fotoPerfil) {
                this.fotoPerfil = fotoPerfil;
            }
            
            


        }
