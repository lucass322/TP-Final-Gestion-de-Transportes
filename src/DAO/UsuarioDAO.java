package DAO;

import Conexion.ConexionDB;
import Roles.Rol;
import Usuarios.Usuario;
import Utilidades.Encriptacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        String sql = "SElECT email FROM usuarios WHERE email = ?";

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
        String sql = "SELECT email FROM usuarios WHERE email = ? AND idUsuario != ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void alta(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (idRol, nombreUsuario, passwordUsuario, email) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuario.getRol().getIdRol());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, Encriptacion.hashearPassword(usuario.getPasswordUsuario()));
            ps.setString(4, usuario.getEmail());

            ps.executeUpdate();
        }
    }

    @Override
    public void baja(int id) throws SQLException {
        String sql = "UPDATE usuarios SET habilitadoUsuario = false WHERE idUsuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void modificar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET idRol = ?, nombreUsuario = ?, email = ? WHERE idUsuario = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuario.getRol().getIdRol());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getEmail());
            ps.setInt(4, usuario.getIdUsuario());

            if(usuario.getPasswordUsuario() != null && !usuario.getPasswordUsuario().isBlank()) {
                String sqlPassword = "UPDATE usuarios SET passwordUsuario = ? WHERE idUsuario = ?";

                try (PreparedStatement psPassword = con.prepareStatement(sqlPassword)) {
                    psPassword.setString(1, Encriptacion.hashearPassword(usuario.getPasswordUsuario()));
                    psPassword.setInt(2, usuario.getIdUsuario());

                    psPassword.executeUpdate();
                }
            }

            ps.executeUpdate();
        }
    }

    @Override
    public Usuario consultar(int id) throws SQLException {
        String sql = "SELECT u.idUsuario, u.nombreUsuario, u.passwordUsuario, u.email, u.habilitadoUsuario, " +
                "r.idRol, r.nombreRol, r.habilitadoRol " +
                "FROM usuarios u INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.idUsuario = ? AND u.habilitadoUsuario = true";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );

                    return new Usuario(
                            rs.getInt("idUsuario"),
                            rol,
                            rs.getString("nombreUsuario"),
                            rs.getString("passwordUsuario"),
                            rs.getString("email"),
                            rs.getBoolean("habilitadoUsuario")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT u.idUsuario, u.nombreUsuario, u.email, u.habilitadoUsuario, " +
                "r.idRol, r.nombreRol, r.habilitadoRol " +
                "FROM usuarios u INNER JOIN roles r ON u.idRol = r.idRol";

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombreRol"),
                        rs.getBoolean("habilitadoRol")
                );

                Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        rol,
                        rs.getString("nombreUsuario"),
                        null,
                        rs.getString("email"),
                        rs.getBoolean("habilitadoUsuario")
                );

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT u.idUsuario, u.nombreUsuario, u.passwordUsuario, u.email, u.habilitadoUsuario, " +
                "r.idRol, r.nombreRol, r.habilitadoRol " +
                "FROM usuarios u INNER JOIN roles r ON u.idRol = r.idRol " +
                "WHERE u.nombreUsuario = ?";

        try (Connection con = ConexionDB.conectar();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    Rol rol = new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );

                    Usuario usuario = new Usuario(
                            rs.getInt("idUsuario"),
                            rol,
                            rs.getString("nombreUsuario"),
                            rs.getString("passwordUsuario"),
                            rs.getString("email"),
                            rs.getBoolean("habilitadoUsuario")
                    );

                    return usuario;
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
}
