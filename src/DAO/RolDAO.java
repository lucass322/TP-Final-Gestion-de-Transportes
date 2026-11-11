package DAO;

import Conexion.ConexionDB;
import Roles.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAO implements iDAO<Rol>{
    public Rol buscarRolPorNombre(String nombreRol) throws SQLException {

        String sql = "SELECT idRol, nombreRol, habilitadoRol FROM roles WHERE nombreRol = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreRol);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );
                }
            }
        }

        return null;
    }

    public int contarRoles() throws SQLException {
        String sql = "SELECT COUNT(*) FROM roles";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    @Override
    public void alta(Rol rol) throws SQLException {
        if ("ROOT".equalsIgnoreCase(rol.getNombreRol())) { //excluye ROOT
            throw new IllegalArgumentException("El nombre ROOT está reservado.");
        }

        Rol rolExistente = buscarRolPorNombre(rol.getNombreRol());

        if (rolExistente != null) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre.");
        }

        String sql = "INSERT INTO roles (nombreRol, habilitadoRol) VALUES (?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rol.getNombreRol());
            ps.executeUpdate();
        }
    }

    @Override
    public void baja(int id) throws SQLException {
        String sql = "UPDATE roles SET habilitadoRol = false WHERE idRol = ? AND nombreRol <> 'ROOT'"; //excluye ROOT

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void modificar(Rol rol) throws SQLException {
        if ("ROOT".equalsIgnoreCase(rol.getNombreRol())) { //evita la asignación a otro rol
            throw new IllegalArgumentException("No se puede asignar el nombre ROOT a otro rol.");
        }

        Rol rolActual = consultar(rol.getIdRol());

        if (rolActual != null && "ROOT".equalsIgnoreCase(rolActual.getNombreRol())) {
            throw new IllegalArgumentException("No se puede modificar el rol ROOT."); //evita modificar el rol root
        }

        Rol rolExistente = buscarRolPorNombre(rol.getNombreRol());

        if (rolExistente != null && rolExistente.getIdRol() != rol.getIdRol()) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre.");
        }

        String sql = "UPDATE roles SET nombreRol = ? WHERE idRol = ? AND nombreRol <> 'ROOT'"; //excluye ROOT

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rol.getNombreRol());
            ps.setInt(2, rol.getIdRol());

            ps.executeUpdate();
        }
    }

    @Override
    public Rol consultar(int id) throws SQLException {
        String sql = "SELECT idRol, nombreRol, habilitadoRol " +
                "FROM roles WHERE idRol = ? AND habilitadoRol = true";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombreRol"),
                            rs.getBoolean("habilitadoRol")
                    );
                }
            }
        }

        return null;
    }

    @Override
    public List<Rol> listar() throws SQLException {
        List<Rol> roles = new ArrayList<>();

        String sql = """
            SELECT idRol, nombreRol, habilitadoRol
            FROM roles
            WHERE habilitadoRol = TRUE
            AND nombreRol <> 'ROOT'
            """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombreRol"),
                        rs.getBoolean("habilitadoRol")
                ));
            }
        }

        return roles;
    }
}
