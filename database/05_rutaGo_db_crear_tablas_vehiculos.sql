USE rutaGo_db;

CREATE TABLE IF NOT EXISTS vehiculos (
    idVehiculo INT AUTO_INCREMENT PRIMARY KEY,
    patente VARCHAR(20) NOT NULL UNIQUE,
    capacidadPasajeros INT NOT NULL,
    costoPorKm DECIMAL(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS aviones (
    idVehiculo INT PRIMARY KEY,
    autonomiaKM DOUBLE NOT NULL,
    cantTurbinas INT NOT NULL,

	FOREIGN KEY (idVehiculo) REFERENCES vehiculos(idVehiculo)
);

CREATE TABLE IF NOT EXISTS colectivos (
    idVehiculo INT PRIMARY KEY,
    cantidadPisos INT NOT NULL,
    tieneBaño BOOLEAN NOT NULL,

	FOREIGN KEY (idVehiculo) REFERENCES vehiculos (idVehiculo)
);

CREATE TABLE IF NOT EXISTS trenes (
    idVehiculo INT PRIMARY KEY,
    cantVagones INT NOT NULL,
    tipoTraccion VARCHAR(50) NOT NULL,
    
	FOREIGN KEY (idVehiculo) REFERENCES vehiculos (idVehiculo)
);
