package Controllers;

import DAO.ReservaDAO;
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
    private Usuarios.Usuario usuarioActual;
    private final ReservaDAO reservaDAO = new ReservaDAO();

    private void cargarReservas() {
        if (usuarioActual == null || usuarioActual.getRol() == null) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo identificar al usuario actual.");
            return;
        }

        String rol = usuarioActual.getRol().getNombreRol();

        try {
            if ("CLIENTE".equalsIgnoreCase(rol)) {

                tablaReservas.getItems().setAll(gestoraReservas.listarReservasPorUsuario(usuarioActual.getIdUsuario()));

            } else {
                tablaReservas.getItems().setAll(gestoraReservas.listarReservas());
            }

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"Error al cargar las reservas: " + e.getMessage());
        }
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    public void setUsuario(Usuarios.Usuario usuario) {
        this.usuarioActual = usuario;

        if (gestoraReservas != null) {
            cargarReservas();
        }
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
            controller.setUsuario(usuarioActual);

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

        if (usuarioActual != null&& usuarioActual.getRol() != null
                && "CLIENTE".equalsIgnoreCase(usuarioActual.getRol().getNombreRol())) {

            try {
                if (!reservaDAO.perteneceAUsuario(reservaSeleccionada.getIdReserva(),usuarioActual.getIdUsuario())) {

                    Notificaciones.mostrarError(contenedorNotificaciones,"No tenés permiso para editar esta reserva.");
                    return;
                }

            } catch (SQLException e) {
                Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo verificar la propiedad de la reserva.");
                e.printStackTrace();
                return;
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioReserva.fxml"));

            Parent vista = loader.load();

            FormularioReservaController controller = loader.getController();

            controller.setPanelContenido(panelContenido);
            controller.setUsuario(usuarioActual);
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
                if (usuarioActual != null && usuarioActual.getRol() != null
                        && "CLIENTE".equalsIgnoreCase(usuarioActual.getRol().getNombreRol())) {

                    gestoraReservas.cancelarReserva(reserva.getIdReserva(), usuarioActual.getIdUsuario());

                } else {
                    gestoraReservas.cancelarReserva(reserva.getIdReserva());
                }

                cantidadCancelada++;

            }catch (IllegalArgumentException  e) {
                errores.add("ID " + reserva.getIdReserva() + ": " + e.getMessage());
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

        gestoraReservas = new GestoraReservas();
    }
}
