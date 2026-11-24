package Viajes;

import Excepciones.PasajeroExistenteExcepcion;
import Interfaz.Reservable;
import Pasajeros.Pasajero;
import Utilidades.Validaciones;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;
import Vehiculos.Vehiculo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Viaje implements Reservable {
    private int idViaje;
    private Recorrido recorrido;
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraLlegada;
    private Vehiculo vehiculoAsignado;
    private List<Pasajero> pasajeros;
    private Set<String> asientosOcupados;
    private Map<String, Viaje> historialViajes;
    private boolean habilitadoViaje = true;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Viaje(int idViaje, Recorrido recorrido, LocalDateTime fechaHoraSalida, Vehiculo vehiculoAsignado) {
        this.idViaje = idViaje;
        this.recorrido = recorrido;
        this.fechaHoraSalida = fechaHoraSalida;
        this.vehiculoAsignado = vehiculoAsignado;
        this.pasajeros = new ArrayList<>();
        this.asientosOcupados = new HashSet<>();
        this.historialViajes = new HashMap<>();

        calcularFechaHoraLlegada();
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

    public Recorrido getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(Recorrido recorrido) {
        this.recorrido = recorrido;

        if (this.recorrido != null &&
                this.fechaHoraSalida != null &&
                this.vehiculoAsignado != null) {
            calcularFechaHoraLlegada();
        }else{
            this.fechaHoraLlegada = null;
        }
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;

        if (this.fechaHoraSalida != null &&
                this.recorrido != null &&
                this.vehiculoAsignado != null) {
            calcularFechaHoraLlegada();
        }else{
            this.fechaHoraLlegada = null;
        }
    }

    public LocalDateTime getFechaHoraLlegada() {
        return fechaHoraLlegada;
    }

    public String getFechaHoraSalidaFormateada() {
        return fechaHoraSalida != null ? fechaHoraSalida.format(FORMATO_FECHA) : "";
    }

    public void setFechaHoraLlegada(LocalDateTime fechaHoraLlegada) {
        this.fechaHoraLlegada = fechaHoraLlegada;
    }

    public String getFechaHoraLlegadaFormateada() {
        return fechaHoraLlegada != null ? fechaHoraLlegada.format(FORMATO_FECHA) : "";
    }

    public Vehiculo getVehiculoAsignado() {
        return vehiculoAsignado;
    }

    public void setVehiculoAsignado(Vehiculo vehiculoAsignado) {
        this.vehiculoAsignado = vehiculoAsignado;

        if (this.vehiculoAsignado != null &&
                this.fechaHoraSalida != null &&
                this.recorrido != null) {
            calcularFechaHoraLlegada();
        }else{
            this.fechaHoraLlegada = null;
        }
    }

    public List<Pasajero> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<Pasajero> pasajeros) {

        this.pasajeros = pasajeros != null ? pasajeros : new ArrayList<>();
    }

    public Set<String> getAsientosOcupados() {

        return new HashSet<>(asientosOcupados);
    }

    public void setAsientosOcupados(Set<String> asientosOcupados) {
        this.asientosOcupados = asientosOcupados != null ? new HashSet<>(asientosOcupados) : new HashSet<>();
    }

    public Map<String, Viaje> getHistorialViajes() {
        return historialViajes;
    }

    public void setHistorialViajes(Map<String, Viaje> historialViajes) {

        this.historialViajes = historialViajes != null ? historialViajes : new HashMap<>();
    }

    public boolean isHabilitadoViaje() {
        return habilitadoViaje;
    }

    public void setHabilitadoViaje(boolean habilitadoViaje) {
        this.habilitadoViaje = habilitadoViaje;
    }

    public void agregarPasajero(Pasajero pasajero) throws PasajeroExistenteExcepcion {

        if (pasajero != null && pasajero.getPersona() != null && !pasajeros.contains(pasajero)) {
            pasajeros.add(pasajero);

            System.out.println("Pasajero " + pasajero.getPersona().getNombre() + " agregado al viaje.");

        } else {
            throw new PasajeroExistenteExcepcion("El pasajero ya está en el viaje o es inválido.");
        }
    }

    @Override
    public boolean reservarAsiento(Pasajero pasajero, String codigoAsiento) {
        if (pasajero == null || pasajero.getPersona() == null ||
                codigoAsiento == null || codigoAsiento.isBlank()) {

            return false;
        }

        codigoAsiento = codigoAsiento.trim().toUpperCase(Locale.ROOT);
        if (!pasajeros.contains(pasajero)) {
            try {
                agregarPasajero(pasajero);
            } catch (PasajeroExistenteExcepcion e) {
                return false;
            }
        }
        if (vehiculoAsignado == null ||
                asientosOcupados.size() >= vehiculoAsignado.getCapacidadPasajeros()) {
            return false;
        }
        if (!asientosOcupados.contains(codigoAsiento)) {
            asientosOcupados.add(codigoAsiento);

            System.out.println("Asiento " + codigoAsiento + " reservado para " + pasajero.getPersona().getNombre());

            return true;
        }
        System.out.println("El asiento ya está ocupado.");

        return false;
    }

    public boolean cancelarReserva(String codigoAsiento) {
        if (codigoAsiento == null || codigoAsiento.isBlank()) {
            return false;
        }

        codigoAsiento = codigoAsiento.trim().toUpperCase(Locale.ROOT);
        if (asientosOcupados.contains(codigoAsiento)) {
            asientosOcupados.remove(codigoAsiento);

            System.out.println("Reserva de asiento " + codigoAsiento + " cancelada.");

            return true;
        }
        System.out.println("El asiento no estaba ocupado.");
        return false;
    }


    public Pasajero buscarPasajerosPorDni(String dni) {
        if (!Validaciones.dniValido(dni)) {
            return null;
        }

        for (Pasajero p : pasajeros) {
            if (p != null && p.getPersona() != null && p.getPersona().getDni() != null) {
                if (p.getPersona().getDni().equals(dni)) {

                    System.out.println("Pasajero encontrado: " +
                            p.getPersona().getNombre() + " " +
                            p.getPersona().getApellido());

                    return p;
                }
            }
        }
        System.out.println("No se encontró ningún pasajero con el DNI: " + dni);

        return null;
    }

    public void agregarTramo(String nombreTramo, Viaje viajeTramo) {
        if (nombreTramo == null || nombreTramo.isBlank() || viajeTramo == null) {
            return;
        }
        if (viajeTramo == this) {
            return;
        }
        nombreTramo = nombreTramo.trim();
        for (String nombre : historialViajes.keySet()) {
            if (nombre != null && nombre.equalsIgnoreCase(nombreTramo)) {
                System.out.println("Ya existe un tramo con ese nombre.");
                return;
            }
        }
        historialViajes.put(nombreTramo, viajeTramo);

        System.out.println("Tramo " + nombreTramo + " agregado al itinerario.");
    }


    public Viaje obtenerTramo(String nombreTramo) {
        if (nombreTramo == null || nombreTramo.isBlank()) {
            return null;
        }

        nombreTramo = nombreTramo.trim();
        for (String nombre : historialViajes.keySet()) {
            if (nombre != null && nombre.equalsIgnoreCase(nombreTramo)) {
                return historialViajes.get(nombre);
            }
        }
        System.out.println("No se encontró el tramo: " + nombreTramo);

        return null;
    }
    public double calcularDuracionHoras() {
        if (recorrido == null || vehiculoAsignado == null) {
            throw new IllegalStateException("El viaje debe tener un recorrido y un vehículo asignado.");
        }
        if (recorrido.getDistanciaKM() <= 0) {
            throw new IllegalStateException("La distancia del recorrido debe ser mayor a cero.");
        }

        double velocidadPromedio;

        if (vehiculoAsignado instanceof Avion) {
            velocidadPromedio = 800;
        } else if (vehiculoAsignado instanceof Colectivo) {
            velocidadPromedio = 80;
        } else if (vehiculoAsignado instanceof Tren) {
            velocidadPromedio = 100;
        } else {
            throw new IllegalArgumentException("Tipo de vehículo desconocido.");
        }

        return recorrido.getDistanciaKM() / velocidadPromedio;
    }

    public void calcularFechaHoraLlegada() {
        if (fechaHoraSalida == null) {
            throw new IllegalStateException("El viaje debe tener una fecha y hora de salida.");
        }
        double duracionHoras = calcularDuracionHoras();
        long duracionMinutos = Math.max(1, Math.round(duracionHoras * 60));
        this.fechaHoraLlegada = fechaHoraSalida.plusMinutes(duracionMinutos);
    }
}

