package Controllers;

import Vehiculos.Vehiculo;
import Vehiculos.GestorVehiculos.GestoraVehiculos;

import Utilidades.Tablas;
import Utilidades.Notificaciones;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;

import Excepciones.VehiculoNoDisponibleExcepcion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.SelectionMode;
import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;

public class GestionVehiculosController {

    @FXML
    private StackPane contenedorNotificaciones;

    @FXML
    private TableView<Vehiculo> tablaVehiculos;

    @FXML
    private TableColumn<Vehiculo, Number> colId;

    @FXML
    private TableColumn<Vehiculo, String> colPatente;

    @FXML
    private TableColumn<Vehiculo, String> colTipo;

    @FXML
    private TableColumn<Vehiculo, Number> colCapacidad;

    @FXML
    private TableColumn<Vehiculo, Number> colCosto;

    @FXML
    private TableColumn<Vehiculo, String> colEstado;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnActivar;

    @FXML
    private Button btnDesactivar;

    private GestoraVehiculos gestoraVehiculos;

    private StackPane panelContenido;

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    private String obtenerTipo(Vehiculo vehiculo) {

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

        colId.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getId())
        );

        colPatente.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPatente())
        );

        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(obtenerTipo(data.getValue()))
        );

        colCapacidad.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().getCapacidadPasajeros())
        );

        colCosto.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        data.getValue().getCostoPorKm()
                )
        );

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isActivo() ? "Activo" : "Inactivo")
        );

        Tablas.centrarColumnas(
                colId,
                colPatente,
                colTipo,
                colCapacidad,
                colCosto,
                colEstado
        );

        Tablas.centrarColumnas(colId, colPatente, colTipo, colCapacidad, colCosto, colEstado);

        tablaVehiculos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarVehiculos() {

        tablaVehiculos.getItems().setAll(gestoraVehiculos.listarVehiculos());
    }

    @FXML
    public void initialize() {

        configurarColumnas();
        configurarSeleccion();

        try {
            gestoraVehiculos = new GestoraVehiculos();
            cargarVehiculos();

        } catch (SQLException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "Error al cargar los vehículos: " + e.getMessage());
        }
    }

    private void actualizarEstadoBotones() {

        var seleccionados = tablaVehiculos.getSelectionModel()
                .getSelectedItems();

        // Editar requiere exactamente un vehículo.
        btnEditar.setDisable(seleccionados.size() != 1);

        // Activar requiere al menos un vehículo inactivo.
        btnActivar.setDisable(seleccionados.stream().noneMatch(v -> !v.isActivo()));

        // Desactivar requiere al menos un vehículo activo.
        btnDesactivar.setDisable(seleccionados.stream().noneMatch(Vehiculo::isActivo));
    }

    private void configurarSeleccion() {

        tablaVehiculos.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);

        tablaVehiculos.getSelectionModel()
                .getSelectedItems()
                .addListener((ListChangeListener<Vehiculo>) cambio ->
                        actualizarEstadoBotones()
                );

        actualizarEstadoBotones();
    }

    @FXML
    private void activarVehiculos() {
        cambiarEstadoVehiculos(true);
    }

    @FXML
    private void desactivarVehiculos() {
        cambiarEstadoVehiculos(false);
    }

    private void cambiarEstadoVehiculos(boolean activar) {

        List<Vehiculo> seleccionados = new ArrayList<>(tablaVehiculos.getSelectionModel().getSelectedItems());

        if (seleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná al menos un vehículo.");

            return;
        }

        int cantidadActualizada = 0;
        List<String> errores = new ArrayList<>();

        for (Vehiculo vehiculo : seleccionados) {

            if (vehiculo.isActivo() == activar) {
                continue;
            }

            try {

                if (activar) {
                    gestoraVehiculos.habilitarVehiculo(vehiculo.getId());
                } else {
                    gestoraVehiculos.bajaLogicaVehiculo(vehiculo.getId());
                }

                cantidadActualizada++;

            } catch (VehiculoNoDisponibleExcepcion | SQLException e) {

                errores.add( "ID " + vehiculo.getId() + ": " + e.getMessage());
            }
        }

        cargarVehiculos();

        if (!errores.isEmpty()) {

            Notificaciones.mostrarError(contenedorNotificaciones, "Se actualizaron " + cantidadActualizada +
                            " vehículos. Errores: " + String.join("; ", errores)
            );

        } else if (cantidadActualizada > 0) {

            Notificaciones.mostrarExito(contenedorNotificaciones, cantidadActualizada +
                            (activar ? " vehículo(s) activado(s) correctamente." : " vehículo(s) desactivado(s) correctamente.")
            );

        } else {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Los vehículos seleccionados ya tienen ese estado.");
        }
    }

    @FXML
    private void abrirFormularioVehiculo() {
        try {
            FXMLLoader loader = new FXMLLoader( getClass().getResource("/views/FormularioVehiculo.fxml"));

            Parent vista = loader.load();

            FormularioVehiculoController controller = loader.getController();

            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    public void mostrarExito(String mensaje) {

        Notificaciones.mostrarExito(contenedorNotificaciones, mensaje);
    }

    @FXML
    private void editarVehiculo() {
        Vehiculo vehiculoSeleccionado = tablaVehiculos.getSelectionModel().getSelectedItem();

        if (vehiculoSeleccionado == null) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un vehículo para editar.");

            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioVehiculo.fxml"));

            Parent vista = loader.load();

            FormularioVehiculoController controller = loader.getController();

            controller.setPanelContenido(panelContenido);
            controller.prepararEdicion(vehiculoSeleccionado);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }
}
