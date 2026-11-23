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

        // Verifica que el pasajero y el código de asiento sean válidos.
        if (pasajero == null || pasajero.getPersona() == null ||
                codigoAsiento == null || codigoAsiento.isBlank()) {

            return false;
        }

        codigoAsiento = codigoAsiento.trim().toUpperCase(Locale.ROOT);

        // Agrega al pasajero si todavía no está registrado.
        if (!pasajeros.contains(pasajero)) {
            try {
                agregarPasajero(pasajero);
            } catch (PasajeroExistenteExcepcion e) {
                return false;
            }
        }

        // Verifica que el vehículo tenga capacidad disponible.
        if (vehiculoAsignado == null ||
                asientosOcupados.size() >= vehiculoAsignado.getCapacidadPasajeros()) {
            return false;
        }

        // Comprueba que el asiento esté disponible.
        if (!asientosOcupados.contains(codigoAsiento)) {
            asientosOcupados.add(codigoAsiento);

            System.out.println("Asiento " + codigoAsiento + " reservado para " + pasajero.getPersona().getNombre());

            return true;
        }

        // Informa que el asiento ya está ocupado.
        System.out.println("El asiento ya está ocupado.");

        return false;
    }

    public boolean cancelarReserva(String codigoAsiento) {

        // Verifica que el código de asiento sea válido.
        if (codigoAsiento == null || codigoAsiento.isBlank()) {
            return false;
        }

        codigoAsiento = codigoAsiento.trim().toUpperCase(Locale.ROOT);

        // Comprueba si el asiento está ocupado.
        if (asientosOcupados.contains(codigoAsiento)) {
            asientosOcupados.remove(codigoAsiento);

            System.out.println("Reserva de asiento " + codigoAsiento + " cancelada.");

            return true;
        }

        // Informa que el asiento no estaba ocupado.
        System.out.println("El asiento no estaba ocupado.");

        return false;
    }


    public Pasajero buscarPasajerosPorDni(String dni) {

        // Verifica que el DNI recibido sea válido.
        if (!Validaciones.dniValido(dni)) {
            return null;
        }

        // Recorre la lista de pasajeros del viaje.
        for (Pasajero p : pasajeros) {

            // Comprueba que el pasajero y su persona tengan datos válidos.
            if (p != null && p.getPersona() != null &&
                    p.getPersona().getDni() != null) {

                // Compara el DNI del pasajero con el DNI recibido.
                if (p.getPersona().getDni().equals(dni)) {

                    System.out.println("Pasajero encontrado: " +
                            p.getPersona().getNombre() + " " +
                            p.getPersona().getApellido());

                    return p;
                }
            }
        }

        // Informa que no se encontró ningún pasajero con ese DNI.
        System.out.println("No se encontró ningún pasajero con el DNI: " + dni);

        return null;
    }

    public void agregarTramo(String nombreTramo, Viaje viajeTramo) {
        // Verifica que el nombre y el viaje sean válidos.
        if (nombreTramo == null || nombreTramo.isBlank() || viajeTramo == null) {
            return;
        }

        // Evita que un viaje se agregue a sí mismo como tramo.
        if (viajeTramo == this) {
            return;
        }

        // Elimina los espacios al principio y al final del nombre.
        nombreTramo = nombreTramo.trim();

        // Evita registrar dos tramos con el mismo nombre, sin importar mayúsculas/minúsculas.
        for (String nombre : historialViajes.keySet()) {
            if (nombre != null && nombre.equalsIgnoreCase(nombreTramo)) {
                System.out.println("Ya existe un tramo con ese nombre.");
                return;
            }
        }

        // Agrega el tramo al itinerario.
        historialViajes.put(nombreTramo, viajeTramo);

        System.out.println("Tramo " + nombreTramo + " agregado al itinerario.");
    }


    public Viaje obtenerTramo(String nombreTramo) {
        // Verifica que el nombre del tramo sea válido.
        if (nombreTramo == null || nombreTramo.isBlank()) {
            return null;
        }

        nombreTramo = nombreTramo.trim();

        // Recorre los nombres de los tramos registrados.
        for (String nombre : historialViajes.keySet()) {

            // Compara los nombres ignorando mayúsculas y minúsculas.
            if (nombre != null && nombre.equalsIgnoreCase(nombreTramo)) {
                return historialViajes.get(nombre);
            }
        }

        // Informa que no se encontró el tramo solicitado.
        System.out.println("No se encontró el tramo: " + nombreTramo);

        return null;
    }

    //NOTA: las velocidades son valores estimados que definimos para calcular la duración, no velocidades reales garantizadas.
    public double calcularDuracionHoras() {
        //Verifica que el viaje tenga los datos necesarios para calcular su duración.
        if (recorrido == null || vehiculoAsignado == null) {
            throw new IllegalStateException("El viaje debe tener un recorrido y un vehículo asignado.");
        }

        // Verifica que la distancia del recorrido sea válida.
        if (recorrido.getDistanciaKM() <= 0) {
            throw new IllegalStateException("La distancia del recorrido debe ser mayor a cero.");
        }

        // Almacena la velocidad promedio correspondiente al vehículo.
        double velocidadPromedio;

        // Determina la velocidad promedio según el tipo de vehículo asignado.

        if (vehiculoAsignado instanceof Avion) {
            velocidadPromedio = 800;
        } else if (vehiculoAsignado instanceof Colectivo) {
            velocidadPromedio = 80;
        } else if (vehiculoAsignado instanceof Tren) {
            velocidadPromedio = 100;
        } else {
            // Informa que se recibió un tipo de vehículo no contemplado.
            throw new IllegalArgumentException("Tipo de vehículo desconocido.");
        }

        //Calcula y devuelve la duración dividiendo la distancia por la velocidad.
        return recorrido.getDistanciaKM() / velocidadPromedio;
    }

    public void calcularFechaHoraLlegada() {
        // Verifica que exista una fecha y hora de salida.
        if (fechaHoraSalida == null) {
            throw new IllegalStateException("El viaje debe tener una fecha y hora de salida.");
        }

        // Obtiene la duración estimada del viaje en horas.
        double duracionHoras = calcularDuracionHoras();

        // Convierte la duración a minutos, redondea al entero más cercano y garantiza una duración mínima de un minuto.
        long duracionMinutos = Math.max(1, Math.round(duracionHoras * 60));

        // Calcula la fecha y hora de llegada sumando los minutos a la salida.
        this.fechaHoraLlegada = fechaHoraSalida.plusMinutes(duracionMinutos);
    }
}

