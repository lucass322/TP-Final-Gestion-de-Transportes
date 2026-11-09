USE rutaGo_db;

INSERT INTO roles (nombreRol) VALUES ("Administrador");
SET @idAdministrador = last_insert_id();
INSERT INTO roles (nombreRol) VALUES ("Empleado");
SET @idEmpleado = last_insert_id();
INSERT INTO roles (nombreRol) VALUES ("Cliente");
SET @idCliente = last_insert_id();
INSERT INTO roles (nombreRol) VALUES ("ROOT");
SET @idRoot = last_insert_id();