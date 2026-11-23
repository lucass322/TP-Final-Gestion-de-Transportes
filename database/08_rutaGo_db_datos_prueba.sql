USE rutaGO_db;
SET SQL_SAFE_UPDATES = 0;

-- =========================================
-- CONTRASEÑA DE PRUEBA (hash BCrypt de "1234")
-- =========================================
SET @pass = '$2a$12$BRIgbcpNmFJHcgYKTcGuvuhB367vsbTUcNQj2C8jf9Tx7bJvi5uIq';

-- =========================================
-- ROLES (se cargan solo si no existen)
-- =========================================
INSERT IGNORE INTO roles (nombreRol) VALUES ('ROOT'), ('Empleado'), ('Cliente');

SET @rolEmpleado = (SELECT idRol FROM roles WHERE nombreRol = 'Empleado');
SET @rolCliente  = (SELECT idRol FROM roles WHERE nombreRol = 'Cliente');

-- =========================================
-- LIMPIEZA DE DATOS DE PRUEBA ANTERIORES
-- (incluye los de la versión vieja de 4 usuarios)
-- =========================================
DROP TEMPORARY TABLE IF EXISTS tmp_vehiculos, tmp_usuarios;

CREATE TEMPORARY TABLE tmp_vehiculos AS
SELECT idVehiculo FROM vehiculos
WHERE patente LIKE 'AE1__AA' OR patente LIKE 'LV-A__' OR patente LIKE 'TR-0__'
   OR patente IN ('AE123BC', 'AF456DE', 'LV-ABC', 'LV-XYZ');

CREATE TEMPORARY TABLE tmp_usuarios AS
SELECT u.idUsuario FROM usuarios u
JOIN personas p ON p.idPersona = u.idPersona
WHERE p.dni LIKE '300000__' OR p.dni LIKE '400000__'
   OR p.dni IN ('38123456', '35987654', '42111222', '40333444');

DELETE ar FROM asientos_reserva ar
JOIN reservas r ON r.idReserva = ar.idReserva
JOIN viajes v ON v.idViaje = r.idViaje
JOIN tmp_vehiculos t ON t.idVehiculo = v.idVehiculo;

DELETE ar FROM asientos_reserva ar
JOIN reservas r ON r.idReserva = ar.idReserva
JOIN pasajeros pa ON pa.idPasajero = r.idPasajero
JOIN tmp_usuarios t ON t.idUsuario = pa.idUsuario;

DELETE r FROM reservas r
JOIN viajes v ON v.idViaje = r.idViaje
JOIN tmp_vehiculos t ON t.idVehiculo = v.idVehiculo;

DELETE r FROM reservas r
JOIN pasajeros pa ON pa.idPasajero = r.idPasajero
JOIN tmp_usuarios t ON t.idUsuario = pa.idUsuario;

DELETE v  FROM viajes v      JOIN tmp_vehiculos t ON t.idVehiculo = v.idVehiculo;
DELETE c  FROM colectivos c  JOIN tmp_vehiculos t ON t.idVehiculo = c.idVehiculo;
DELETE a  FROM aviones a     JOIN tmp_vehiculos t ON t.idVehiculo = a.idVehiculo;
DELETE tr FROM trenes tr     JOIN tmp_vehiculos t ON t.idVehiculo = tr.idVehiculo;
DELETE ve FROM vehiculos ve  JOIN tmp_vehiculos t ON t.idVehiculo = ve.idVehiculo;

DELETE FROM recorridos WHERE (origen, destino) IN (
    ('Mar del Plata', 'Buenos Aires'), ('Buenos Aires', 'Mar del Plata'),
    ('Buenos Aires', 'Córdoba'),       ('Córdoba', 'Buenos Aires'),
    ('Buenos Aires', 'Rosario'),       ('Rosario', 'Buenos Aires'),
    ('Buenos Aires', 'Mendoza'),       ('Mendoza', 'Buenos Aires'),
    ('Buenos Aires', 'Bariloche'),     ('Bariloche', 'Buenos Aires'),
    ('Mar del Plata', 'Bahía Blanca'), ('Córdoba', 'Salta'),
    ('Buenos Aires', 'Neuquén'),       ('Rosario', 'Córdoba'),
    ('Buenos Aires', 'Ushuaia')
);

DELETE pn FROM preferencias_notificaciones pn JOIN tmp_usuarios t ON t.idUsuario = pn.idUsuario;
DELETE pa FROM pasajeros pa                   JOIN tmp_usuarios t ON t.idUsuario = pa.idUsuario;
DELETE e  FROM empleados e                    JOIN tmp_usuarios t ON t.idUsuario = e.idUsuario;
DELETE u  FROM usuarios u                     JOIN tmp_usuarios t ON t.idUsuario = u.idUsuario;

DELETE FROM personas
WHERE dni LIKE '300000__' OR dni LIKE '400000__'
   OR dni IN ('38123456', '35987654', '42111222', '40333444');

DROP TEMPORARY TABLE IF EXISTS tmp_vehiculos, tmp_usuarios;

-- =========================================
-- PERSONAS (DNI 300000xx = empleados, 400000xx = clientes)
-- El usuario de cada uno es lo que está antes del @ del email
-- =========================================
INSERT INTO personas (nombre, apellido, dni, telefono, email) VALUES
('Lucía',     'Fernández', '30000001', '2235551001', 'lfernandez@mail.com'),
('Martín',    'Gómez',     '30000002', '2235551002', 'mgomez@mail.com'),
('Carla',     'Benítez',   '30000003', '2235551003', 'cbenitez@mail.com'),
('Diego',     'Sosa',      '30000004', '2235551004', 'dsosa@mail.com'),
('Valentina', 'Ruiz',      '30000005', '2235551005', 'vruiz@mail.com'),
('Joaquín',   'Medina',    '30000006', '2235551006', 'jmedina@mail.com'),
('Florencia', 'Castro',    '30000007', '2235551007', 'fcastro@mail.com'),
('Nicolás',   'Romero',    '30000008', '2235551008', 'nromero@mail.com'),
('Camila',    'Acosta',    '30000009', '2235551009', 'cacosta@mail.com'),
('Federico',  'Molina',    '30000010', '2235551010', 'fmolina@mail.com'),
('Agustina',  'Rojas',     '30000011', '2235551011', 'arojas@mail.com'),
('Pablo',     'Navarro',   '30000012', '2235551012', 'pnavarro@mail.com'),
('Julieta',   'Peralta',   '30000013', '2235551013', 'jperalta@mail.com'),
('Matías',    'Vega',      '30000014', '2235551014', 'mvega@mail.com'),
('Rocío',     'Cabrera',   '30000015', '2235551015', 'rcabrera@mail.com'),

('Sofía',     'Ramírez',   '40000001', '1155552001', 'sramirez@mail.com'),
('Tomás',     'Herrera',   '40000002', '1155552002', 'therrera@mail.com'),
('Martina',   'López',     '40000003', '1155552003', 'mlopez@mail.com'),
('Lucas',     'Díaz',      '40000004', '1155552004', 'ldiaz@mail.com'),
('Paula',     'Torres',    '40000005', '1155552005', 'ptorres@mail.com'),
('Bruno',     'Álvarez',   '40000006', '1155552006', 'balvarez@mail.com'),
('Micaela',   'Suárez',    '40000007', '1155552007', 'msuarez@mail.com'),
('Gonzalo',   'Ríos',      '40000008', '1155552008', 'grios@mail.com'),
('Antonella', 'Giménez',   '40000009', '1155552009', 'agimenez@mail.com'),
('Facundo',   'Silva',     '40000010', '1155552010', 'fsilva@mail.com'),
('Milagros',  'Ortiz',     '40000011', '1155552011', 'mortiz@mail.com'),
('Ezequiel',  'Luna',      '40000012', '1155552012', 'eluna@mail.com'),
('Brenda',    'Morales',   '40000013', '1155552013', 'bmorales@mail.com'),
('Ignacio',   'Ibáñez',    '40000014', '1155552014', 'iibanez@mail.com'),
('Daniela',   'Correa',    '40000015', '1155552015', 'dcorrea@mail.com');

-- =========================================
-- USUARIOS (uno por persona, contraseña 1234)
-- =========================================
INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario)
SELECT @rolEmpleado, idPersona, SUBSTRING_INDEX(email, '@', 1), @pass
FROM personas WHERE dni LIKE '300000__';

INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario)
SELECT @rolCliente, idPersona, SUBSTRING_INDEX(email, '@', 1), @pass
FROM personas WHERE dni LIKE '400000__';

-- =========================================
-- EMPLEADOS Y PASAJEROS
-- =========================================
INSERT INTO empleados (idUsuario)
SELECT u.idUsuario FROM usuarios u
JOIN personas p ON p.idPersona = u.idPersona
WHERE p.dni LIKE '300000__';

INSERT INTO pasajeros (idUsuario)
SELECT u.idUsuario FROM usuarios u
JOIN personas p ON p.idPersona = u.idPersona
WHERE p.dni LIKE '400000__';

-- =========================================
-- PREFERENCIAS (una por pasajero, con valores variados)
-- =========================================
INSERT INTO preferencias_notificaciones (idUsuario, notificarReservas, notificarCancelaciones, notificarCambiosViajes)
SELECT u.idUsuario,
       TRUE,
       u.idUsuario % 2 = 0,
       u.idUsuario % 3 <> 0
FROM usuarios u
JOIN personas p ON p.idPersona = u.idPersona
WHERE p.dni LIKE '400000__';

-- =========================================
-- RECORRIDOS
-- =========================================
INSERT INTO recorridos (origen, destino, distanciaKM) VALUES
('Mar del Plata', 'Buenos Aires',  415.00),
('Buenos Aires',  'Mar del Plata', 415.00),
('Buenos Aires',  'Córdoba',       700.00),
('Córdoba',       'Buenos Aires',  700.00),
('Buenos Aires',  'Rosario',       300.00),
('Rosario',       'Buenos Aires',  300.00),
('Buenos Aires',  'Mendoza',      1050.00),
('Mendoza',       'Buenos Aires', 1050.00),
('Buenos Aires',  'Bariloche',    1570.00),
('Bariloche',     'Buenos Aires', 1570.00),
('Mar del Plata', 'Bahía Blanca',  460.00),
('Córdoba',       'Salta',         880.00),
('Buenos Aires',  'Neuquén',      1140.00),
('Rosario',       'Córdoba',       400.00),
('Buenos Aires',  'Ushuaia',      3050.00);

-- =========================================
-- VEHÍCULOS (15 colectivos, 15 aviones, 15 trenes)
-- =========================================
INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES
-- Colectivos
('AE101AA', 60, 25.50), ('AE102AA', 45, 20.00), ('AE103AA', 60, 26.00),
('AE104AA', 50, 22.00), ('AE105AA', 60, 27.00), ('AE106AA', 45, 21.00),
('AE107AA', 60, 25.00), ('AE108AA', 40, 19.50), ('AE109AA', 60, 26.50),
('AE110AA', 50, 23.00), ('AE111AA', 45, 20.50), ('AE112AA', 60, 25.50),
('AE113AA', 40, 19.00), ('AE114AA', 50, 22.50), ('AE115AA', 60, 27.50),
-- Aviones
('LV-A01', 180, 95.00), ('LV-A02',  70, 80.00), ('LV-A03', 150, 90.00),
('LV-A04', 180, 98.00), ('LV-A05', 120, 85.00), ('LV-A06',  70, 78.00),
('LV-A07', 180, 96.00), ('LV-A08', 150, 92.00), ('LV-A09',  70, 80.00),
('LV-A10', 120, 86.00), ('LV-A11', 180, 97.00), ('LV-A12', 150, 91.00),
('LV-A13',  70, 79.00), ('LV-A14', 120, 84.00), ('LV-A15', 180, 99.00),
-- Trenes
('TR-001', 400, 10.00), ('TR-002', 300, 12.00), ('TR-003', 450, 11.00),
('TR-004', 350, 12.50), ('TR-005', 400, 10.50), ('TR-006', 300, 13.00),
('TR-007', 450,  9.50), ('TR-008', 350, 11.50), ('TR-009', 400, 10.00),
('TR-010', 300, 12.00), ('TR-011', 450, 11.00), ('TR-012', 350, 12.50),
('TR-013', 400,  9.00), ('TR-014', 300, 13.00), ('TR-015', 450, 10.50);

-- Colectivos: 60 asientos = 2 pisos; 50 o más = tiene baño
INSERT INTO colectivos (idVehiculo, cantidadPisos, `tieneBaño`)
SELECT idVehiculo,
       IF(capacidadPasajeros >= 60, 2, 1),
       capacidadPasajeros >= 50
FROM vehiculos WHERE patente LIKE 'AE1__AA';

-- Aviones: más capacidad = más autonomía
INSERT INTO aviones (idVehiculo, autonomiaKM, cantTurbinas)
SELECT idVehiculo,
       CASE WHEN capacidadPasajeros >= 150 THEN 5600
            WHEN capacidadPasajeros >= 120 THEN 4000
            ELSE 2500 END,
       2
FROM vehiculos WHERE patente LIKE 'LV-A__';

-- Trenes: 1 vagón cada 50 pasajeros; costo alto = eléctrico
INSERT INTO trenes (idVehiculo, cantVagones, tipoTraccion)
SELECT idVehiculo,
       capacidadPasajeros DIV 50,
       IF(costoPorKm >= 12, 'Eléctrica', 'Diésel')
FROM vehiculos WHERE patente LIKE 'TR-0__';

-- =========================================
-- VIAJES (8 ya realizados, 7 futuros)
-- =========================================
INSERT INTO viajes (idRecorrido, idVehiculo, fechaHoraSalida, fechaHoraLlegada) VALUES
((SELECT idRecorrido FROM recorridos WHERE origen = 'Mar del Plata' AND destino = 'Buenos Aires'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'AE101AA'), '2026-09-05 08:00:00', '2026-09-05 13:30:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Córdoba'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'LV-A01'),  '2026-09-12 10:00:00', '2026-09-12 11:20:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Rosario'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'TR-001'),  '2026-09-20 07:00:00', '2026-09-20 13:00:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Mar del Plata'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'AE102AA'), '2026-10-03 22:00:00', '2026-10-04 03:30:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Mendoza'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'LV-A02'),  '2026-10-10 09:00:00', '2026-10-10 10:50:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Mar del Plata' AND destino = 'Bahía Blanca'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'AE103AA'), '2026-10-15 08:00:00', '2026-10-15 14:00:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Bariloche'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'LV-A03'),  '2026-10-25 06:30:00', '2026-10-25 08:50:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Rosario' AND destino = 'Buenos Aires'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'TR-002'),  '2026-11-08 15:00:00', '2026-11-08 21:00:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Córdoba' AND destino = 'Salta'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'AE104AA'), '2026-11-28 21:00:00', '2026-11-29 08:30:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Ushuaia'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'LV-A04'),  '2026-12-05 05:00:00', '2026-12-05 08:40:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Rosario' AND destino = 'Córdoba'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'TR-003'),  '2026-12-08 08:00:00', '2026-12-08 14:30:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Buenos Aires' AND destino = 'Neuquén'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'AE105AA'), '2026-12-15 20:00:00', '2026-12-16 08:00:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Mendoza' AND destino = 'Buenos Aires'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'LV-A05'),  '2026-12-20 18:00:00', '2026-12-20 19:45:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Córdoba' AND destino = 'Buenos Aires'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'AE106AA'), '2027-01-03 22:00:00', '2027-01-04 07:30:00'),
((SELECT idRecorrido FROM recorridos WHERE origen = 'Bariloche' AND destino = 'Buenos Aires'),
 (SELECT idVehiculo FROM vehiculos WHERE patente = 'LV-A06'),  '2027-01-10 12:00:00', '2027-01-10 14:20:00');

-- =========================================
-- RESERVAS (una por pasajero; 3 canceladas)
-- =========================================
INSERT INTO reservas (idPasajero, idViaje, fechaReserva, estado)
SELECT pa.idPasajero, v.idViaje, d.fechaReserva, d.estado
FROM (
    SELECT 'sramirez' AS usuario, 'AE101AA' AS patente, '2026-08-28 18:20:00' AS fechaReserva, 'ACTIVA' AS estado
    UNION ALL SELECT 'therrera', 'LV-A01',  '2026-09-01 10:05:00', 'ACTIVA'
    UNION ALL SELECT 'mlopez',   'TR-001',  '2026-09-10 21:40:00', 'ACTIVA'
    UNION ALL SELECT 'ldiaz',    'AE102AA', '2026-09-25 16:00:00', 'ACTIVA'
    UNION ALL SELECT 'ptorres',  'LV-A02',  '2026-09-30 11:30:00', 'CANCELADA'
    UNION ALL SELECT 'balvarez', 'AE103AA', '2026-10-05 19:15:00', 'ACTIVA'
    UNION ALL SELECT 'msuarez',  'LV-A03',  '2026-10-12 09:50:00', 'ACTIVA'
    UNION ALL SELECT 'grios',    'TR-002',  '2026-10-30 20:10:00', 'ACTIVA'
    UNION ALL SELECT 'agimenez', 'AE104AA', '2026-11-15 17:45:00', 'ACTIVA'
    UNION ALL SELECT 'fsilva',   'LV-A04',  '2026-11-18 22:00:00', 'CANCELADA'
    UNION ALL SELECT 'mortiz',   'TR-003',  '2026-11-20 13:25:00', 'ACTIVA'
    UNION ALL SELECT 'eluna',    'AE105AA', '2026-11-21 08:40:00', 'ACTIVA'
    UNION ALL SELECT 'bmorales', 'LV-A05',  '2026-11-22 15:10:00', 'ACTIVA'
    UNION ALL SELECT 'iibanez',  'AE106AA', '2026-11-22 19:30:00', 'CANCELADA'
    UNION ALL SELECT 'dcorrea',  'LV-A06',  '2026-11-23 10:00:00', 'ACTIVA'
) AS d
JOIN usuarios u   ON u.nombreUsuario = d.usuario
JOIN pasajeros pa ON pa.idUsuario = u.idUsuario
JOIN vehiculos ve ON ve.patente = d.patente
JOIN viajes v     ON v.idVehiculo = ve.idVehiculo;

-- =========================================
-- ASIENTOS (uno por reserva)
-- =========================================
INSERT INTO asientos_reserva (idReserva, codigoAsiento)
SELECT r.idReserva, d.asiento
FROM (
    SELECT 'sramirez' AS usuario, '12A' AS asiento
    UNION ALL SELECT 'therrera', '3C'
    UNION ALL SELECT 'mlopez',   '2B'
    UNION ALL SELECT 'ldiaz',    '8D'
    UNION ALL SELECT 'ptorres',  '14F'
    UNION ALL SELECT 'balvarez', '21A'
    UNION ALL SELECT 'msuarez',  '7B'
    UNION ALL SELECT 'grios',    '5C'
    UNION ALL SELECT 'agimenez', '30D'
    UNION ALL SELECT 'fsilva',   '9A'
    UNION ALL SELECT 'mortiz',   '4A'
    UNION ALL SELECT 'eluna',    '16C'
    UNION ALL SELECT 'bmorales', '11E'
    UNION ALL SELECT 'iibanez',  '2A'
    UNION ALL SELECT 'dcorrea',  '18B'
) AS d
JOIN usuarios u   ON u.nombreUsuario = d.usuario
JOIN pasajeros pa ON pa.idUsuario = u.idUsuario
JOIN reservas r   ON r.idPasajero = pa.idPasajero;
