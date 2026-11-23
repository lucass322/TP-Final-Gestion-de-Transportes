USE rutaGO_db;

CREATE TABLE IF NOT EXISTS preferencias_notificaciones (
    idPreferencia INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL UNIQUE,
    notificarReservas BOOLEAN NOT NULL DEFAULT TRUE,
    notificarCancelaciones BOOLEAN NOT NULL DEFAULT TRUE,
    notificarCambiosViajes BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario)
);