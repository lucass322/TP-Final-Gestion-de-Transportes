package DAO;

import Viajes.Recorrido;
import Conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecorridoDAO implements iDAO<Recorrido> {
    @Override
    public void alta(Recorrido recorrido) throws SQLException {
        if (recorrido == null ||
                recorrido.getOrigen() == null ||
                recorrido.getOrigen().isBlank() ||
                recorrido.getDestino() == null ||
                recorrido.getDestino().isBlank() ||
                recorrido.getDistanciaKM() <= 0) {

            throw new IllegalArgumentException("Los datos del recorrido no son válidos.");
        }

        String sql = """
                INSERT INTO recorridos (origen, destino, distanciaKM)
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, recorrido.getOrigen());
            ps.setString(2, recorrido.getDestino());
            ps.setDouble(3, recorrido.getDistanciaKM());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    recorrido.setIdRecorrido(rs.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del recorrido.");
                }
            }
        }
    }

    @Override
    public void baja(int id) throws SQLException {

        String sql = """
            UPDATE recorridos
            SET habilitadoRecorrido = FALSE
            WHERE idRecorrido = ?
            AND habilitadoRecorrido = TRUE
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró un recorrido habilitado con el ID indicado.");
            }
        }
    }

    public void habilitar(int id) throws SQLException {

        String sql = """
            UPDATE recorridos
            SET habilitadoRecorrido = TRUE
            WHERE idRecorrido = ?
            AND habilitadoRecorrido = FALSE
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró un recorrido deshabilitado con el ID indicado.");
            }
        }
    }

    @Override
    public void modificar(Recorrido recorrido) throws SQLException {

        if (recorrido == null ||
                recorrido.getIdRecorrido() <= 0 ||
                recorrido.getOrigen() == null ||
                recorrido.getOrigen().isBlank() ||
                recorrido.getDestino() == null ||
                recorrido.getDestino().isBlank() ||
                recorrido.getDistanciaKM() <= 0) {

            throw new IllegalArgumentException("Los datos del recorrido no son válidos.");
        }

        String sql = """
            UPDATE recorridos
            SET origen = ?,
                destino = ?,
                distanciaKM = ?
            WHERE idRecorrido = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, recorrido.getOrigen());
            ps.setString(2, recorrido.getDestino());
            ps.setDouble(3, recorrido.getDistanciaKM());
            ps.setInt(4, recorrido.getIdRecorrido());

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró el recorrido indicado.");
            }
        }
    }

    @Override
    public Recorrido consultar(int id) throws SQLException {

        String sql = """
            SELECT idRecorrido, origen, destino, distanciaKM, habilitadoRecorrido
            FROM recorridos
            WHERE idRecorrido = ?
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Recorrido recorrido = new Recorrido(
                            rs.getInt("idRecorrido"),
                            rs.getString("origen"),
                            rs.getString("destino"),
                            rs.getDouble("distanciaKM")
                    );

                    recorrido.setHabilitadoRecorrido(rs.getBoolean("habilitadoRecorrido"));

                    return recorrido;
                }
            }
        }

        return null;
    }

    @Override
    public List<Recorrido> listar() throws SQLException {

        List<Recorrido> recorridos = new ArrayList<>();

        String sql = """
            SELECT idRecorrido, origen, destino, distanciaKM, habilitadoRecorrido
            FROM recorridos
            ORDER BY idRecorrido
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Recorrido recorrido = new Recorrido(
                        rs.getInt("idRecorrido"),
                        rs.getString("origen"),
                        rs.getString("destino"),
                        rs.getDouble("distanciaKM")
                );

                recorrido.setHabilitadoRecorrido(rs.getBoolean("habilitadoRecorrido"));

                recorridos.add(recorrido);
            }
        }

        return recorridos;
    }
}
