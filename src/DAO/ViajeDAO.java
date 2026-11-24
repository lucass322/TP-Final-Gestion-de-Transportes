package DAO;

import Conexion.ConexionDB;
import Vehiculos.Vehiculo;
import Viajes.Recorrido;
import Viajes.Viaje;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


public class ViajeDAO implements iDAO<Viaje> {
    @Override
    public void alta(Viaje viaje) throws SQLException {

        if (viaje == null ||
                viaje.getRecorrido() == null ||
                viaje.getVehiculoAsignado() == null ||
                viaje.getFechaHoraSalida() == null ||
                viaje.getFechaHoraLlegada() == null ||
                !viaje.getFechaHoraLlegada().isAfter(viaje.getFechaHoraSalida())) {

            throw new IllegalArgumentException("Los datos del viaje no son válidos.");
        }

        if (!viaje.getRecorrido().isHabilitadoRecorrido()) {
            throw new IllegalArgumentException("El recorrido seleccionado está deshabilitado.");
        }

        if (!viaje.getVehiculoAsignado().isActivo()) {
            throw new IllegalArgumentException("El vehículo seleccionado está inactivo.");
        }

        String sql = """
                INSERT INTO viajes (
                    idRecorrido,
                    idVehiculo,
                    fechaHoraSalida,
                    fechaHoraLlegada
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, viaje.getRecorrido().getIdRecorrido());
            ps.setInt(2, viaje.getVehiculoAsignado().getId());
            ps.setTimestamp(3, Timestamp.valueOf(viaje.getFechaHoraSalida()));
            ps.setTimestamp(4, Timestamp.valueOf(viaje.getFechaHoraLlegada()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    viaje.setIdViaje(rs.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID del viaje.");
                }
            }
        }
    }

    @Override
    public void baja(int id) throws SQLException {

        String sql = """
            UPDATE viajes
            SET habilitadoViaje = FALSE
            WHERE idViaje = ?
            AND habilitadoViaje = TRUE
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró un viaje habilitado con el ID indicado.");
            }
        }
    }

    public void habilitar(int id) throws SQLException {

        String sql = """
            UPDATE viajes
            SET habilitadoViaje = TRUE
            WHERE idViaje = ?
            AND habilitadoViaje = FALSE
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró un viaje deshabilitado con el ID indicado.");
            }
        }
    }

    @Override
    public void modificar(Viaje viaje) throws SQLException {

        if (viaje == null ||
                viaje.getIdViaje() <= 0 ||
                viaje.getRecorrido() == null ||
                viaje.getVehiculoAsignado() == null ||
                viaje.getFechaHoraSalida() == null ||
                viaje.getFechaHoraLlegada() == null ||
                !viaje.getFechaHoraLlegada().isAfter(viaje.getFechaHoraSalida())) {

            throw new IllegalArgumentException("Los datos del viaje no son válidos.");
        }

        if (!viaje.getRecorrido().isHabilitadoRecorrido()) {
            throw new IllegalArgumentException("El recorrido seleccionado está deshabilitado.");
        }

        if (!viaje.getVehiculoAsignado().isActivo()) {
            throw new IllegalArgumentException("El vehículo seleccionado está inactivo.");
        }

        String sql = """
            UPDATE viajes
            SET idRecorrido = ?,
                idVehiculo = ?,
                fechaHoraSalida = ?,
                fechaHoraLlegada = ?
            WHERE idViaje = ?
            AND habilitadoViaje = TRUE
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, viaje.getRecorrido().getIdRecorrido());
            ps.setInt(2, viaje.getVehiculoAsignado().getId());
            ps.setTimestamp(3, Timestamp.valueOf(viaje.getFechaHoraSalida()));
            ps.setTimestamp(4, Timestamp.valueOf(viaje.getFechaHoraLlegada()));
            ps.setInt(5, viaje.getIdViaje());

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró un viaje habilitado con el ID indicado.");
            }
        }
    }

    @Override
    public Viaje consultar(int id) throws SQLException {

        String sql = """
            SELECT idViaje, idRecorrido, idVehiculo,
                   fechaHoraSalida, fechaHoraLlegada, habilitadoViaje
            FROM viajes
            WHERE idViaje = ?
            """;

        int idRecorrido;
        int idVehiculo;
        LocalDateTime fechaHoraSalida;
        LocalDateTime fechaHoraLlegada;
        boolean habilitadoViaje;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                idRecorrido = rs.getInt("idRecorrido");
                idVehiculo = rs.getInt("idVehiculo");

                fechaHoraSalida = rs.getTimestamp("fechaHoraSalida").toLocalDateTime();
                fechaHoraLlegada = rs.getTimestamp("fechaHoraLlegada").toLocalDateTime();

                habilitadoViaje = rs.getBoolean("habilitadoViaje");
            }
        }

        Recorrido recorrido = new RecorridoDAO().consultar(idRecorrido);
        Vehiculo vehiculo = new VehiculoDAO().consultar(idVehiculo);

        if (recorrido == null || vehiculo == null) {
            throw new SQLException("No se pudo recuperar el recorrido o el vehículo del viaje.");
        }

        Viaje viaje = new Viaje(
                id,
                recorrido,
                fechaHoraSalida,
                vehiculo
        );


        viaje.setFechaHoraLlegada(fechaHoraLlegada);

        viaje.setHabilitadoViaje(habilitadoViaje);

        String sqlAsientos = """
        SELECT ar.codigoAsiento
        FROM asientos_reserva ar
        INNER JOIN reservas r ON ar.idReserva = r.idReserva
        WHERE r.idViaje = ?
        AND r.estado = 'ACTIVA'
        """;

        Set<String> asientosOcupados = new HashSet<>();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sqlAsientos)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    asientosOcupados.add(rs.getString("codigoAsiento"));
                }
            }
        }

        viaje.setAsientosOcupados(asientosOcupados);

        return viaje;
    }

    @Override
    public List<Viaje> listar() throws SQLException {

        List<Viaje> viajes = new ArrayList<>();
        List<Integer> idsViajes = new ArrayList<>();

        String sql = """
            SELECT idViaje
            FROM viajes
            ORDER BY fechaHoraSalida
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                idsViajes.add(rs.getInt("idViaje"));
            }
        }

        for (int id : idsViajes) {
            Viaje viaje = consultar(id);

            if (viaje != null) {
                viajes.add(viaje);
            }
        }

        return viajes;
    }
}
