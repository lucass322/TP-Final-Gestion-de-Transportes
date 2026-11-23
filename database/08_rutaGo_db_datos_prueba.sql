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
-- (solo borra lo que carga este script)
-- =========================================
DELETE ar FROM asientos_reserva ar
JOIN reservas r ON r.idReserva = ar.idReserva
JOIN viajes v ON v.idViaje = r.idViaje
JOIN vehiculos ve ON ve.idVehiculo = v.idVehiculo
WHERE ve.patente IN ('AE123BC', 'AF456DE', 'LV-ABC', 'LV-XYZ', 'TR-001', 'TR-002');

DELETE r FROM reservas r
JOIN viajes v ON v.idViaje = r.idViaje
JOIN vehiculos ve ON ve.idVehiculo = v.idVehiculo
WHERE ve.patente IN ('AE123BC', 'AF456DE', 'LV-ABC', 'LV-XYZ', 'TR-001', 'TR-002');

DELETE v FROM viajes v
JOIN vehiculos ve ON ve.idVehiculo = v.idVehiculo
WHERE ve.patente IN ('AE123BC', 'AF456DE', 'LV-ABC', 'LV-XYZ', 'TR-001', 'TR-002');

DELETE c FROM colectivos c JOIN vehiculos ve ON ve.idVehiculo = c.idVehiculo
WHERE ve.patente IN ('AE123BC', 'AF456DE');

DELETE a FROM aviones a JOIN vehiculos ve ON ve.idVehiculo = a.idVehiculo
WHERE ve.patente IN ('LV-ABC', 'LV-XYZ');

DELETE t FROM trenes t JOIN vehiculos ve ON ve.idVehiculo = t.idVehiculo
WHERE ve.patente IN ('TR-001', 'TR-002');

DELETE FROM vehiculos
WHERE patente IN ('AE123BC', 'AF456DE', 'LV-ABC', 'LV-XYZ', 'TR-001', 'TR-002');

DELETE FROM recorridos
WHERE (origen = 'Mar del Plata' AND destino = 'Buenos Aires')
   OR (origen = 'Buenos Aires' AND destino = 'Córdoba');

DELETE pn FROM preferencias_notificaciones pn
JOIN usuarios u ON u.idUsuario = pn.idUsuario
WHERE u.nombreUsuario IN ('lfernandez', 'mgomez', 'sramirez', 'therrera');

DELETE pa FROM pasajeros pa
JOIN usuarios u ON u.idUsuario = pa.idUsuario
WHERE u.nombreUsuario IN ('lfernandez', 'mgomez', 'sramirez', 'therrera');

DELETE e FROM empleados e
JOIN usuarios u ON u.idUsuario = e.idUsuario
WHERE u.nombreUsuario IN ('lfernandez', 'mgomez', 'sramirez', 'therrera');

DELETE FROM usuarios
WHERE nombreUsuario IN ('lfernandez', 'mgomez', 'sramirez', 'therrera');

DELETE FROM personas
WHERE dni IN ('38123456', '35987654', '42111222', '40333444');

-- =========================================
-- PERSONAS
-- =========================================
INSERT INTO personas (nombre, apellido, dni, telefono, email)
VALUES ('Lucía', 'Fernández', '38123456', '2235551001', 'lucia.fernandez@mail.com');
SET @perEmp1 = LAST_INSERT_ID();

INSERT INTO personas (nombre, apellido, dni, telefono, email)
VALUES ('Martín', 'Gómez', '35987654', '2235551002', 'martin.gomez@mail.com');
SET @perEmp2 = LAST_INSERT_ID();

INSERT INTO personas (nombre, apellido, dni, telefono, email)
VALUES ('Sofía', 'Ramírez', '42111222', '1155552001', 'sofia.ramirez@mail.com');
SET @perCli1 = LAST_INSERT_ID();

INSERT INTO personas (nombre, apellido, dni, telefono, email)
VALUES ('Tomás', 'Herrera', '40333444', '1155552002', 'tomas.herrera@mail.com');
SET @perCli2 = LAST_INSERT_ID();

-- =========================================
-- USUARIOS
-- =========================================
INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario)
VALUES (@rolEmpleado, @perEmp1, 'lfernandez', @pass);
SET @usuEmp1 = LAST_INSERT_ID();

INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario)
VALUES (@rolEmpleado, @perEmp2, 'mgomez', @pass);
SET @usuEmp2 = LAST_INSERT_ID();

INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario)
VALUES (@rolCliente, @perCli1, 'sramirez', @pass);
SET @usuCli1 = LAST_INSERT_ID();

INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario)
VALUES (@rolCliente, @perCli2, 'therrera', @pass);
SET @usuCli2 = LAST_INSERT_ID();

-- =========================================
-- EMPLEADOS
-- =========================================
INSERT INTO empleados (idUsuario) VALUES (@usuEmp1);
INSERT INTO empleados (idUsuario) VALUES (@usuEmp2);

-- =========================================
-- PASAJEROS
-- =========================================
INSERT INTO pasajeros (idUsuario) VALUES (@usuCli1);
SET @pas1 = LAST_INSERT_ID();

INSERT INTO pasajeros (idUsuario) VALUES (@usuCli2);
SET @pas2 = LAST_INSERT_ID();

-- =========================================
-- PREFERENCIAS DE NOTIFICACIONES
-- =========================================
INSERT INTO preferencias_notificaciones (idUsuario, notificarReservas, notificarCancelaciones, notificarCambiosViajes)
VALUES (@usuCli1, TRUE, TRUE, TRUE);

INSERT INTO preferencias_notificaciones (idUsuario, notificarReservas, notificarCancelaciones, notificarCambiosViajes)
VALUES (@usuCli2, TRUE, FALSE, TRUE);

-- =========================================
-- RECORRIDOS
-- =========================================
INSERT INTO recorridos (origen, destino, distanciaKM)
VALUES ('Mar del Plata', 'Buenos Aires', 415.00);
SET @rec1 = LAST_INSERT_ID();

INSERT INTO recorridos (origen, destino, distanciaKM)
VALUES ('Buenos Aires', 'Córdoba', 700.00);
SET @rec2 = LAST_INSERT_ID();

-- =========================================
-- COLECTIVOS
-- =========================================
INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES ('AE123BC', 60, 25.50);
SET @col1 = LAST_INSERT_ID();
INSERT INTO colectivos (idVehiculo, cantidadPisos, `tieneBaño`) VALUES (@col1, 2, TRUE);

INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES ('AF456DE', 45, 20.00);
SET @col2 = LAST_INSERT_ID();
INSERT INTO colectivos (idVehiculo, cantidadPisos, `tieneBaño`) VALUES (@col2, 1, FALSE);

-- =========================================
-- AVIONES
-- =========================================
INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES ('LV-ABC', 180, 95.00);
SET @avi1 = LAST_INSERT_ID();
INSERT INTO aviones (idVehiculo, autonomiaKM, cantTurbinas) VALUES (@avi1, 5600, 2);

INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES ('LV-XYZ', 70, 80.00);
SET @avi2 = LAST_INSERT_ID();
INSERT INTO aviones (idVehiculo, autonomiaKM, cantTurbinas) VALUES (@avi2, 2500, 2);

-- =========================================
-- TRENES
-- =========================================
INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES ('TR-001', 400, 10.00);
SET @tre1 = LAST_INSERT_ID();
INSERT INTO trenes (idVehiculo, cantVagones, tipoTraccion) VALUES (@tre1, 8, 'Diésel');

INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm) VALUES ('TR-002', 300, 12.00);
SET @tre2 = LAST_INSERT_ID();
INSERT INTO trenes (idVehiculo, cantVagones, tipoTraccion) VALUES (@tre2, 6, 'Eléctrica');

-- =========================================
-- VIAJES (uno ya realizado, uno futuro)
-- =========================================
INSERT INTO viajes (idRecorrido, idVehiculo, fechaHoraSalida, fechaHoraLlegada)
VALUES (@rec1, @col1, '2026-10-15 08:00:00', '2026-10-15 13:30:00');
SET @via1 = LAST_INSERT_ID();

INSERT INTO viajes (idRecorrido, idVehiculo, fechaHoraSalida, fechaHoraLlegada)
VALUES (@rec2, @avi1, '2026-12-10 10:00:00', '2026-12-10 11:30:00');
SET @via2 = LAST_INSERT_ID();

-- =========================================
-- RESERVAS (una activa, una cancelada)
-- =========================================
INSERT INTO reservas (idPasajero, idViaje, fechaReserva, estado)
VALUES (@pas1, @via1, '2026-10-01 18:20:00', 'ACTIVA');
SET @res1 = LAST_INSERT_ID();

INSERT INTO reservas (idPasajero, idViaje, fechaReserva, estado)
VALUES (@pas2, @via2, '2026-11-20 21:05:00', 'CANCELADA');
SET @res2 = LAST_INSERT_ID();

-- =========================================
-- ASIENTOS
-- =========================================
INSERT INTO asientos_reserva (idReserva, codigoAsiento) VALUES (@res1, '12A');
INSERT INTO asientos_reserva (idReserva, codigoAsiento) VALUES (@res2, '3C');

SET SQL_SAFE_UPDATES = 1;