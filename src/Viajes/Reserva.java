package Viajes;

import Pasajeros.Pasajero;

import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class Reserva {
    private int idReserva;
    private Pasajero pasajero;
    private Viaje viaje;
    private List<String> asientosReservados;
    private LocalDateTime fechaReserva;
    private String estado;
    private String mensajeError;

    public static final String ACTIVA = "ACTIVA";
    public static final String CANCELADA = "CANCELADA";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Reserva() {
        this.asientosReservados = new ArrayList<>();
        this.fechaReserva = LocalDateTime.now();
        this.estado = ACTIVA;
    }

    public Reserva(int idReserva, Pasajero pasajero, Viaje viaje, List<String> asientosReservados,
                   LocalDateTime fechaReserva, String estado) {

        this.idReserva = idReserva;
        this.pasajero = pasajero;
        this.viaje = viaje;
        this.asientosReservados = asientosReservados != null ? new ArrayList<>(asientosReservados) : new ArrayList<>();
        this.fechaReserva = fechaReserva != null ? fechaReserva : LocalDateTime.now();
        setEstado(estado);
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public List<String> getAsientosReservados() {
        return new ArrayList<>(asientosReservados);
    }

    public void setAsientosReservados(List<String> asientosReservados) {
        this.asientosReservados = asientosReservados != null ? new ArrayList<>(asientosReservados) : new ArrayList<>();
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public String getFechaReservaFormateada() {
        return fechaReserva != null ? fechaReserva.format(FORMATO_FECHA) : "";
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (ACTIVA.equals(estado) || CANCELADA.equals(estado)) {
            this.estado = estado;
        } else {
            throw new IllegalArgumentException("Estado de reserva inválido.");
        }
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public boolean esValida() {
        return pasajero != null
                && viaje != null
                && asientosReservados != null
                && !asientosReservados.isEmpty();
    }

    public boolean reservarAsientos(List<String> codigosAsientos) {
        if (!ACTIVA.equals(estado) || pasajero == null || viaje == null || codigosAsientos == null || codigosAsientos.isEmpty()) {
            return false;
        }

        mensajeError = null;

        for (String codigo : codigosAsientos) {
            if (codigo == null || codigo.isBlank()) {
                return false;
            }
        }

        Set<String> asientosSolicitados = new HashSet<>();

        for (String codigo : codigosAsientos) {
            String codigoNormalizado = codigo.trim().toUpperCase(Locale.ROOT);

            if (!asientosSolicitados.add(codigoNormalizado)) {
                return false;
            }

            if (asientosReservados.contains(codigoNormalizado)) {
                return false;
            }
        }

        List<String> reservados = new ArrayList<>();

        for (String codigo : codigosAsientos) {

            String codigoNormalizado = codigo.trim().toUpperCase(Locale.ROOT);

            String error = viaje.validarDisponibilidadAsiento(pasajero, codigoNormalizado);

            if (error != null) {
                mensajeError = error;

                for (String asiento : reservados) {
                    viaje.cancelarReserva(asiento);
                }

                return false;
            }

            if (!viaje.reservarAsiento(pasajero, codigoNormalizado)) {
                mensajeError = "No se pudo reservar el asiento " + codigoNormalizado + ".";

                for (String asiento : reservados) {
                    viaje.cancelarReserva(asiento);
                }

                return false;
            }

            reservados.add(codigoNormalizado);
        }
        asientosReservados.addAll(reservados);
        return true;
    }

    public boolean cancelarReserva() {
        if (!ACTIVA.equals(estado) || viaje == null || asientosReservados == null || asientosReservados.isEmpty()) {
            return false;
        }
        for (String asiento : asientosReservados) {
            if (!viaje.getAsientosOcupados().contains(asiento)) {
                return false;
            }
        }

        for (String asiento : asientosReservados) {
            if (!viaje.cancelarReserva(asiento)) {
                return false;
            }
        }

        setEstado(CANCELADA);
        return true;
    }
}
