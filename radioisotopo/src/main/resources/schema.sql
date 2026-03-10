-- TABLA DEPARTAMENTO

CREATE TABLE IF NOT EXISTS Departamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    centro VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- TABLA DOCTOR

CREATE TABLE IF NOT EXISTS Doctor (
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

CREATE TABLE IF NOT EXISTS Paciente (
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

CREATE TABLE IF NOT EXISTS Familiar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    num_telefono VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    dni VARCHAR(20) NOT NULL UNIQUE,
    tarjeta_sanitaria VARCHAR(50) UNIQUE
);


-- TABLA CONTACTO

CREATE TABLE IF NOT EXISTS Contacto (
    id_paciente INT,
    id_familiar INT,
    PRIMARY KEY (id_paciente, id_familiar), 
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

CREATE TABLE IF NOT EXISTS Reloj (
    id INT AUTO_INCREMENT PRIMARY KEY,
    imei VARCHAR(50) UNIQUE NOT NULL,
    mac_address VARCHAR(50) UNIQUE NOT NULL,
    estado_reloj ENUM('Disponible', 'Asignado', 'En_Mantenimiento') NOT NULL,
    bateria_actual INT NOT NULL
);


-- TABLA TRATAMIENTOS

CREATE TABLE IF NOT EXISTS Tratamientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_doctor INT NOT NULL,
    id_reloj INT NOT NULL,
    tipo_isotopo VARCHAR(50) NOT NULL,
    dosis_inicial DECIMAL(10,2) NOT NULL,
    fecha_administracion DATE NOT NULL,
    fecha_final_estimada DATE,
    estado_tratamiento ENUM('Activo', 'Finalizado', 'Cancelado') NOT NULL,
    CONSTRAINT fk_tratamiento_paciente
        FOREIGN KEY (id_paciente)
        REFERENCES Paciente(id)
        ON DELETE CASCADE,
        CONSTRAINT fk_tratamiento_doctor
        FOREIGN KEY (id_doctor)
        REFERENCES Doctor(id)   -- Relación con la tabla Doctor
        ON DELETE CASCADE,
    CONSTRAINT fk_tratamiento_reloj
        FOREIGN KEY (id_reloj)
        REFERENCES Reloj(id)
        ON DELETE CASCADE
);


-- TABLA TELEMETRIA

CREATE TABLE IF NOT EXISTS Telemetria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tratamiento INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    frecuencia_cardiaca INT,
    pasos_acumulados INT NOT NULL,
    temperatura DECIMAL(4,2),
    radiacion_actual DECIMAL(10,4) NOT NULL,
    CONSTRAINT fk_telemetria_tratamiento
        FOREIGN KEY (id_tratamiento)
        REFERENCES Tratamientos(id)
        ON DELETE CASCADE
);


-- TABLA ALERTA

CREATE TABLE IF NOT EXISTS Alerta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_tratamiento INT NOT NULL,
    tipo ENUM('Alerta', 'Recomendacion') NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    CONSTRAINT fk_alerta_tratamiento
        FOREIGN KEY (id_tratamiento)
        REFERENCES Tratamientos(id)
        ON DELETE CASCADE
);