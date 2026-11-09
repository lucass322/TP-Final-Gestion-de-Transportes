package DAO;

import Conexion.ConexionDB;
import Roles.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolDAO {
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
}
