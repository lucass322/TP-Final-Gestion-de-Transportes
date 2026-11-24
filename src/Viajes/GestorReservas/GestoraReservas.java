package Viajes.GestorReservas;

import DAO.ReservaDAO;
import Viajes.Reserva;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestoraReservas {

    private List<Reserva> listaReservas;
    private ReservaDAO reservaDAO;
    private Integer idUsuarioFiltro;

    public GestoraReservas(List<Reserva> listaReservas) {
        this.reservaDAO = new ReservaDAO();
        this.listaReservas = listaReservas != null ? new ArrayList<>(listaReservas) : new ArrayList<>();
    }

    public GestoraReservas() {
        this.reservaDAO = new ReservaDAO();
        this.listaReservas = new ArrayList<>();
    }

    // ALTA
    public void altaReserva(Reserva reserva) throws SQLException {
        if (reserva == null || reserva.getViaje() == null || reserva.getViaje().getVehiculoAsignado() == null) {
            throw new IllegalArgumentException("La reserva, el viaje o el vehículo no son válidos.");
        }

        if (!reserva.esValida() || !Reserva.ACTIVA.equals(reserva.getEstado())) {
            throw new IllegalArgumentException("La reserva no es válida.");
        }

        if (!reserva.getViaje().isHabilitadoViaje() || !reserva.getViaje().getVehiculoAsignado().isActivo()) {
            throw new IllegalArgumentException("El viaje o el vehículo no están disponibles.");
        }


        try {
            reservaDAO.alta(reserva);
        } catch (SQLException | RuntimeException e) {
            // Si falla el registro, se liberan los asientos en memoria.
            for (String asiento : reserva.getAsientosReservados()) {
                reserva.getViaje().cancelarReserva(asiento);
            }

            throw e;
        }

        actualizarLista();

        System.out.println("Reserva registrada correctamente.");
    }


    public void altaReserva(Reserva reserva, int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario no es válido.");
        }

        if (reserva == null || reserva.getPasajero() == null) {
            throw new IllegalArgumentException("La reserva o el pasajero no son válidos.");
        }

        if (reserva.getPasajero().getIdUsuario() != idUsuario) {
            throw new IllegalArgumentException("No tenés permiso para reservar para otro pasajero."
            );
        }

        idUsuarioFiltro = idUsuario;

        altaReserva(reserva);
    }


    // CANCELAR RESERVA
    public void cancelarReserva(int id) throws SQLException {
        cancelarReservaInterno(id, null);
    }

    public void cancelarReserva(int idReserva, int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario no es válido.");
        }

        idUsuarioFiltro = idUsuario;

        cancelarReservaInterno(idReserva, idUsuario);
    }

    private void cancelarReservaInterno(int id, Integer idUsuario) throws SQLException {
        Reserva reserva = buscarReservaPorId(id);

        if (reserva == null || !Reserva.ACTIVA.equals(reserva.getEstado())) {
            throw new SQLException("La reserva no existe o ya está cancelada.");
        }

        // Comprobar que los asientos estén ocupados.
        for (String asiento : reserva.getAsientosReservados()) {
            if (!reserva.getViaje().getAsientosOcupados().contains(asiento)) {
                throw new SQLException("No se puede cancelar la reserva porque sus asientos no están ocupados.");
            }
        }

        // La cancelación en la BD también comprueba la pertenencia al usuario.
        if (idUsuario == null) {
            reservaDAO.baja(id);
        } else {
            reservaDAO.baja(id, idUsuario);
        }

        // Liberar los asientos después de cancelar correctamente en la BD.
        for (String asiento : reserva.getAsientosReservados()) {
            reserva.getViaje().cancelarReserva(asiento);
        }

        reserva.setEstado(Reserva.CANCELADA);

        actualizarLista();

        System.out.println("Reserva cancelada correctamente. ID: " + id);
    }


    // MODIFICAR RESERVA
    public void modificarReserva(Reserva reservaModificada) throws SQLException {
        if (reservaModificada == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula.");
        }

        if (!Reserva.ACTIVA.equals(reservaModificada.getEstado())) {
            throw new IllegalArgumentException("No se puede modificar una reserva cancelada.");
        }

        reservaDAO.modificar(reservaModificada);

        actualizarLista();

        System.out.println("Reserva modificada correctamente.");
    }


    private void actualizarLista() throws SQLException {
        if (idUsuarioFiltro != null) {
            listaReservas = new ArrayList<>(reservaDAO.listarPorUsuario(idUsuarioFiltro));
        } else {
            listaReservas = new ArrayList<>(reservaDAO.listar());
        }
    }

    public void modificarReserva(Reserva reserva, int idUsuario) throws SQLException {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula.");
        }

        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario no es válido.");
        }

        idUsuarioFiltro = idUsuario;

        reservaDAO.modificar(reserva, idUsuario);

        actualizarLista();
    }


    // LISTAR
    public List<Reserva> listarReservas() throws SQLException {
        idUsuarioFiltro = null;
        listaReservas = new ArrayList<>(reservaDAO.listar());

        return new ArrayList<>(listaReservas);
    }

    // LISTAR RESERVAS DE UN USUARIO
    public List<Reserva> listarReservasPorUsuario(int idUsuario) throws SQLException {

        idUsuarioFiltro = idUsuario;

        listaReservas = new ArrayList<>( reservaDAO.listarPorUsuario(idUsuario));

        return new ArrayList<>(listaReservas);
    }

    // BUSCAR POR ID
    private Reserva buscarReservaPorId(int id) {
        for (Reserva reserva : listaReservas) {

            if (reserva.getIdReserva() == id) {
                return reserva;
            }
        }

        return null;
    }
}
