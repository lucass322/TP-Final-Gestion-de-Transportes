USE rutaGo_db;

CREATE TABLE IF NOT EXISTS recorridos (
    idRecorrido INT AUTO_INCREMENT PRIMARY KEY,
    origen VARCHAR(150) NOT NULL,
    destino VARCHAR(150) NOT NULL,
    distanciaKM DECIMAL(10,2) NOT NULL CHECK (distanciaKM > 0),
	habilitadoRecorrido BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT uq_recorrido UNIQUE (origen, destino)
);
