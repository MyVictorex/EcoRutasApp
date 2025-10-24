Drop DATABASE IF EXISTS PROYECTOECORUTAS;
Create Database PROYECTOECORUTAS;
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
  tipo ENUM('BICICLETA', 'SCOOTER', 'MONOPATIN_ELECTRICO', 'SEGWAY', 'CARPOOL'),
  estado ENUM('activa', 'inactiva') DEFAULT 'activa',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  id_usuario INT,
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE vehiculo (
  id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
  tipo ENUM('BICICLETA', 'SCOOTER', 'CARPOOL', 'MONOPATIN_ELECTRICO', 'SEGWAY'),
  codigo_qr VARCHAR(100) UNIQUE,
  disponible BOOLEAN DEFAULT TRUE,
  ubicacion_actual VARCHAR(150),
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
  latitud DECIMAL(10,6),
  longitud DECIMAL(10,6)
);



CREATE TABLE alquiler (
  id_alquiler INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_vehiculo INT NOT NULL,
  id_ruta INT NOT NULL,
  fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
  fecha_fin DATETIME,
  costo DECIMAL(6,2),
  estado ENUM('EN_CURSO', 'FINALIZADO') DEFAULT 'EN_CURSO',
  FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY (id_vehiculo) REFERENCES vehiculo(id_vehiculo),
  FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
);
ALTER TABLE alquiler MODIFY estado ENUM('EN_CURSO', 'FINALIZADO') DEFAULT 'EN_CURSO';


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
  modo_transporte ENUM(
    'BICICLETA', 
    'SCOOTER', 
    'MONOPATIN_ELECTRICO', 
    'SEGWAY', 
    'CARPOOL'
  ),
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

select*from usuario;
INSERT INTO usuario (nombre, apellido, correo, password, rol) VALUES
('Carlos', 'Ramírez', 'carlos@ecorutas.com', '123456', 'admin'),
('María', 'Torres', 'maria@ecorutas.com', '123456', 'usuario'),
('José', 'Quispe', 'jose@ecorutas.com', '123456', 'usuario'),
('Lucía', 'Pérez', 'lucia@ecorutas.com', '123456', 'usuario'),
('Ana', 'Gómez', 'ana@ecorutas.com', '123456', 'usuario');

INSERT INTO usuario (nombre, apellido, correo, password, rol, fecha_registro) VALUES
('Diego', 'Fernández', 'diego@ecorutas.com', '123456', 'usuario', '2025-10-06 10:30:00'),
('Valeria', 'Campos', 'valeria@ecorutas.com', '123456', 'usuario', '2025-10-07 09:45:00');

INSERT INTO ruta (nombre, descripcion, punto_inicio, punto_fin, distancia_km, tipo, estado, id_usuario) VALUES
('Ruta Central', 'Recorrido principal por el centro de Lima', 'Plaza Mayor', 'Parque Kennedy', 8.5, 'BICICLETA', 'activa', 1),
('Ruta Verde', 'Ruta ecológica con árboles y poco tráfico', 'San Borja', 'Surco', 5.2, 'BICICLETA', 'activa', 2),
('Costa Ride', 'Ruta frente al mar por el circuito de playas', 'Miraflores', 'Chorrillos', 6.8, 'SCOOTER', 'activa', 3),
('Reto Urbano', 'Ruta con pendiente ideal para entrenamiento', 'Barranco', 'San Isidro', 7.0, 'BICICLETA', 'activa', 4),
('EcoCamino', 'Ruta peatonal con zonas verdes', 'La Molina', 'Camacho', 3.4, 'SCOOTER', 'activa', 5);



INSERT INTO ruta (nombre, descripcion, punto_inicio, punto_fin, distancia_km, tipo, estado, fecha_creacion, id_usuario) VALUES
('Ruta Central', 'Recorrido principal por el centro de Lima', 'Plaza Mayor', 'Parque Kennedy', 8.5, 'BICICLETA', 'activa', '2025-10-01 08:30:00', 1),
('Ruta Verde', 'Ruta ecológica con árboles y poco tráfico', 'San Borja', 'Surco', 5.2, 'BICICLETA', 'activa', '2025-10-02 09:15:00', 2);

select*from Ruta;

INSERT INTO vehiculo (tipo, codigo_qr, disponible, ubicacion_actual) VALUES
('BICICLETA', 'QRB-001', TRUE, 'Plaza Mayor'),
('BICICLETA', 'QRB-002', TRUE, 'San Borja'),
('SCOOTER', 'QRS-001', TRUE, 'Miraflores'),
('BICICLETA', 'QRB-003', FALSE, 'Barranco'),
('SCOOTER', 'QRS-002', TRUE, 'La Molina');


INSERT INTO vehiculo (tipo, codigo_qr, disponible, ubicacion_actual, fecha_registro) VALUES
('BICICLETA', 'QRB-006', TRUE, 'Plaza Mayor', '2025-10-01 08:00:00'),
('SCOOTER', 'QRS-006', TRUE, 'Miraflores', '2025-10-02 09:45:00');


INSERT INTO alquiler (id_usuario, id_vehiculo, id_ruta, fecha_inicio, fecha_fin, costo, estado) VALUES
(2, 1, 1, '2025-10-01 08:30:00', '2025-10-01 09:10:00', 7.50, 'FINALIZADO'),
(3, 2, 2, '2025-10-02 10:00:00', NULL, 0.00, 'EN_CURSO'),
(4, 3, 3, '2025-10-03 14:15:00', '2025-10-03 14:50:00', 6.00, 'FINALIZADO'),
(5, 4, 4, '2025-10-04 09:00:00', NULL, 0.00, 'EN_CURSO'),
(2, 5, 5, '2025-10-05 07:45:00', '2025-10-05 08:20:00', 4.80, 'FINALIZADO');



INSERT INTO logro (nombre, descripcion, puntos) VALUES
('Primer Viaje', 'Completa tu primer recorrido', 10),
('Ciclista Verde', 'Realiza 5 viajes en bicicleta', 20),
('EcoRider', 'Ahorra más de 2kg de CO₂', 25),
('Usuario Frecuente', 'Realiza más de 10 recorridos', 30),
('Compromiso Verde', 'Participa durante 1 mes seguido', 40);

INSERT INTO usuario_logro (id_usuario, id_logro) VALUES
(2, 1),
(2, 2),
(3, 1),
(4, 3),
(5, 1);

INSERT INTO historial_ruta (id_usuario, id_ruta, fecha_inicio, fecha_fin, distancia_recorrida, duracion_minutos, modo_transporte, co2_ahorrado) VALUES
(2, 1, '2025-09-28 07:00:00', '2025-09-28 07:40:00', 8.5, 40, 'BICICLETA', 1.2),
(3, 2, '2025-09-29 08:15:00', '2025-09-29 08:50:00', 5.2, 35, 'BICICLETA', 0.8),
(4, 3, '2025-09-30 09:00:00', '2025-09-30 09:45:00', 6.8, 45, 'SCOOTER', 0.6),
(5, 4, '2025-10-01 10:10:00', '2025-10-01 10:50:00', 7.0, 40, 'BICICLETA', 1.0),
(2, 5, '2025-10-02 11:00:00', '2025-10-02 11:30:00', 3.4, 30, 'SCOOTER', 0.2);


INSERT INTO estadistica (fecha, total_usuarios, total_rutas, total_alquileres, co2_ahorrado) VALUES
('2025-09-30', 5, 5, 5, 3.8),
('2025-10-01', 5, 5, 6, 4.1),
('2025-10-02', 5, 5, 7, 4.7),
('2025-10-03', 5, 5, 8, 5.0),
('2025-10-04', 5, 5, 9, 5.5);
