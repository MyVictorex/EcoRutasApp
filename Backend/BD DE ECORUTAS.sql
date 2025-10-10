
CREATE DATABASE IF NOT EXISTS PROYECTOECORUTAS;
USE PROYECTOECORUTAS;


CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100),
  correo VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol ENUM('usuario', 'admin') DEFAULT 'usuario',
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE ruta (
  id_ruta INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  punto_inicio VARCHAR(150),
  punto_fin VARCHAR(150),
  distancia_km DECIMAL(5,2),
  tipo ENUM('bicicleta', 'scooter', 'caminata', 'carpool'),
  estado ENUM('activa', 'inactiva') DEFAULT 'activa',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  id_usuario INT,
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);


CREATE TABLE vehiculo (
  id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
  tipo ENUM('bicicleta', 'scooter'),
  codigo_qr VARCHAR(100) UNIQUE,
  disponible BOOLEAN DEFAULT TRUE,
  ubicacion_actual VARCHAR(150),
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE alquiler (
  id_alquiler INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_vehiculo INT NOT NULL,
  id_ruta INT NOT NULL,
  fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_fin DATETIME,
  costo DECIMAL(6,2),
  estado ENUM('en curso', 'finalizado') DEFAULT 'en curso',
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_vehiculo) REFERENCES vehiculo(id_vehiculo),
  FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
);


CREATE TABLE logro (
  id_logro INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100),
  descripcion TEXT,
  puntos INT
);


CREATE TABLE usuario_logro (
  id_usuario INT,
  id_logro INT,
  fecha_obtencion DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_logro),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_logro) REFERENCES logro(id_logro)
);


CREATE TABLE historial_ruta (
  id_historial INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_ruta INT NOT NULL,
  fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_fin DATETIME,
  distancia_recorrida DECIMAL(5,2),
  duracion_minutos INT,
  modo_transporte ENUM('bicicleta', 'scooter', 'caminata', 'carpool'),
  co2_ahorrado DECIMAL(6,2),
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
);


CREATE TABLE estadistica (
  id_estadistica INT AUTO_INCREMENT PRIMARY KEY,
  fecha DATE,
  total_usuarios INT,
  total_rutas INT,
  total_alquileres INT,
  co2_ahorrado DECIMAL(10,2)
);
