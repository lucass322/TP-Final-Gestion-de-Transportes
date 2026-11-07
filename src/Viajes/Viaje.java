package Viajes;

import Excepciones.PasajeroExistenteExcepcion;
import Interfaz.Reservable;
import Pasajeros.Pasajero;
import Vehiculos.Vehiculo;

import java.util.*;

public class Viaje implements Reservable {
    private int idViaje;
    private String origen;
    private String destino;
    private String fechaHoraSalida;
    private Vehiculo vehiculoAsignado;
    private List<Pasajero> pasajeros;
    private Set<String> asientosOcupados;
    private Map<String, Viaje> historialViajes;

    public Viaje(int idViaje, String origen, String destino, String fechaHoraSalida, Vehiculo vehiculoAsignado) {
        this.idViaje = idViaje;
        this.origen = origen;
        this.destino = destino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.vehiculoAsignado = vehiculoAsignado;
        this.pasajeros = new ArrayList<>();
        this.asientosOcupados = new HashSet<>();
        this.historialViajes = new HashMap<>();
    }

    public Viaje() {
        this.pasajeros = new ArrayList<>();
        this.asientosOcupados = new HashSet<>();
        this.historialViajes = new HashMap<>();
    }

    public int getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(String fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public Vehiculo getVehiculoAsignado() {
        return vehiculoAsignado;
    }

    public void setVehiculoAsignado(Vehiculo vehiculoAsignado) {
        this.vehiculoAsignado = vehiculoAsignado;
    }

    public List<Pasajero> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<Pasajero> pasajeros) {
        this.pasajeros = pasajeros;
    }

    public Set<String> getAsientosOcupados() {
        return asientosOcupados;
    }

    public void setAsientosOcupados(Set<String> asientosOcupados) {
        this.asientosOcupados = asientosOcupados;
    }

    public Map<String, Viaje> getHistorialViajes() {
        return historialViajes;
    }

    public void setHistorialViajes(Map<String, Viaje> historialViajes) {
        this.historialViajes = historialViajes;
    }


    public void agregarPasajero(Pasajero pasajero) throws PasajeroExistenteExcepcion {
        if (pasajero != null && !pasajeros.contains(pasajero)) {
            pasajeros.add(pasajero);
            System.out.println("Pasajero " + pasajero.getNombre() + " agregado al viaje.");
        } else {
            throw new PasajeroExistenteExcepcion("El pasajero ya está en el viaje o es inválido.");
        }
    }

    @Override
    public boolean reservarAsiento(Pasajero pasajero, String codigoAsiento) {
        if (!asientosOcupados.contains(codigoAsiento)) {
            asientosOcupados.add(codigoAsiento);
            System.out.println("Asiento " + codigoAsiento + " reservado para " + pasajero.getNombre());
            return true;
        }
        System.out.println("El asiento ya está ocupado.");
        return false;
    }

    @Override
    public boolean cancelarReserva(String codigoAsiento) {
        if (asientosOcupados.contains(codigoAsiento)) {
            asientosOcupados.remove(codigoAsiento);
            System.out.println("Reserva de asiento " + codigoAsiento + " cancelada.");
            return true;
        }
        System.out.println("El asiento no estaba ocupado.");
        return false;
    }


    public Pasajero buscarPasajerosPorDni(String dni) {
        for (Pasajero p : pasajeros) {
            if (p.getDni().equalsIgnoreCase(dni)) {
                System.out.println("Pasajero encontrado: " + p.getNombre() + "" + p.getApellido());
                return p;
            }
        }
        System.out.println("No se encontro ningun pasajero con el DNI: " + dni);
        return null;
    }
}

