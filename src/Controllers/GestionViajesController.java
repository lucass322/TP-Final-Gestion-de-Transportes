package Controllers;

import Utilidades.Tablas;
import Viajes.Viaje;
import Viajes.GestorViajes.GestoraViajes;
import Utilidades.Notificaciones;
import Vehiculos.Vehiculo;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.SelectionMode;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;


public class GestionViajesController {

    @FXML
    private StackPane contenedorNotificaciones;

    @FXML
    private TableView<Viaje> tablaViajes;

    // Columnas
    @FXML
    private TableColumn<Viaje, Number> colId;

    @FXML
    private TableColumn<Viaje, String> colRecorrido;

    @FXML
    private TableColumn<Viaje, String> colVehiculo;

    @FXML
    private TableColumn<Viaje, String> colSalida;

    @FXML
    private TableColumn<Viaje, String> colLlegada;

    @FXML
    private TableColumn<Viaje, String> colEstado;

    // Botones
    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnHabilitar;

    @FXML
    private Button btnDeshabilitar;

    private GestoraViajes gestoraViajes;
    private StackPane panelContenido;

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

    private void configurarColumnas() {

        // ID
        colId.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getIdViaje()));

        // Recorrido
        colRecorrido.setCellValueFactory(data -> {
            Viaje viaje = data.getValue();
            String recorrido = viaje.getRecorrido().getOrigen() + " → " + viaje.getRecorrido().getDestino();

            return new SimpleStringProperty(recorrido);
        });

        // Vehículo
        colVehiculo.setCellValueFactory(data -> {

            Vehiculo vehiculo = data.getValue().getVehiculoAsignado();

            String descripcion = obtenerTipoVehiculo(vehiculo) + " - " + vehiculo.getPatente();

            return new SimpleStringProperty(descripcion);
        });

        // Fecha y hora de salida
        colSalida.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaHoraSalidaFormateada())
        );

        // Fecha y hora de llegada
        colLlegada.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaHoraLlegadaFormateada()));

        // Estado
        colEstado.setCellValueFactory(data ->  new SimpleStringProperty(data.getValue().isHabilitadoViaje() ? "Habilitado" : "Deshabilitado"));

        Tablas.centrarColumnas(colId, colRecorrido, colVehiculo, colSalida, colLlegada, colEstado);

        // Ajustar columnas al ancho de la tabla.
        tablaViajes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void actualizarEstadoBotones() {

        var seleccionados = tablaViajes.getSelectionModel().getSelectedItems();


        btnEditar.setDisable(seleccionados.size() != 1);

        btnHabilitar.setDisable(seleccionados.stream() .noneMatch(v -> !v.isHabilitadoViaje()));

        btnDeshabilitar.setDisable(seleccionados.stream().noneMatch(Viaje::isHabilitadoViaje));
    }

    private void cargarViajes() {

        tablaViajes.getItems().setAll(gestoraViajes.listarViajes());
    }

    private void configurarSeleccion() {

        tablaViajes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tablaViajes.getSelectionModel()
                .getSelectedItems()
                .addListener((ListChangeListener<Viaje>) cambio -> actualizarEstadoBotones());

        actualizarEstadoBotones();
    }

    @FXML
    private void habilitarViajes() {
        cambiarEstadoViajes(true);
    }

    @FXML
    private void deshabilitarViajes() {
        cambiarEstadoViajes(false);
    }

    private void cambiarEstadoViajes(boolean habilitar) {

        List<Viaje> seleccionados = new ArrayList<>(tablaViajes.getSelectionModel().getSelectedItems());

        if (seleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná al menos un viaje.");

            return;
        }

        int cantidadActualizada = 0;
        List<String> errores = new ArrayList<>();

        for (Viaje viaje : seleccionados) {

            if (viaje.isHabilitadoViaje() == habilitar) {
                continue;
            }

            try {

                if (habilitar) {
                    gestoraViajes.habilitarViaje(viaje.getIdViaje());
                } else {
                    gestoraViajes.bajaLogicaViaje(viaje.getIdViaje());
                }

                cantidadActualizada++;

            } catch (SQLException e) {

                errores.add("ID " + viaje.getIdViaje() + ": " + e.getMessage());
            }
        }

        cargarViajes();

        if (!errores.isEmpty()) {

            Notificaciones.mostrarError(contenedorNotificaciones, "Se actualizaron " + cantidadActualizada +
                            " viajes. Errores: " + String.join("; ", errores)
            );

        } else if (cantidadActualizada > 0) {

            Notificaciones.mostrarExito(contenedorNotificaciones, cantidadActualizada +
                            (habilitar ? " viaje(s) habilitado(s) correctamente." : " viaje(s) deshabilitado(s) correctamente.")
            );

        } else {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Los viajes seleccionados ya tienen ese estado.");
        }
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    @FXML
    private void abrirFormularioViaje() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioViaje.fxml"));

            Parent vista = loader.load();

            FormularioViajeController controller = loader.getController();

            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    public void mostrarExito(String mensaje) {
        Notificaciones.mostrarExito(contenedorNotificaciones,mensaje);
    }

    @FXML
    private void editarViaje() {

        Viaje viajeSeleccionado = tablaViajes.getSelectionModel().getSelectedItem();

        if (viajeSeleccionado == null) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un viaje para editar."
            );

            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioViaje.fxml"));

            Parent vista = loader.load();

            FormularioViajeController controller = loader.getController();

            controller.setPanelContenido(panelContenido);
            controller.prepararEdicion(viajeSeleccionado);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSeleccion();

        try {
            gestoraViajes = new GestoraViajes();

            cargarViajes();

        } catch (SQLException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "Error al cargar los viajes: " + e.getMessage());
        }
    }
}
