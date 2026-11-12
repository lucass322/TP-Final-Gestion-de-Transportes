package DAO;

import Empleados.Empleado;
import Conexion.ConexionDB;
import Personas.Persona;
import Roles.Rol;
import Utilidades.Encriptacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO implements iDAO<Empleado> {
    @Override
    public void alta(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (idUsuario) " +
                "SELECT u.idUsuario FROM usuarios u " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.idUsuario = ? AND UPPER(r.nombreRol) = 'EMPLEADO'";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, empleado.getIdUsuario());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException(
                        "No se pudo registrar el empleado. El usuario no existe o no tiene rol EMPLEADO."
                );
            }

            // Recupera el ID del empleado generado.
            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    empleado.setIdEmpleado(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void baja(int id) throws SQLException {
        String sql = "UPDATE usuarios u " +
                "INNER JOIN empleados e ON u.idUsuario = e.idUsuario " +
                "SET u.habilitadoUsuario = FALSE " +
                "WHERE e.idEmpleado = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el empleado.");
            }
        }
    }

    @Override
    public void modificar(Empleado empleado) throws SQLException {
        if (empleado.getPersona() == null) {
            throw new SQLException("El empleado no tiene una Persona asociada.");
        }

        String sqlExiste = """
        SELECT em.idEmpleado
        FROM empleados em
        INNER JOIN usuarios u ON em.idUsuario = u.idUsuario
        INNER JOIN roles r ON u.idRol = r.idRol
        WHERE em.idEmpleado = ?
        AND UPPER(r.nombreRol) = 'EMPLEADO'
        FOR UPDATE
        """;

        String sqlPersona = """
            UPDATE personas per
            INNER JOIN usuarios u ON per.idPersona = u.idPersona
            INNER JOIN empleados em ON u.idUsuario = em.idUsuario
            SET per.nombre = ?,
                per.apellido = ?,
                per.dni = ?,
                per.telefono = ?,
                per.email = ?
            WHERE em.idEmpleado = ?
            """;

        String sqlUsuario = """
            UPDATE usuarios u
            INNER JOIN empleados em ON u.idUsuario = em.idUsuario
            SET u.nombreUsuario = ?
            WHERE em.idEmpleado = ?
            """;

        String sqlPassword = """
            UPDATE usuarios u
            INNER JOIN empleados em ON u.idUsuario = em.idUsuario
            SET u.passwordUsuario = ?
            WHERE em.idEmpleado = ?
            """;

        try (Connection con = ConexionDB.conectar()) {

            // Inicia una transacción.
            con.setAutoCommit(false);

            try {

                // Comprueba que el empleado exista.
                try (PreparedStatement psExiste = con.prepareStatement(sqlExiste)) {

                    psExiste.setInt(1, empleado.getIdEmpleado());

                    try (ResultSet rs = psExiste.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("No se encontró el empleado o no tiene el rol correspondiente.");
                        }
                    }
                }

                // Actualiza los datos personales.
                try (PreparedStatement psPersona = con.prepareStatement(sqlPersona)) {

                    psPersona.setString(1, empleado.getPersona().getNombre());
                    psPersona.setString(2, empleado.getPersona().getApellido());
                    psPersona.setString(3, empleado.getPersona().getDni());
                    psPersona.setString(4, empleado.getPersona().getTelefono());
                    psPersona.setString(5, empleado.getPersona().getEmail());
                    psPersona.setInt(6, empleado.getIdEmpleado());

                    psPersona.executeUpdate();
                }

                // Actualiza los datos de la cuenta.
                try (PreparedStatement psUsuario = con.prepareStatement(sqlUsuario)) {

                    psUsuario.setString(1, empleado.getNombreUsuario());
                    psUsuario.setInt(2, empleado.getIdEmpleado());

                    psUsuario.executeUpdate();
                }

                // Actualiza la contraseña únicamente si se ingresó una nueva.
                if (empleado.getPasswordUsuario() != null &&
                        !empleado.getPasswordUsuario().isBlank()) {

                    try (PreparedStatement psPassword = con.prepareStatement(sqlPassword)) {

                        psPassword.setString(1,
                                Encriptacion.hashearPassword(empleado.getPasswordUsuario()));

                        psPassword.setInt(2, empleado.getIdEmpleado());

                        psPassword.executeUpdate();
                    }
                }

                // Confirma todas las modificaciones.
                con.commit();

            } catch (SQLException ex) {

                // Revierte los cambios si ocurre algún error.
                con.rollback();
                throw ex;
            }
        }
    }

    @Override
    public Empleado consultar(int id) throws SQLException {
        String sql = """
            SELECT
                em.idEmpleado,
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
            FROM empleados em
            INNER JOIN usuarios u ON em.idUsuario = u.idUsuario
            INNER JOIN personas p ON u.idPersona = p.idPersona
            INNER JOIN roles r ON u.idRol = r.idRol
            WHERE em.idEmpleado = ?
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

                    // Construye el Empleado con sus datos relacionados.
                    return new Empleado(
                            rs.getInt("idEmpleado"),
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
    public List<Empleado> listar() throws SQLException {
        List<Empleado> lista = new ArrayList<>();

        String sql = """
            SELECT
                em.idEmpleado,
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
            FROM empleados em
            INNER JOIN usuarios u ON em.idUsuario = u.idUsuario
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

                // Construye el Empleado con sus datos relacionados.
                lista.add(new Empleado(
                        rs.getInt("idEmpleado"),
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

    public void cambiarEstado(List<Integer> idsEmpleados, boolean habilitado) throws SQLException {

        if (idsEmpleados == null || idsEmpleados.isEmpty()) {
            return;
        }

        String placeholders = String.join(",",
                java.util.Collections.nCopies(idsEmpleados.size(), "?"));

        String sql = """
        UPDATE usuarios u
        INNER JOIN empleados e ON u.idUsuario = e.idUsuario
        SET u.habilitadoUsuario = ?
        WHERE e.idEmpleado IN (
    """ + placeholders + ")";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, habilitado);

            for (int i = 0; i < idsEmpleados.size(); i++) {
                ps.setInt(i + 2, idsEmpleados.get(i));
            }

            ps.executeUpdate();
        }
    }
}
