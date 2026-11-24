package Controllers;

import Pasajeros.Pasajero;
import Utilidades.Notificaciones;
import Viajes.Viaje;
import DAO.PasajeroDAO;
import DAO.ViajeDAO;
import Viajes.GestorReservas.GestoraReservas;
import Viajes.Reserva;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class FormularioReservaController {

    private final PasajeroDAO pasajeroDAO = new PasajeroDAO();
    private final ViajeDAO viajeDAO = new ViajeDAO();
    private StackPane panelContenido;
    private GestoraReservas gestoraReservas;

    @FXML
    private StackPane contenedorNotificaciones;

    @FXML
    private ComboBox<Pasajero> cbPasajero;

    @FXML
    private ComboBox<Viaje> cbViaje;

    @FXML
    private TextField txtAsientos;

    @FXML
    private Label lblFechaReserva;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnCancelar;

    private Reserva reservaEnEdicion;

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    public void prepararEdicion(Reserva reserva) {

        this.reservaEnEdicion = reserva;

        // Cargar los datos actuales de la reserva.
        cbPasajero.setValue(reserva.getPasajero());
        cbViaje.setValue(reserva.getViaje());

        txtAsientos.setText(String.join(", ", reserva.getAsientosReservados()));

        lblFechaReserva.setText(reserva.getFechaReservaFormateada());

        // En edición solo se pueden modificar los asientos.
        cbPasajero.setDisable(true);
        cbViaje.setDisable(true);

        btnRegistrar.setText("Guardar cambios");
    }

    private void cargarPasajeros() {
        try {
            List<Pasajero> pasajeros = pasajeroDAO.listar();

            pasajeros.removeIf(p -> !p.isHabilitadoUsuario() || p.getPersona() == null
                    || !p.getPersona().isHabilitadoPersona());

            cbPasajero.getItems().setAll(pasajeros);

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudieron cargar los pasajeros.");
            e.printStackTrace();
        }
    }

    private void cargarViajes() {
        try {
            List<Viaje> viajes = viajeDAO.listar();

            viajes.removeIf(v -> !v.isHabilitadoViaje() || v.getFechaHoraSalida() == null
                    || !v.getFechaHoraSalida().isAfter(LocalDateTime.now()));

            cbViaje.getItems().setAll(viajes);

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudieron cargar los viajes disponibles.");
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        cargarPasajeros();
        cargarViajes();

        try {
            gestoraReservas = new GestoraReservas();
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudieron inicializar las reservas.");
            e.printStackTrace();
        }
    }


    @FXML
    private void registrarReserva() {

        if (gestoraReservas == null) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo inicializar el sistema de reservas.");
            return;
        }

        String asientosTexto = txtAsientos.getText();

        if (asientosTexto == null || asientosTexto.isBlank()) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ingresá al menos un asiento.");
            return;
        }

        try {
            List<String> asientos = Arrays.asList(asientosTexto.split(","));

            // MODO EDICIÓN
            if (reservaEnEdicion != null) {
                Reserva reservaModificada = new Reserva(
                        reservaEnEdicion.getIdReserva(),
                        reservaEnEdicion.getPasajero(),
                        reservaEnEdicion.getViaje(),
                        asientos,
                        reservaEnEdicion.getFechaReserva(),
                        reservaEnEdicion.getEstado()
                );

                gestoraReservas.modificarReserva(reservaModificada);

                volverGestionReservas();

                return;
            }

            // MODO REGISTRO
            Pasajero pasajero = cbPasajero.getValue();
            Viaje viaje = cbViaje.getValue();

            if (pasajero == null || viaje == null) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un pasajero y un viaje.");
                return;
            }

            Reserva reserva = new Reserva();
            reserva.setPasajero(pasajero);
            reserva.setViaje(viaje);

            if (!reserva.reservarAsientos(asientos)) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, reserva.getMensajeError() != null
                                ? reserva.getMensajeError()
                                : "No se pudo realizar la reserva.");
                return;
            }

            gestoraReservas.altaReserva(reserva);

            Notificaciones.mostrarExito(contenedorNotificaciones, "Reserva registrada correctamente.");

            cbPasajero.getSelectionModel().clearSelection();
            cbViaje.getSelectionModel().clearSelection();
            txtAsientos.clear();

        } catch (SQLException | RuntimeException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, e.getMessage() != null
                    ? e.getMessage() : "No se pudo guardar la reserva.");

            e.printStackTrace();
        }
    }


    @FXML
    private void cancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionReservas.fxml"));

            Parent vista = loader.load();

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a la gestión de reservas.");
            e.printStackTrace();
        }
    }

    private void volverGestionReservas() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionReservas.fxml"));

            Parent vista = loader.load();

            GestionReservasController controller = loader.getController();
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

            controller.mostrarNotificacionEdicion();

        } catch (IOException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a la gestión de reservas.");
            e.printStackTrace();
        }
    }
}
