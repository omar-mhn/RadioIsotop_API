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
        import jakarta.persistence.Column;

        @Entity
        @Table(name ="Doctor")
        public class Doctor {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            @Column(length = 100, nullable = false)
            private String nombre;

            @Column(length = 100, nullable = false)
            private String apellido;

            @Column(length = 100, nullable = false,unique = true)
            private String email;

            @Column(name="num_colegiado",length = 50)
            private String numColegiado;

            @Enumerated(EnumType.STRING)
            @Column(nullable = false)
            private RolDoctor rol;

            @ManyToOne
            @JoinColumn(name = "id_departamento")
            private Departamento departamento;

            

            public Doctor(Long id, String nombre, String apellido, String email, String num_colegiado, RolDoctor rol,
                    Departamento departamento) {
                this.id = id;
                this.nombre = nombre;
                this.apellido = apellido;
                this.email = email;
                this.numColegiado = num_colegiado;
                this.rol = rol;
                this.departamento = departamento;
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

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getNumColegiado() {
                return numColegiado;
            }

            public void setNumColegiado(String num_colegiado) {
                this.numColegiado = num_colegiado;
            }

            public RolDoctor getRol() {
                return rol;
            }

            public void setRol(RolDoctor rol) {
                this.rol = rol;
            }

            public Departamento getDepartamento() {
                return departamento;
            }

            public void setDepartamento(Departamento departamento) {
                this.departamento = departamento;
            }
            
            


        }
