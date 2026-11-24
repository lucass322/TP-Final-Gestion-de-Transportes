package Viajes.GestorReservas;

import DAO.ReservaDAO;
import Viajes.Reserva;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestoraReservas {

    private List<Reserva> listaReservas;
    private ReservaDAO reservaDAO;

    public GestoraReservas(List<Reserva> listaReservas) {
        this.reservaDAO = new ReservaDAO();
        this.listaReservas = listaReservas != null ? new ArrayList<>(listaReservas) : new ArrayList<>();
    }

    public GestoraReservas() throws SQLException {
        this.reservaDAO = new ReservaDAO();
        this.listaReservas = new ArrayList<>(reservaDAO.listar());
    }

    // ALTA
    public void altaReserva(Reserva reserva) throws SQLException {
        if (reserva == null || !reserva.esValida() || !Reserva.ACTIVA.equals(reserva.getEstado())) {

            throw new IllegalArgumentException("La reserva no es válida.");
        }

        if (!reserva.getViaje().isHabilitadoViaje() || !reserva.getViaje().getVehiculoAsignado().isActivo()) {
            throw new IllegalArgumentException("El viaje o el vehículo no están disponibles.");
        }

        try {
            reservaDAO.alta(reserva);

            listaReservas.add(reserva);

            System.out.println("Reserva registrada correctamente.");

        } catch (SQLException | RuntimeException e) {
            // Si falla el registro, se liberan los asientos reservados en memoria.
            for (String asiento : reserva.getAsientosReservados()) {
                reserva.getViaje().cancelarReserva(asiento);
            }

            throw e;
        }
    }

    // CANCELAR RESERVA
    public void cancelarReserva(int id) throws SQLException {

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

        reservaDAO.baja(id);

        for (String asiento : reserva.getAsientosReservados()) {
            reserva.getViaje().cancelarReserva(asiento);
        }

        reserva.setEstado(Reserva.CANCELADA);

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

        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getIdReserva() == reservaModificada.getIdReserva()) {

                reservaDAO.modificar(reservaModificada);

                listaReservas.set(i, reservaModificada);

                System.out.println("Reserva modificada correctamente.");

                return;
            }
        }

        throw new SQLException("No se encontró la reserva para modificar.");
    }

    // LISTAR
    public List<Reserva> listarReservas() {
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
