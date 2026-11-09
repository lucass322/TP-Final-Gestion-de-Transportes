package Database;

import Conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Prepara la base de datos al iniciar la aplicación, creando las restricciones necesarias si todavía no existen.
public class InicializadorDB {
    public static void inicializar() throws SQLException {
        try(Connection con = ConexionDB.conectar()) {

            //Consulta el identificador del rol ROOT para no depender de un ID fijo.
            String sql = "SELECT idRol FROM roles WHERE nombreRol = ?";

            int idRolRoot;

            try(PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, "ROOT");

                try(ResultSet rs = ps.executeQuery()) {
                    if(!rs.next()) {
                        //Si el rol ROOT no existe, se interrumpe la inicialización.
                        throw new SQLException("No existe el rol ROOT en la base de datos.");
                    }

                    idRolRoot = rs.getInt("idRol");
                }
            }

            System.out.println("\nRol ROOT encontrado: " + idRolRoot);

            //Se crea una columna calculada que identifica a los usuarios con el rol ROOT
            //y un índice único que impide registrar más de uno.
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

            //GENERATED ALWAYS: MySQL calcula automáticamente el valor.
            //CASE: determina si el usuario tiene el rol ROOT.
            //%d: se reemplaza por el identificador real obtenido desde MySQL.
            //VIRTUAL: la columna se calcula cuando se consulta, sin almacenarse físicamente.
            // UNIQUE INDEX: impide que más de un usuario tenga el valor 1.
            // Los demás usuarios reciben NULL, y MySQL permite múltiples valores NULL en un índice UNIQUE.

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

    //Comprueba si existe el índice único encargado de impedir más de un usuario ROOT.
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

            //Devuelve true si encuentra el índice único y false si no existe.
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
