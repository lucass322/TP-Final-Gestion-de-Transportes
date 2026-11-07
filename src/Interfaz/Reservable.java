package Interfaz;

import Pasajeros.Pasajero;

public interface Reservable {
    boolean reservarAsiento (Pasajero pasajero, String codigoAsiento);
    boolean cancelarReserva(String codigoAsiento);
}
