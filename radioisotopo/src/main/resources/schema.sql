-- TABLA DEPARTAMENTO

CREATE TABLE Departamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    centro VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- TABLA DOCTOR

CREATE TABLE Doctor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    num_colegiado VARCHAR(50),
    rol ENUM('Doctor', 'Admin') NOT NULL,
    id_departamento INT,
    CONSTRAINT fk_doctor_departamento
        FOREIGN KEY(id_departamento)
        REFERENCES Departamento(id)
        ON DELETE SET NULL
);


-- TABLA PACIENTE

CREATE TABLE Paciente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    num_telefono VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    dni VARCHAR(20) NOT NULL UNIQUE,
    tarjeta_sanitaria VARCHAR(50) UNIQUE,
    fecha_nacimiento DATE,
    peso DECIMAL(5,2),
    altura DECIMAL(5,2),
    id_departamento INT,
    CONSTRAINT fk_paciente_departamento
        FOREIGN KEY (id_departamento)
        REFERENCES Departamento(id)
        ON DELETE SET NULL
);


-- TABLA FAMILIAR

CREATE TABLE Familiar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    num_telefono VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    dni VARCHAR(20) NOT NULL UNIQUE,
    tarjeta_sanitaria VARCHAR(50) UNIQUE,
);


-- TABLA CONTACTO

CREATE TABLE Contacto (
    id_paciente INT PRIMARY KEY,
    id_familiar INT PRIMARY KEY,
    CONSTRAINT fk_paciente_contacto
        FOREIGN KEY (id_paciente)
        REFERENCES Paciente(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_familiar_contacto
        FOREIGN KEY (id_familiar)
        REFERENCES Familiar(id)
        ON DELETE CASCADE
);


-- TABLA RELOJ

CREATE TABLE Reloj (
    id INT AUTO_INCREMENT PRIMARY KEY,
    imei VARCHAR(50) UNIQUE NOT NULL,
    mac_address VARCHAR(50) UNIQUE NOT NULL,
    estado ENUM('Disponible', 'Asignado', 'En Mantenimiento') NOT NULL,
    bateria_actual INT
);


-- TABLA TRATAMIENTOS

CREATE TABLE Tratamientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_reloj INT NOT NULL,
    tipo_isotopo VARCHAR(50) NOT NULL,
    dosis_inicial DECIMAL(10,2) NOT NULL,
    fecha_administracion DATE NOT NULL,
    fecha_final_estimada DATE,
    estado ENUM('Activo', 'Finalizado', 'Cancelado') NOT NULL,
    CONSTRAINT fk_tratamiento_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES Paciente(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_tratamiento_reloj
        FOREIGN KEY (id_reloj)
        REFERENCES Reloj(id_reloj)
        ON DELETE CASCADE
);


-- TABLA TELEMETRIA

CREATE TABLE Telemetria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tratamiento INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    frecuencia_cardiaca INT,
    pasos_acumulados INT,
    temperatura DECIMAL(4,2),
    radiacion_actual DECIMAL(10,4) NOT NULL,
    CONSTRAINT fk_telemetria_tratamiento
        FOREIGN KEY (id_tratamiento)
        REFERENCES Tratamientos(id)
        ON DELETE CASCADE
);


-- TABLA ALERTA

CREATE TABLE Alerta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tratamiento INT NOT NULL,
    tipo ENUM('Alerta', 'Recomendación') NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    CONSTRAINT fk_alerta_tratamiento
        FOREIGN KEY (id_tratamiento)
        REFERENCES Tratamientos(id)
        ON DELETE CASCADE
);