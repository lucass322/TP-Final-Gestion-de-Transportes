USE rutaGo_db;

CREATE TABLE IF NOT EXISTS viajes (
    idViaje INT AUTO_INCREMENT PRIMARY KEY,
    idRecorrido INT NOT NULL,
    idVehiculo INT NOT NULL,
    fechaHoraSalida DATETIME NOT NULL,
    fechaHoraLlegada DATETIME NOT NULL,
    habilitadoViaje BOOLEAN NOT NULL DEFAULT TRUE,
	
    CONSTRAINT chk_fechas_viaje CHECK (fechaHoraLlegada > fechaHoraSalida),
    
    FOREIGN KEY (idRecorrido) REFERENCES recorridos(idRecorrido),

    FOREIGN KEY (idVehiculo) REFERENCES vehiculos(idVehiculo)
);

CREATE TABLE IF NOT EXISTS reservas (
    idReserva INT AUTO_INCREMENT PRIMARY KEY,
    idPasajero INT NOT NULL,
    idViaje INT NOT NULL,
    fechaReserva DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',

	CONSTRAINT chk_estado_reserva CHECK (estado IN ('ACTIVA', 'CANCELADA')),

	FOREIGN KEY (idPasajero) REFERENCES pasajeros (idPasajero),

	FOREIGN KEY (idViaje) REFERENCES viajes(idViaje)
);

CREATE TABLE IF NOT EXISTS asientos_reserva (
    idReserva INT NOT NULL,
    codigoAsiento VARCHAR(10) NOT NULL,
    
    PRIMARY KEY (idReserva, codigoAsiento),
    
    FOREIGN KEY (idReserva) REFERENCES reservas (idReserva)
);