package Controllers;

import DAO.RecorridoDAO;
import DAO.VehiculoDAO;

import Viajes.Recorrido;
import Viajes.Viaje;
import Viajes.GestorViajes.GestoraViajes;

import Vehiculos.Vehiculo;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;
import Utilidades.Validaciones;

import Utilidades.Notificaciones;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FormularioViajeController {

    @FXML
    private StackPane contenedorNotificaciones;

    // Datos del viaje

    @FXML
    private ComboBox<Recorrido> cbRecorrido;

    @FXML
    private ComboBox<Vehiculo> cbVehiculo;

    @FXML
    private DatePicker dpFechaSalida;

    @FXML
    private TextField txtHoraSalida;

    @FXML
    private Label lblLlegadaCalculada;

    // Botones

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnCancelar;

    // Gestoras y DAO
    private GestoraViajes gestoraViajes;
    private RecorridoDAO recorridoDAO;
    private VehiculoDAO vehiculoDAO;

    // Edición
    private Viaje viajeEnEdicion;

    // Navegación
    private StackPane panelContenido;

    @FXML
    public void initialize() {

        dpFechaSalida.setValue(LocalDate.now());

        txtHoraSalida.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        configurarConvertidores();
        configurarCalculoLlegada();

        try {
            gestoraViajes = new GestoraViajes();

            recorridoDAO = new RecorridoDAO();
            vehiculoDAO = new VehiculoDAO();

            cargarRecorridos();
            cargarVehiculos();

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "Error al inicializar el formulario: " + e.getMessage());
        }
    }

    private void configurarConvertidores() {

        cbRecorrido.setConverter(new StringConverter<>() {

            @Override
            public String toString(Recorrido recorrido) {

                if (recorrido == null) {
                    return "";
                }

                return recorrido.getOrigen() + " → " + recorrido.getDestino();
            }

            @Override
            public Recorrido fromString(String texto) {
                return null;
            }
        });

        cbVehiculo.setConverter(new StringConverter<>() {

            @Override
            public String toString(Vehiculo vehiculo) {

                if (vehiculo == null) {
                    return "";
                }

                return obtenerTipoVehiculo(vehiculo) + " - " + vehiculo.getPatente();
            }

            @Override
            public Vehiculo fromString(String texto) {
                return null;
            }
        });
    }

    private void cargarRecorridos() throws SQLException {

        List<Recorrido> recorridos = recorridoDAO.listar();

        cbRecorrido.getItems().setAll(
                recorridos.stream()
                        .filter(Recorrido::isHabilitadoRecorrido)
                        .toList()
        );
    }

    private void cargarVehiculos() throws SQLException {

        List<Vehiculo> vehiculos = vehiculoDAO.listar();

        cbVehiculo.getItems().setAll(
                vehiculos.stream()
                        .filter(Vehiculo::isActivo)
                        .toList()
        );
    }

    private String obtenerTipoVehiculo(Vehiculo vehiculo) {

        if (vehiculo instanceof Avion) {
            return "Avión";
        }

        if (vehiculo instanceof Colectivo) {
            return "Colectivo";
        }

        if (vehiculo instanceof Tren) {
            return "Tren";
        }

        return "Desconocido";
    }

    private void actualizarLlegadaCalculada() {
        Recorrido recorrido = cbRecorrido.getValue();
        Vehiculo vehiculo = cbVehiculo.getValue();
        LocalDate fecha = dpFechaSalida.getValue();

        String hora = txtHoraSalida.getText();

        if (recorrido == null || vehiculo == null ||
                fecha == null || hora == null || hora.isBlank()) {

            lblLlegadaCalculada.setText("Seleccionar recorrido, vehículo y hora de salida.");

            return;
        }

        try {
            LocalTime horaSalida = LocalTime.parse(hora.trim(), DateTimeFormatter.ofPattern("HH:mm"));

            LocalDateTime fechaHoraSalida = LocalDateTime.of(fecha, horaSalida);

            // Crear un viaje temporal para calcular la llegada.
            Viaje viajeTemporal = new Viaje(recorrido, fechaHoraSalida, vehiculo);

            lblLlegadaCalculada.setText(viajeTemporal.getFechaHoraLlegadaFormateada() );

        } catch (DateTimeParseException e) {

            lblLlegadaCalculada.setText("Ingresá una hora válida (HH:mm).");

        } catch (IllegalArgumentException | IllegalStateException e) {

            lblLlegadaCalculada.setText("No se pudo calcular la llegada: " + e.getMessage());
        }
    }

    private void configurarCalculoLlegada() {

        cbRecorrido.valueProperty().addListener((observable, anterior, seleccionado) -> actualizarLlegadaCalculada());

        cbVehiculo.valueProperty().addListener((observable, anterior, seleccionado) -> actualizarLlegadaCalculada());

        dpFechaSalida.valueProperty().addListener((observable, anterior, seleccionada) -> actualizarLlegadaCalculada());

        txtHoraSalida.textProperty().addListener((observable, anterior, nuevaHora) -> actualizarLlegadaCalculada());

        actualizarLlegadaCalculada();
    }

    private boolean validarCampos() {
        // Validar recorrido.
        if (cbRecorrido.getValue() == null) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un recorrido.");

            return false;
        }

        // Validar vehículo.
        if (cbVehiculo.getValue() == null) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un vehículo.");

            return false;
        }

        // Validar fecha.
        LocalDate fecha = dpFechaSalida.getValue();

        if (fecha == null) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná una fecha de salida.");

            return false;
        }

        if (!Validaciones.fechaNoAnterior(fecha)) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "La fecha de salida no puede ser anterior a la fecha actual.");

            return false;
        }

        // Validar hora.
        String hora = txtHoraSalida.getText();

        if (Validaciones.campoVacio(hora)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ingresá una hora de salida.");

            return false;
        }

        if (!Validaciones.horaValida(hora)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ingresá una hora válida con formato HH:mm.");

            return false;
        }

        // Validar fecha y hora combinadas.
        if (!Validaciones.fechaHoraNoAnterior(fecha, hora)) {
            Notificaciones.mostrarAdvertencia( contenedorNotificaciones, "La fecha y hora de salida no pueden ser anteriores al momento actual.");

            return false;
        }

        return true;
    }

    private void limpiarCampos() {

        cbRecorrido.setValue(null);
        cbVehiculo.setValue(null);

        dpFechaSalida.setValue(LocalDate.now());

        txtHoraSalida.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        lblLlegadaCalculada.setText("Seleccioná recorrido, vehículo y hora de salida.");
    }

    @FXML
    private void registrarViaje() {

        if (!validarCampos()) {
            return;
        }

        if (gestoraViajes == null) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo inicializar la gestión de viajes.");

            return;
        }

        try {
            Recorrido recorrido = cbRecorrido.getValue();
            Vehiculo vehiculo = cbVehiculo.getValue();

            LocalDate fecha = dpFechaSalida.getValue();

            LocalTime hora = LocalTime.parse(txtHoraSalida.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));

            LocalDateTime fechaHoraSalida = LocalDateTime.of(fecha, hora);

            if (viajeEnEdicion == null) {

                // REGISTRAR NUEVO VIAJE
                Viaje nuevoViaje = new Viaje(recorrido,fechaHoraSalida, vehiculo);

                gestoraViajes.altaViaje(nuevoViaje);

                Notificaciones.mostrarExito(contenedorNotificaciones, "Viaje registrado correctamente.");

                limpiarCampos();

            } else {

                // MODIFICAR VIAJE EXISTENTE
                viajeEnEdicion.setRecorrido(recorrido);

                viajeEnEdicion.setVehiculoAsignado(vehiculo);

                viajeEnEdicion.setFechaHoraSalida(fechaHoraSalida);

                gestoraViajes.modificarViaje(viajeEnEdicion);

                volverGestionViajes("Viaje actualizado correctamente.");
            }

        } catch (SQLException | IllegalArgumentException | IllegalStateException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo guardar el viaje: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionViajes.fxml"));

            Parent vista = loader.load();

            GestionViajesController controller = loader.getController();

            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a Gestión de Viajes: " + e.getMessage());
        }
    }

    public void prepararEdicion(Viaje viaje) {
        if (viaje == null) {
            throw new IllegalArgumentException("El viaje no puede ser nulo.");
        }

        this.viajeEnEdicion = viaje;

        // Recuperar recorrido.
        cbRecorrido.setValue(viaje.getRecorrido());

        // Recuperar vehículo.
        cbVehiculo.setValue(viaje.getVehiculoAsignado());

        // Recuperar fecha de salida.
        dpFechaSalida.setValue(viaje.getFechaHoraSalida().toLocalDate());

        // Recuperar hora de salida.
        txtHoraSalida.setText(viaje.getFechaHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm")));

        // Actualizar llegada estimada.
        actualizarLlegadaCalculada();

        // Cambiar texto del botón.
        btnRegistrar.setText("Guardar cambios");
    }

    private void volverGestionViajes(String mensaje) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionViajes.fxml"));

            Parent vista = loader.load();

            GestionViajesController controller = loader.getController();

            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

            controller.mostrarExito(mensaje);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a Gestión de Viajes: " + e.getMessage());
        }
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }
}
