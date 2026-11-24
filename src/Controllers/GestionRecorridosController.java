package Controllers;

import DAO.RecorridoDAO;
import Utilidades.Tablas;
import Viajes.Recorrido;
import Utilidades.Notificaciones;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.SelectionMode;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionRecorridosController {

    @FXML
    private StackPane contenedorNotificaciones;

    @FXML
    private TableView<Recorrido> tablaRecorridos;

    @FXML
    private TableColumn<Recorrido, Number> colId;

    @FXML
    private TableColumn<Recorrido, String> colOrigen;

    @FXML
    private TableColumn<Recorrido, String> colDestino;

    @FXML
    private TableColumn<Recorrido, Number> colDistancia;

    @FXML
    private TableColumn<Recorrido, String> colEstado;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnHabilitar;

    @FXML
    private Button btnDeshabilitar;

    private final RecorridoDAO recorridoDAO = new RecorridoDAO();
    private StackPane panelContenido;

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<Number>(
                        data.getValue().getIdRecorrido()
                )
        );

        colOrigen.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrigen())
        );

        colDestino.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDestino())
        );

        colDistancia.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<Number>(data.getValue().getDistanciaKM())
        );

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isHabilitadoRecorrido() ? "Habilitado" : "Deshabilitado")
        );

        Tablas.centrarColumnas(colId, colOrigen, colDestino, colDistancia, colEstado);

        colId.setResizable(true);
        colOrigen.setResizable(true);
        colDestino.setResizable(true);
        colDistancia.setResizable(true);
        colEstado.setResizable(true);

        tablaRecorridos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void cargarRecorridos() {

        try {
            tablaRecorridos.getItems().setAll(recorridoDAO.listar());

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "Error al cargar los recorridos: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSeleccion();
        cargarRecorridos();
    }

    private void actualizarEstadoBotones() {

        // Obtiene la lista de recorridos seleccionados actualmente.
        // Se usa var para que Java infiera automáticamente el tipo de dato.
        var seleccionados = tablaRecorridos.getSelectionModel().getSelectedItems();

        // El botón Editar solamente se habilita cuando hay exactamente un recorrido seleccionado.
        btnEditar.setDisable(seleccionados.size() != 1);

        // Habilitar requiere al menos un recorrido deshabilitado.
        btnHabilitar.setDisable(
                seleccionados.stream() // Permite recorrer los elementos seleccionados.
                        // noneMatch() comprueba que ninguno cumpla una determinada condición.
                        .noneMatch(r -> !r.isHabilitadoRecorrido())
                // r -> !r.isHabilitadoRecorrido() comprueba si el recorrido está deshabilitado.
        );

        // Deshabilitar requiere al menos un recorrido habilitado.
        btnDeshabilitar.setDisable(
                seleccionados.stream()
                        .noneMatch(Recorrido::isHabilitadoRecorrido)
        );

        // Recorrido::isHabilitadoRecorrido es una referencia al metodo que devuelve el estado de habilitación de cada recorrido.
        // El botón Deshabilitar queda desactivado cuando ninguno de los recorridos seleccionados está habilitado.
    }

    // Configura cómo se seleccionan los recorridos en la tabla.
    private void configurarSeleccion() {

        // Permite seleccionar varios recorridos al mismo tiempo.
        tablaRecorridos.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);

        // Cada vez que cambia la selección de la tabla, JavaFX ejecuta automáticamente:
        tablaRecorridos.getSelectionModel()
                .getSelectedItems()
                .addListener((ListChangeListener<Recorrido>) cambio ->
                        actualizarEstadoBotones()
                );

        // Se ejecuta una primera vez para establecer correctamente el estado inicial de los botones, cuando todavía
        // no hay ningún recorrido seleccionado.
        actualizarEstadoBotones();
    }

    @FXML
    private void abrirFormularioRecorrido() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioRecorrido.fxml"));

            Parent vista = loader.load();

            FormularioRecorridoController controller = loader.getController();
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    @FXML
    private void editarRecorrido() {

        Recorrido recorridoSeleccionado = tablaRecorridos.getSelectionModel().getSelectedItem();

        if (recorridoSeleccionado == null) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un recorrido para editar.");

            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioRecorrido.fxml"));

            Parent vista = loader.load();

            FormularioRecorridoController controller = loader.getController();

            controller.setPanelContenido(panelContenido);
            controller.prepararEdicion(recorridoSeleccionado);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    private void habilitarRecorridos() {
        cambiarEstadoRecorridos(true);
    }

    @FXML
    private void deshabilitarRecorridos() {
        cambiarEstadoRecorridos(false);
    }

    private void cambiarEstadoRecorridos(boolean habilitar) {

        List<Recorrido> seleccionados = new ArrayList<>(tablaRecorridos.getSelectionModel().getSelectedItems());

        if (seleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná al menos un recorrido.");

            return;
        }

        int cantidadActualizada = 0;

        List<String> errores = new ArrayList<>();

        for (Recorrido recorrido : seleccionados) {

            if (recorrido.isHabilitadoRecorrido() == habilitar) {
                continue;
            }

            try {

                if (habilitar) {
                    recorridoDAO.habilitar(recorrido.getIdRecorrido());
                } else {
                    recorridoDAO.baja(recorrido.getIdRecorrido());
                }

                cantidadActualizada++;

            } catch (SQLException e) {
                errores.add("ID " + recorrido.getIdRecorrido() + ": " + e.getMessage());
            }
        }

        cargarRecorridos();

        if (!errores.isEmpty()) {

            Notificaciones.mostrarError(contenedorNotificaciones, "Se actualizaron " + cantidadActualizada +
                            " recorridos. Errores: " + String.join("; ", errores)
            );

        } else if (cantidadActualizada > 0) {

            Notificaciones.mostrarExito(contenedorNotificaciones, cantidadActualizada +
                    (habilitar ? " recorrido(s) habilitado(s) correctamente." : " recorrido(s) deshabilitado(s) correctamente."));

        } else {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Los recorridos seleccionados ya tienen ese estado.");
        }
    }

    public void mostrarExito(String mensaje) {
        Notificaciones.mostrarExito(contenedorNotificaciones, mensaje);
    }
}
