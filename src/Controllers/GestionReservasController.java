package Controllers;

import Viajes.Reserva;
import Viajes.GestorReservas.GestoraReservas;

import Utilidades.Notificaciones;
import Personas.Persona;
import Pasajeros.Pasajero;
import Viajes.Viaje;
import Viajes.Recorrido;
import Utilidades.Tablas;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

public class GestionReservasController {

    @FXML
    private StackPane contenedorNotificaciones;

    @FXML
    private TableView<Reserva> tablaReservas;

    // Columnas
    @FXML
    private TableColumn<Reserva, Number> colId;

    @FXML
    private TableColumn<Reserva, String> colPasajero;

    @FXML
    private TableColumn<Reserva, String> colDni;

    @FXML
    private TableColumn<Reserva, String> colViaje;

    @FXML
    private TableColumn<Reserva, String> colAsientos;

    @FXML
    private TableColumn<Reserva, String> colFecha;

    @FXML
    private TableColumn<Reserva, String> colEstado;

    // Botones
    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnCancelar;

    private GestoraReservas gestoraReservas;
    private StackPane panelContenido;

    private void cargarReservas() {
        tablaReservas.getItems().setAll(gestoraReservas.listarReservas());
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    private void configurarColumnas() {

        // ID
        colId.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getIdReserva())
        );

        // Pasajero
        colPasajero.setCellValueFactory(data -> {
            Pasajero pasajero = data.getValue().getPasajero();

            String nombreCompleto = "";

            if (pasajero != null && pasajero.getPersona() != null) {

                Persona persona = pasajero.getPersona();

                nombreCompleto = persona.getNombre()
                        + " "
                        + persona.getApellido();
            }

            return new SimpleStringProperty(nombreCompleto);
        });

        // DNI
        colDni.setCellValueFactory(data -> {
            Pasajero pasajero = data.getValue().getPasajero();

            String dni = "";

            if (pasajero != null && pasajero.getPersona() != null) {
                dni = pasajero.getPersona().getDni();
            }

            return new SimpleStringProperty(dni);
        });

        // Viaje
        colViaje.setCellValueFactory(data -> {
            Viaje viaje = data.getValue().getViaje();

            String descripcion = "";

            if (viaje != null && viaje.getRecorrido() != null) {
                Recorrido recorrido = viaje.getRecorrido();

                descripcion = recorrido.getOrigen() + " → " + recorrido.getDestino() + " - " +
                        viaje.getFechaHoraSalidaFormateada();
            }

            return new SimpleStringProperty(descripcion);
        });

        // Asientos
        colAsientos.setCellValueFactory(data -> {

            String asientos = String.join(", ", data.getValue().getAsientosReservados());

            return new SimpleStringProperty(asientos);
        });

        // Fecha de reserva
        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaReservaFormateada())
        );

        // Estado
        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(Reserva.ACTIVA.equals(data.getValue().getEstado()) ? "Activa" : "Cancelada")
        );

        Tablas.centrarColumnas(colId, colPasajero, colDni, colViaje, colAsientos, colFecha, colEstado);

        // Ajustar columnas al ancho de la tabla
        tablaReservas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void actualizarEstadoBotones() {

        var seleccionados = tablaReservas.getSelectionModel().getSelectedItems();

        boolean unaReservaActiva = seleccionados.size() == 1
                && Reserva.ACTIVA.equals(seleccionados.get(0).getEstado());

        boolean hayReservasActivas = seleccionados.stream()
                .anyMatch(r -> Reserva.ACTIVA.equals(r.getEstado()));

        btnEditar.setDisable(!unaReservaActiva);

        btnCancelar.setDisable(!hayReservasActivas);
    }

    private void configurarSeleccion() {
        tablaReservas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tablaReservas.getSelectionModel()
                .getSelectedItems()
                .addListener((ListChangeListener<Reserva>) cambio -> actualizarEstadoBotones());

        actualizarEstadoBotones();
    }

    @FXML
    private void abrirFormularioReserva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioReserva.fxml"));

            Parent vista = loader.load();

            FormularioReservaController controller = loader.getController();
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarReserva() {
        Reserva reservaSeleccionada = tablaReservas.getSelectionModel().getSelectedItem();

        if (reservaSeleccionada == null) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná una reserva para editar.");
            return;
        }

        if (!Reserva.ACTIVA.equals(reservaSeleccionada.getEstado())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "No se pueden editar reservas canceladas.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioReserva.fxml"));

            Parent vista = loader.load();

            FormularioReservaController controller = loader.getController();

            controller.setPanelContenido(panelContenido);
            controller.prepararEdicion(reservaSeleccionada);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario de edición.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelarReservas() {
        List<Reserva> seleccionadas = new ArrayList<>(tablaReservas.getSelectionModel().getSelectedItems());

        if (seleccionadas.isEmpty()) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná al menos una reserva.");

            return;
        }

        int cantidadCancelada = 0;
        List<String> errores = new ArrayList<>();

        for (Reserva reserva : seleccionadas) {
            if (!Reserva.ACTIVA.equals(reserva.getEstado())) {
                continue;
            }

            try {
                gestoraReservas.cancelarReserva(reserva.getIdReserva());

                cantidadCancelada++;

            } catch (SQLException e) {
                errores.add("ID " + reserva.getIdReserva() + ": " + e.getMessage());
            }
        }

        cargarReservas();

        if (!errores.isEmpty()) {
            Notificaciones.mostrarError(contenedorNotificaciones, "Se cancelaron " + cantidadCancelada +
                            " reservas. Errores: " + String.join("; ", errores)
            );

        } else if (cantidadCancelada > 0) {
            Notificaciones.mostrarExito(contenedorNotificaciones, cantidadCancelada +
                            " reserva(s) cancelada(s) correctamente.");

        } else {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Las reservas seleccionadas ya están canceladas.");
        }
    }

    public void mostrarNotificacionEdicion() {
        Notificaciones.mostrarExito( contenedorNotificaciones, "Los asientos de la reserva se modificaron correctamente.");
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSeleccion();

        try {
            gestoraReservas = new GestoraReservas();
            cargarReservas();

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "Error al cargar las reservas: " + e.getMessage());
        }
    }
}
