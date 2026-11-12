package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Conexion.ConexionDB;
import Pasajeros.Pasajero;
import Utilidades.Encriptacion;
import Personas.Persona;
import Roles.Rol;

public class PasajeroDAO implements iDAO<Pasajero> {

    @Override
    public void alta(Pasajero p) throws SQLException {

        String sql = "INSERT INTO pasajeros (idUsuario) " +
                "SELECT u.idUsuario FROM usuarios u " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.idUsuario = ? AND UPPER(r.nombreRol) = 'CLIENTE'";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdUsuario());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo registrar el pasajero. El usuario no existe o no tiene rol CLIENTE.");
            }

            // Recupera el ID del pasajero generado.
            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    p.setIdPasajero(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void baja(int id) throws SQLException {

        String sql = "UPDATE usuarios u " +
                "INNER JOIN pasajeros p ON u.idUsuario = p.idUsuario " +
                "SET u.habilitadoUsuario = FALSE " +
                "WHERE p.idPasajero = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el pasajero.");
            }
        }
    }

    @Override
    public void modificar(Pasajero p) throws SQLException {

        if (p.getPersona() == null) {
            throw new SQLException("El pasajero no tiene una Persona asociada.");
        }

        String sqlExiste = "SELECT idPasajero FROM pasajeros WHERE idPasajero = ? FOR UPDATE";

        String sqlPersona = """
            UPDATE personas per
            INNER JOIN usuarios u ON per.idPersona = u.idPersona
            INNER JOIN pasajeros pa ON u.idUsuario = pa.idUsuario
            SET per.nombre = ?,
                per.apellido = ?,
                per.dni = ?,
                per.telefono = ?,
                per.email = ?
            WHERE pa.idPasajero = ?
            """;

        String sqlUsuario = """
            UPDATE usuarios u
            INNER JOIN pasajeros pa ON u.idUsuario = pa.idUsuario
            SET u.nombreUsuario = ?
            WHERE pa.idPasajero = ?
            """;

        String sqlPassword = """
                        UPDATE usuarios u
                        INNER JOIN pasajeros pa ON u.idUsuario = pa.idUsuario
                        SET u.passwordUsuario = ?
                        WHERE pa.idPasajero = ?
                        """;

        try (Connection con = ConexionDB.conectar()) {

            // Inicia una transacción.
            con.setAutoCommit(false);

            try {

                // Comprueba que el pasajero exista.
                try (PreparedStatement psExiste = con.prepareStatement(sqlExiste)) {

                    psExiste.setInt(1, p.getIdPasajero());

                    try (ResultSet rs = psExiste.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("No se encontró el pasajero.");
                        }
                    }
                }

                // Actualiza los datos personales.
                try (PreparedStatement psPersona = con.prepareStatement(sqlPersona)) {

                    psPersona.setString(1, p.getPersona().getNombre());
                    psPersona.setString(2, p.getPersona().getApellido());
                    psPersona.setString(3, p.getPersona().getDni());
                    psPersona.setString(4, p.getPersona().getTelefono());
                    psPersona.setString(5, p.getPersona().getEmail());
                    psPersona.setInt(6, p.getIdPasajero());

                    psPersona.executeUpdate();
                }

                // Actualiza los datos de la cuenta.
                try (PreparedStatement psUsuario = con.prepareStatement(sqlUsuario)) {

                    psUsuario.setString(1, p.getNombreUsuario());
                    psUsuario.setInt(2, p.getIdPasajero());

                    psUsuario.executeUpdate();
                }

                // Actualiza la contraseña únicamente si se ingresó una nueva.
                if (p.getPasswordUsuario() != null &&
                        !p.getPasswordUsuario().isBlank()) {

                    try (PreparedStatement psPassword = con.prepareStatement(sqlPassword)) {

                        psPassword.setString(1,
                                Encriptacion.hashearPassword(p.getPasswordUsuario()));

                        psPassword.setInt(2, p.getIdPasajero());

                        psPassword.executeUpdate();
                    }
                }

                // Confirma todas las modificaciones.
                con.commit();

            } catch (SQLException e) {

                // Revierte los cambios si ocurre algún error.
                con.rollback();
                throw e;
            }
        }
    }

    @Override
    public Pasajero consultar(int id) throws SQLException {

        String sql = """
            SELECT
                pa.idPasajero,
                u.idUsuario,
                u.nombreUsuario,
                u.habilitadoUsuario,
                p.idPersona,
                p.nombre,
                p.apellido,
                p.dni,
                p.telefono,
                p.email,
                p.habilitadoPersona,
                r.idRol,
                r.nombreRol,
                r.habilitadoRol
            FROM pasajeros pa
            INNER JOIN usuarios u ON pa.idUsuario = u.idUsuario
            INNER JOIN personas p ON u.idPersona = p.idPersona
            INNER JOIN roles r ON u.idRol = r.idRol
            WHERE pa.idPasajero = ?
            """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // Construye la Persona asociada.
                    Persona persona = new Persona(
                            rs.getInt("idPersona"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("dni"),
                            rs.getString("telefono"),
                            rs.getString("email"),
                            rs.getBoolean("habilitadoPersona")
                    );

                    // Construye el Rol del usuario.
                    Rol rol = new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );

                    // Construye el Pasajero con sus datos relacionados.
                    return new Pasajero(
                            rs.getInt("idPasajero"),
                            rs.getInt("idUsuario"),
                            persona,
                            rol,
                            rs.getString("nombreUsuario"),
                            null,
                            rs.getBoolean("habilitadoUsuario")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Pasajero> listar() throws SQLException {

        List<Pasajero> lista = new ArrayList<>();

        String sql = """
            SELECT
                pa.idPasajero,
                u.idUsuario,
                u.nombreUsuario,
                u.habilitadoUsuario,
                p.idPersona,
                p.nombre,
                p.apellido,
                p.dni,
                p.telefono,
                p.email,
                p.habilitadoPersona,
                r.idRol,
                r.nombreRol,
                r.habilitadoRol
            FROM pasajeros pa
            INNER JOIN usuarios u ON pa.idUsuario = u.idUsuario
            INNER JOIN personas p ON u.idPersona = p.idPersona
            INNER JOIN roles r ON u.idRol = r.idRol
            """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Construye la Persona asociada.
                Persona persona = new Persona(
                        rs.getInt("idPersona"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getBoolean("habilitadoPersona")
                );

                // Construye el Rol del usuario.
                Rol rol = new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombreRol"),
                        rs.getBoolean("habilitadoRol")
                );

                // Construye el Pasajero con sus datos relacionados.
                lista.add(new Pasajero(
                        rs.getInt("idPasajero"),
                        rs.getInt("idUsuario"),
                        persona,
                        rol,
                        rs.getString("nombreUsuario"),
                        null,
                        rs.getBoolean("habilitadoUsuario")
                ));
            }
        }

        return lista;
    }

}
