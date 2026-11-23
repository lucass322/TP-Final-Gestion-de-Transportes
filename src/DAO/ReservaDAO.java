package DAO;

import Conexion.ConexionDB;
import Viajes.Reserva;
import Viajes.Viaje;
import Pasajeros.Pasajero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

public class ReservaDAO implements iDAO<Reserva> {
    @Override
    public void alta(Reserva reserva) throws SQLException {

        if (reserva == null || !reserva.esValida() || !Reserva.ACTIVA.equals(reserva.getEstado())) {
            throw new IllegalArgumentException("La reserva no es válida.");
        }

        String sqlReserva = """
            INSERT INTO reservas (idPasajero, idViaje, fechaReserva, estado)
            VALUES (?, ?, ?, ?)
            """;

        String sqlAsientos = """
            INSERT INTO asientos_reserva (idReserva, codigoAsiento)
            VALUES (?, ?)
            """;

        try (Connection conexion = ConexionDB.conectar()) {

            conexion.setAutoCommit(false);

            try {
                int idReserva;

                // Inserta los datos generales de la reserva.
                try (PreparedStatement ps = conexion.prepareStatement(
                        sqlReserva, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, reserva.getPasajero().getIdPasajero());
                    ps.setInt(2, reserva.getViaje().getIdViaje());
                    ps.setTimestamp(3, Timestamp.valueOf(reserva.getFechaReserva()));
                    ps.setString(4, reserva.getEstado());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se pudo obtener el ID de la reserva.");
                        }

                        idReserva = rs.getInt(1);
                    }
                }

                // Inserta los asientos asociados a la reserva.
                try (PreparedStatement ps = conexion.prepareStatement(sqlAsientos)) {

                    for (String asiento : reserva.getAsientosReservados()) {
                        ps.setInt(1, idReserva);
                        ps.setString(2, asiento);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                conexion.commit();

                reserva.setIdReserva(idReserva);

            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    @Override
    public void baja(int id) throws SQLException {

        String sql = """
            UPDATE reservas
            SET estado = 'CANCELADA'
            WHERE idReserva = ?
            AND estado = 'ACTIVA'
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasActualizadas = ps.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException(
                        "No se encontró una reserva activa con el ID indicado."
                );
            }
        }
    }

    @Override
    public void modificar(Reserva reserva) throws SQLException {

        if (reserva == null ||
                reserva.getIdReserva() <= 0 ||
                !reserva.esValida() ||
                !Reserva.ACTIVA.equals(reserva.getEstado())) {

            throw new IllegalArgumentException("Los datos de la reserva no son válidos.");
        }

        if (reserva.getViaje().getVehiculoAsignado() == null ||
                !reserva.getViaje().isHabilitadoViaje()) {

            throw new IllegalArgumentException("El viaje no está disponible.");
        }

        // Normaliza y valida los nuevos asientos.
        List<String> nuevosAsientos = new ArrayList<>();
        Set<String> asientosSolicitados = new HashSet<>();

        for (String asiento : reserva.getAsientosReservados()) {

            if (asiento == null || asiento.isBlank() || asiento.length() > 10) {
                throw new IllegalArgumentException("Hay un código de asiento inválido.");
            }

            String codigo = asiento.trim().toUpperCase(Locale.ROOT);

            if (!asientosSolicitados.add(codigo)) {
                throw new IllegalArgumentException("No se pueden repetir asientos.");
            }

            nuevosAsientos.add(codigo);
        }

        if (nuevosAsientos.size() >
                reserva.getViaje().getVehiculoAsignado().getCapacidadPasajeros()) {

            throw new IllegalArgumentException("Se supera la capacidad del vehículo.");
        }

        String sqlReserva = """
            SELECT idPasajero, idViaje, estado
            FROM reservas
            WHERE idReserva = ?
            FOR UPDATE
            """;

        String sqlOcupados = """
            SELECT ar.codigoAsiento
            FROM asientos_reserva ar
            INNER JOIN reservas r ON ar.idReserva = r.idReserva
            WHERE r.idViaje = ?
            AND r.estado = 'ACTIVA'
            AND r.idReserva <> ?
            FOR UPDATE
            """;

        String sqlEliminar = """
            DELETE FROM asientos_reserva
            WHERE idReserva = ?
            """;

        String sqlInsertar = """
            INSERT INTO asientos_reserva (idReserva, codigoAsiento)
            VALUES (?, ?)
            """;

        try (Connection conexion = ConexionDB.conectar()) {

            conexion.setAutoCommit(false);

            try {

                int idPasajeroActual;
                int idViajeActual;

                // Verifica que la reserva exista y siga activa.
                try (PreparedStatement ps = conexion.prepareStatement(sqlReserva)) {

                    ps.setInt(1, reserva.getIdReserva());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("No se encontró la reserva indicada.");
                        }

                        idPasajeroActual = rs.getInt("idPasajero");
                        idViajeActual = rs.getInt("idViaje");

                        if (!Reserva.ACTIVA.equals(rs.getString("estado"))) {
                            throw new SQLException("La reserva no está activa.");
                        }
                    }
                }

                // El pasajero y el viaje deben ser los originales.
                if (idPasajeroActual != reserva.getPasajero().getIdPasajero() ||
                        idViajeActual != reserva.getViaje().getIdViaje()) {

                    throw new IllegalArgumentException("No se puede cambiar el pasajero ni el viaje de una reserva.");
                }

                // Recupera los asientos ocupados por otras reservas.
                Set<String> asientosOcupados = new HashSet<>();

                try (PreparedStatement ps = conexion.prepareStatement(sqlOcupados)) {

                    ps.setInt(1, idViajeActual);
                    ps.setInt(2, reserva.getIdReserva());

                    try (ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            asientosOcupados.add(
                                    rs.getString("codigoAsiento").toUpperCase(Locale.ROOT)
                            );
                        }
                    }
                }

                // Comprueba que los nuevos asientos estén disponibles.
                for (String asiento : nuevosAsientos) {

                    if (asientosOcupados.contains(asiento)) {
                        throw new SQLException("El asiento " + asiento + " ya está reservado.");
                    }
                }

                // Elimina los asientos anteriores.
                try (PreparedStatement ps = conexion.prepareStatement(sqlEliminar)) {

                    ps.setInt(1, reserva.getIdReserva());
                    ps.executeUpdate();
                }

                // Registra los nuevos asientos.
                try (PreparedStatement ps = conexion.prepareStatement(sqlInsertar)) {

                    for (String asiento : nuevosAsientos) {

                        ps.setInt(1, reserva.getIdReserva());
                        ps.setString(2, asiento);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                conexion.commit();

                // Sincroniza los objetos en memoria.
                reserva.setAsientosReservados(nuevosAsientos);

                asientosOcupados.addAll(nuevosAsientos);
                reserva.getViaje().setAsientosOcupados(asientosOcupados);

            } catch (SQLException | RuntimeException e) {

                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    @Override
    public Reserva consultar(int id) throws SQLException {

        String sqlReserva = """
            SELECT idPasajero, idViaje, fechaReserva, estado
            FROM reservas
            WHERE idReserva = ?
            """;

        int idPasajero;
        int idViaje;
        LocalDateTime fechaReserva;
        String estado;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sqlReserva)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                idPasajero = rs.getInt("idPasajero");
                idViaje = rs.getInt("idViaje");
                fechaReserva = rs.getTimestamp("fechaReserva").toLocalDateTime();
                estado = rs.getString("estado");
            }
        }

        // Recupera el pasajero asociado.
        Pasajero pasajero = new PasajeroDAO().consultar(idPasajero);

        // Recupera el viaje asociado.
        Viaje viaje = new ViajeDAO().consultar(idViaje);

        if (pasajero == null || viaje == null) {
            throw new SQLException("No se pudo recuperar el pasajero o el viaje de la reserva.");
        }

        // Recupera los asientos de la reserva.
        List<String> asientos = new ArrayList<>();

        String sqlAsientos = """
            SELECT codigoAsiento
            FROM asientos_reserva
            WHERE idReserva = ?
            ORDER BY codigoAsiento
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sqlAsientos)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    asientos.add(rs.getString("codigoAsiento"));
                }
            }
        }

        return new Reserva(
                id,
                pasajero,
                viaje,
                asientos,
                fechaReserva,
                estado
        );
    }

    @Override
    public List<Reserva> listar() throws SQLException {

        List<Reserva> reservas = new ArrayList<>();
        List<Integer> idsReservas = new ArrayList<>();

        String sql = """
            SELECT idReserva
            FROM reservas
            ORDER BY fechaReserva DESC
            """;

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                idsReservas.add(rs.getInt("idReserva"));
            }
        }

        for (int id : idsReservas) {
            Reserva reserva = consultar(id);

            if (reserva != null) {
                reservas.add(reserva);
            }
        }

        return reservas;
    }
}
