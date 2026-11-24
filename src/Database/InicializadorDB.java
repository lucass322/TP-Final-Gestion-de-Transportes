package Database;

import Conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class InicializadorDB {
    public static void inicializar() throws SQLException {
        try(Connection con = ConexionDB.conectar()) {
            String sql = "SELECT idRol FROM roles WHERE nombreRol = ?";

            int idRolRoot;

            try(PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "ROOT");

                try(ResultSet rs = ps.executeQuery()) {
                    if(!rs.next()) {
                        throw new SQLException("No existe el rol ROOT en la base de datos.");
                    }

                    idRolRoot = rs.getInt("idRol");
                }
            }

            System.out.println("\nRol ROOT encontrado: " + idRolRoot);
            String sqlRestriccion = """
                    ALTER TABLE usuarios
                    ADD COLUMN rootUnico TINYINT
                    GENERATED ALWAYS AS (
                        CASE
                            WHEN idRol = %d THEN 1
                            ELSE NULL
                        END
                    ) VIRTUAL,
                    ADD UNIQUE INDEX unico_root (rootUnico)
                    """.formatted(idRolRoot);
            if (!existeRestriccion(con)) {
                try (PreparedStatement psRestriccion = con.prepareStatement(sqlRestriccion)) {
                    psRestriccion.executeUpdate();

                    System.out.println("Restricción ROOT creada correctamente.");
                }
            }else{
                System.out.println("La restricción ROOT ya existe.");
            }
        }
    }
    private static boolean existeRestriccion(Connection con) throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME = 'usuarios'
            AND INDEX_NAME = 'unico_root'
            AND NON_UNIQUE = 0
            """;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
