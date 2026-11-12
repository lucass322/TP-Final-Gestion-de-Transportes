package DAO;

import Conexion.ConexionDB;
import Personas.Persona;
import Roles.Rol;
import Usuarios.Usuario;
import Utilidades.Encriptacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioDAO implements iDAO<Usuario>{
    public boolean hayRoot() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios u INNER JOIN roles r ON u.idRol = r.idRol WHERE r.nombreRol = ?";
        // Buscamos en usuarios todos los registros relacionados con un determinado rol y contamos cuántos existen.

        try(Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "ROOT");

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean existeNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT nombreUsuario FROM usuarios WHERE nombreUsuario = ?";

        try(Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()){  // Verificamos si existe algún registro en la base de datos que coincida
                                                     // con el nombre de usuario recibido como parámetro. Si encuentra uno,
                                                     // devuelve true; de lo contrario, devuelve false
                return rs.next();
            }
        }
    }

    public boolean existeNombreUsuario(String nombreUsuario, int idUsuario) throws SQLException {
        String sql = "SELECT nombreUsuario FROM usuarios WHERE nombreUsuario = ? AND idUsuario != ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT email FROM personas WHERE email = ?";

        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try(ResultSet rs = ps.executeQuery()) { // Verificamos si existe algún registro en la base de datos que coincida
                                                    // con el email recibido como parámetro. Si encuentra uno,
                                                    // devuelve true; de lo contrario, devuelve false
                return rs.next();
            }
        }
    }

    public boolean existeEmail(String email, int idUsuario) throws SQLException {
        String sql = "SELECT p.email FROM personas p " +
                "INNER JOIN usuarios u ON p.idPersona = u.idPersona " +
                "WHERE p.email = ? AND u.idUsuario != ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeDni(String dni) throws SQLException {
        String sql = "SELECT dni FROM personas WHERE dni = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeDni(String dni, int idUsuario) throws SQLException {
        String sql = "SELECT p.dni FROM personas p " +
                "INNER JOIN usuarios u ON p.idPersona = u.idPersona " +
                "WHERE p.dni = ? AND u.idUsuario != ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void alta(Usuario usuario) throws SQLException {

        String sqlPersona = "INSERT INTO personas (nombre, apellido, dni, telefono, email) " +
                "VALUES (?, ?, ?, ?, ?)";

        String sqlUsuario = "INSERT INTO usuarios (idRol, idPersona, nombreUsuario, passwordUsuario) " +
                "VALUES (?, ?, ?, ?)";

        String sqlEmpleado = "INSERT INTO empleados (idUsuario) VALUES (?)";

        String sqlPasajero = "INSERT INTO pasajeros (idUsuario) VALUES (?)";

        if (usuario.getRol() == null) {
            throw new SQLException("El usuario no tiene un rol asignado.");
        }

        String nombreRol = usuario.getRol().getNombreRol();

        boolean esRoot = "ROOT".equalsIgnoreCase(nombreRol);
        boolean esEmpleado = "EMPLEADO".equalsIgnoreCase(nombreRol);
        boolean esCliente = "CLIENTE".equalsIgnoreCase(nombreRol);

        if (!esRoot && !esEmpleado && !esCliente) {
            throw new SQLException("El rol seleccionado no es válido.");
        }

        if (usuario.getPersona() == null && !esRoot) {
            throw new SQLException("El usuario debe tener una persona asociada.");
        }

        if (esRoot && usuario.getPersona() != null) {
            throw new SQLException("ROOT no debe tener una persona asociada.");
        }

        try (Connection con = ConexionDB.conectar()) {

            con.setAutoCommit(false);

            try {

                Integer idPersona = null;

                // Registra la persona y recupera su ID.
                if (usuario.getPersona() != null) {

                    try (PreparedStatement psPersona = con.prepareStatement(
                            sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

                        psPersona.setString(1, usuario.getPersona().getNombre());
                        psPersona.setString(2, usuario.getPersona().getApellido());
                        psPersona.setString(3, usuario.getPersona().getDni());
                        psPersona.setString(4, usuario.getPersona().getTelefono());
                        psPersona.setString(5, usuario.getPersona().getEmail());

                        psPersona.executeUpdate();

                        try (ResultSet rs = psPersona.getGeneratedKeys()) {

                            if (rs.next()) {
                                idPersona = rs.getInt(1);
                                usuario.getPersona().setIdPersona(idPersona);
                            } else {
                                throw new SQLException("No se pudo obtener el ID de la persona.");
                            }
                        }
                    }
                }

                // Registra el usuario y recupera su ID.
                int idUsuario;

                try (PreparedStatement psUsuario = con.prepareStatement(
                        sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

                    psUsuario.setInt(1, usuario.getRol().getIdRol());

                    if (idPersona != null) {
                        psUsuario.setInt(2, idPersona);
                    } else {
                        psUsuario.setNull(2, Types.INTEGER);
                    }

                    psUsuario.setString(3, usuario.getNombreUsuario());
                    psUsuario.setString(4,
                            Encriptacion.hashearPassword(usuario.getPasswordUsuario()));

                    psUsuario.executeUpdate();

                    try (ResultSet rs = psUsuario.getGeneratedKeys()) {

                        if (rs.next()) {
                            idUsuario = rs.getInt(1);
                            usuario.setIdUsuario(idUsuario);
                        } else {
                            throw new SQLException("No se pudo obtener el ID del usuario.");
                        }
                    }
                }

                // Registra la entidad correspondiente según el rol.
                if (esEmpleado) {

                    try (PreparedStatement psEmpleado = con.prepareStatement(sqlEmpleado)) {
                        psEmpleado.setInt(1, idUsuario);
                        psEmpleado.executeUpdate();
                    }

                } else if (esCliente) {

                    try (PreparedStatement psPasajero = con.prepareStatement(sqlPasajero)) {
                        psPasajero.setInt(1, idUsuario);
                        psPasajero.executeUpdate();
                    }
                }

                // Confirma todas las operaciones.
                con.commit();

            } catch (SQLException e) {

                // Revierte todas las operaciones si ocurre algún error.
                con.rollback();
                throw e;
            }
        }
    }

    @Override
    public void baja(int id) throws SQLException {
        String sql = "UPDATE usuarios u " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "SET u.habilitadoUsuario = false " +
                "WHERE u.idUsuario = ? AND r.nombreRol <> ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, "ROOT");

            ps.executeUpdate();
        }
    }

    @Override
    public void modificar(Usuario usuario) throws SQLException {

        String sqlRolActual = "SELECT r.nombreRol, u.idPersona " +
                "FROM usuarios u " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.idUsuario = ? FOR UPDATE";

        String sqlPersona = "UPDATE personas SET nombre = ?, apellido = ?, dni = ?, " +
                "telefono = ?, email = ? WHERE idPersona = ?";

        String sqlUsuario = "UPDATE usuarios SET idRol = ?, nombreUsuario = ? WHERE idUsuario = ?";

        // Elimina la asociación anterior.
        String sqlEliminarEmpleado = "DELETE FROM empleados WHERE idUsuario = ?";
        String sqlEliminarPasajero = "DELETE FROM pasajeros WHERE idUsuario = ?";

        // Crea la nueva asociación.
        String sqlInsertarEmpleado = "INSERT INTO empleados (idUsuario) VALUES (?)";
        String sqlInsertarPasajero = "INSERT INTO pasajeros (idUsuario) VALUES (?)";

        String nombreRolActual = null;
        int idPersonaActual;
        String nombreRolNuevo = null;

        if (usuario.getRol() == null) {
            throw new SQLException("El usuario no tiene un rol asignado.");
        }

        try (Connection con = ConexionDB.conectar()) {

            // Inicia una transacción para realizar las modificaciones.
            con.setAutoCommit(false);

            try {

                // Verifica el rol actual del usuario en la base de datos.

                try (PreparedStatement psRol = con.prepareStatement(sqlRolActual)) {

                    psRol.setInt(1, usuario.getIdUsuario());

                    try (ResultSet rs = psRol.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("El usuario no existe.");
                        }

                        // Impide modificar el usuario ROOT.
                        if ("ROOT".equalsIgnoreCase(rs.getString("nombreRol"))) {
                            throw new SQLException("El usuario ROOT no puede modificarse.");
                        }

                        nombreRolActual = rs.getString("nombreRol");

                        idPersonaActual = rs.getInt("idPersona");

                        if (rs.wasNull()) {
                            throw new SQLException("El usuario no tiene una persona asociada.");
                        }
                    }
                }

                // Verifica que el usuario tenga una Persona asociada.
                if (usuario.getPersona() == null) {
                    throw new SQLException("El usuario debe tener una persona asociada.");
                }

                // Verifica que se esté modificando la Persona correspondiente al usuario.
                if (usuario.getPersona().getIdPersona() != idPersonaActual) {
                    throw new SQLException("La persona asociada al usuario no coincide.");
                }

                // Verifica que el rol seleccionado exista y no sea ROOT.
                String sqlRolNuevo = "SELECT nombreRol FROM roles WHERE idRol = ?";

                try (PreparedStatement psRolNuevo = con.prepareStatement(sqlRolNuevo)) {

                    psRolNuevo.setInt(1, usuario.getRol().getIdRol());

                    try (ResultSet rs = psRolNuevo.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("El rol seleccionado no existe.");
                        }

                        // Recupera el nombre del nuevo rol.
                        nombreRolNuevo = rs.getString("nombreRol");

                        if ("ROOT".equalsIgnoreCase(rs.getString("nombreRol"))) {
                            throw new SQLException("No se puede asignar el rol ROOT mediante una edición.");
                        }

                        // Comprueba que el rol seleccionado sea válido.
                        if (!"EMPLEADO".equalsIgnoreCase(nombreRolNuevo) &&
                                !"CLIENTE".equalsIgnoreCase(nombreRolNuevo)) {

                            throw new SQLException("El rol seleccionado no es válido.");
                        }
                    }
                }

                // Actualiza los datos personales.
                try (PreparedStatement psPersona = con.prepareStatement(sqlPersona)) {

                    psPersona.setString(1, usuario.getPersona().getNombre());
                    psPersona.setString(2, usuario.getPersona().getApellido());
                    psPersona.setString(3, usuario.getPersona().getDni());
                    psPersona.setString(4, usuario.getPersona().getTelefono());
                    psPersona.setString(5, usuario.getPersona().getEmail());
                    psPersona.setInt(6, idPersonaActual);

                    psPersona.executeUpdate();
                }

                // Actualiza los datos de la cuenta.
                try (PreparedStatement psUsuario = con.prepareStatement(sqlUsuario)) {

                    psUsuario.setInt(1, usuario.getRol().getIdRol());
                    psUsuario.setString(2, usuario.getNombreUsuario());
                    psUsuario.setInt(3, usuario.getIdUsuario());

                    psUsuario.executeUpdate();
                }

                // Comprueba si cambió el rol del usuario.
                if (!nombreRolActual.equalsIgnoreCase(nombreRolNuevo)) {

                    // Si pasa de EMPLEADO a CLIENTE.
                    if ("EMPLEADO".equalsIgnoreCase(nombreRolActual) &&
                            "CLIENTE".equalsIgnoreCase(nombreRolNuevo)) {

                        // Elimina su registro de empleados.
                        try (PreparedStatement psEliminar = con.prepareStatement(sqlEliminarEmpleado)) {
                            psEliminar.setInt(1, usuario.getIdUsuario());
                            psEliminar.executeUpdate();
                        }

                        // Crea su registro de pasajero.
                        try (PreparedStatement psInsertar = con.prepareStatement(sqlInsertarPasajero)) {
                            psInsertar.setInt(1, usuario.getIdUsuario());
                            psInsertar.executeUpdate();
                        }

                        // Si pasa de CLIENTE a EMPLEADO.
                    } else if ("CLIENTE".equalsIgnoreCase(nombreRolActual) &&
                            "EMPLEADO".equalsIgnoreCase(nombreRolNuevo)) {

                        // Elimina su registro de pasajeros.
                        try (PreparedStatement psEliminar = con.prepareStatement(sqlEliminarPasajero)) {
                            psEliminar.setInt(1, usuario.getIdUsuario());
                            psEliminar.executeUpdate();
                        }

                        // Crea su registro de empleado.
                        try (PreparedStatement psInsertar = con.prepareStatement(sqlInsertarEmpleado)) {
                            psInsertar.setInt(1, usuario.getIdUsuario());
                            psInsertar.executeUpdate();
                        }
                    }
                }

                // Actualiza la contraseña únicamente si se ingresó una nueva.
                if (usuario.getPasswordUsuario() != null &&
                        !usuario.getPasswordUsuario().isBlank()) {

                    String sqlPassword = "UPDATE usuarios SET passwordUsuario = ? WHERE idUsuario = ?";

                    try (PreparedStatement psPassword = con.prepareStatement(sqlPassword)) {

                        psPassword.setString(1, Encriptacion.hashearPassword(usuario.getPasswordUsuario()));
                        psPassword.setInt(2, usuario.getIdUsuario());

                        psPassword.executeUpdate();
                    }
                }

                // Confirma todas las modificaciones.
                con.commit();

            } catch (SQLException e) {

                // Revierte todos los cambios si ocurre algún error.
                con.rollback();
                throw e;
            }
        }
    }


    @Override
    public Usuario consultar(int id) throws SQLException {

        String sql = "SELECT u.idUsuario, u.nombreUsuario, u.passwordUsuario, u.habilitadoUsuario, " +
                "p.idPersona, p.nombre, p.apellido, p.dni, p.telefono, p.email, p.habilitadoPersona, " +
                "r.idRol, r.nombreRol, r.habilitadoRol " +
                "FROM usuarios u " +
                "LEFT JOIN personas p ON u.idPersona = p.idPersona " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.idUsuario = ? AND u.habilitadoUsuario = true";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // Recupera los datos personales si existe una Persona asociada.
                    Persona persona = obtenerPersona(rs);

                    // Recupera el rol asignado al usuario.
                    Rol rol = new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );

                    // Construye el usuario con sus entidades relacionadas.
                    return new Usuario(
                            rs.getInt("idUsuario"),
                            persona,
                            rol,
                            rs.getString("nombreUsuario"),
                            rs.getString("passwordUsuario"),
                            rs.getBoolean("habilitadoUsuario")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Usuario> listar() throws SQLException {

        String sql = "SELECT u.idUsuario, u.nombreUsuario, u.habilitadoUsuario, " +
                "p.idPersona, p.nombre, p.apellido, p.dni, p.telefono, p.email, p.habilitadoPersona, " +
                "r.idRol, r.nombreRol, r.habilitadoRol " +
                "FROM usuarios u " +
                "LEFT JOIN personas p ON u.idPersona = p.idPersona " +
                "INNER JOIN roles r ON u.idRol = r.idRol";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Recupera los datos personales si existe una Persona asociada.
                Persona persona = obtenerPersona(rs);

                // Recupera el rol asignado.
                Rol rol = new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombreRol"),
                        rs.getBoolean("habilitadoRol")
                );

                // Construye el usuario con sus entidades relacionadas.
                Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        persona,
                        rol,
                        rs.getString("nombreUsuario"),
                        null,
                        rs.getBoolean("habilitadoUsuario")
                );

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws SQLException {

        String sql = "SELECT u.idUsuario, u.nombreUsuario, u.passwordUsuario, u.habilitadoUsuario, " +
                "p.idPersona, p.nombre, p.apellido, p.dni, p.telefono, p.email, p.habilitadoPersona, " +
                "r.idRol, r.nombreRol, r.habilitadoRol " +
                "FROM usuarios u " +
                "LEFT JOIN personas p ON u.idPersona = p.idPersona " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.nombreUsuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // Recupera los datos personales si existe una Persona asociada.
                    Persona persona = obtenerPersona(rs);

                    // Recupera el rol asignado.
                    Rol rol = new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );

                    // Construye el usuario con sus entidades relacionadas.
                    return new Usuario(
                            rs.getInt("idUsuario"),
                            persona,
                            rol,
                            rs.getString("nombreUsuario"),
                            rs.getString("passwordUsuario"),
                            rs.getBoolean("habilitadoUsuario")
                    );
                }
            }
        }

        return null;
    }

    public int contarUsuarios() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public int contarUsuariosHabilitados() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE habilitadoUsuario = true";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    public boolean autenticarUsuario(String nombreUsuario, String passwordUsuario) throws SQLException{
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);

        if(usuario == null) {
            return false;
        }

        if (!usuario.isHabilitadoUsuario() || !usuario.getRol().isHabilitadoRol()) {
            return false;
        }

        return Encriptacion.verificarPassword(passwordUsuario, usuario.getPasswordUsuario());
    }

    //Autentica al usuario verificando su existencia, estado de habilitación y contraseña.
    // Si la autenticación es correcta, devuelve el objeto Usuario completo; de lo contrario, devuelve null.
    public Usuario obtenerUsuarioAutenticado(String nombreUsuario, String passwordUsuario) throws SQLException {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);

        if (usuario == null) {
            return null;
        }

        if (!usuario.isHabilitadoUsuario() || !usuario.getRol().isHabilitadoRol()) {
            return null;
        }

        if (!Encriptacion.verificarPassword(passwordUsuario, usuario.getPasswordUsuario())) {
            return null;
        }

        return usuario;
    }

    public void cambiarEstado(List<Integer> idsUsuarios, boolean habilitado) throws SQLException {

        if (idsUsuarios.isEmpty()) {
            return;
        }

        String placeholders = String.join(", ", Collections.nCopies(idsUsuarios.size(), "?"));
        //Genera un marcador (?) por cada usuario seleccionado para la cláusula IN.

        String sql = "UPDATE usuarios u " +
                "INNER JOIN roles r ON u.idRol = r.idRol " +
                "SET u.habilitadoUsuario = ? " +
                "WHERE u.idUsuario IN (" + placeholders + ") " +
                "AND UPPER(r.nombreRol) <> ?";

        // Actualiza el estado de los usuarios seleccionados, excluyendo a ROOT.
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, habilitado);

            for (int i = 0; i < idsUsuarios.size(); i++) {
                ps.setInt(i + 2, idsUsuarios.get(i));
            }

            //Excluye al usuario ROOT de la actualización.
            ps.setString(idsUsuarios.size() + 2, "ROOT");

            ps.executeUpdate();
        }
    }

    private Persona obtenerPersona(ResultSet rs) throws SQLException {

        int idPersona = rs.getInt("idPersona");

        // Comprueba si el usuario tiene una persona asociada.
        if (rs.wasNull()) {
            return null;
        }

        return new Persona(
                idPersona,
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getBoolean("habilitadoPersona")
        );
    }
}
