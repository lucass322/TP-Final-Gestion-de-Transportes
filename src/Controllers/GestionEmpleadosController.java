package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import DAO.EmpleadoDAO;
import Empleados.Empleado;
import Utilidades.Notificaciones;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class GestionEmpleadosController {

    @FXML private HBox contenedorCabecera;
    @FXML private Label lblTitulo;

    @FXML private VBox contenedorPrincipal;
    @FXML private HBox contenedorAcciones;

    @FXML private Button btnEditar;
    @FXML private Button btnBaja;
    @FXML private Button btnAlta;

    @FXML private TableView<Empleados.Empleado> tablaEmpleados;

    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombreUsuario;
    @FXML private TableColumn<Empleado, String> colEmail;
    @FXML private TableColumn<Empleado, String> colRol;
    @FXML private TableColumn<Empleado, String> colEstado;

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    private void cargarEmpleados() {
        try {
            List<Empleado> empleados = empleadoDAO.listar();

            tablaEmpleados.setItems(FXCollections.observableArrayList(empleados));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(datos ->
                new SimpleIntegerProperty(
                        datos.getValue().getIdEmpleado()
                ).asObject()
        );

        colNombreUsuario.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().getNombreUsuario()
                )
        );

        colEmail.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().getPersona() != null
                                ? datos.getValue().getPersona().getEmail()
                                : ""
                )
        );

        colRol.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().getRol() != null
                                ? datos.getValue().getRol().getNombreRol()
                                : ""
                )
        );

        colEstado.setCellValueFactory(datos ->
                new SimpleStringProperty(
                        datos.getValue().isHabilitadoUsuario()
                                ? "Habilitado"
                                : "Deshabilitado"
                )
        );
    }

    private void configurarSeleccionEmpleados() {
        tablaEmpleados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void abrirEdicion() {
        ObservableList<Empleado> empleadosSeleccionados =
                tablaEmpleados.getSelectionModel().getSelectedItems();

        BorderPane root = (BorderPane) btnEditar.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        if (empleadosSeleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(
                    panelContenido, "Seleccioná un empleado para editar."
            );

        } else if (empleadosSeleccionados.size() > 1) {
            Notificaciones.mostrarAdvertencia(
                    panelContenido, "Para editar, seleccioná un solo empleado."
            );

        } else {
            Empleado empleadoSeleccionado = empleadosSeleccionados.get(0);

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/views/FormularioUsuario.fxml")
                );

                Parent vista = loader.load();

                FormularioUsuarioController controller = loader.getController();

                controller.setContenedorNotificaciones(panelContenido);
                controller.prepararEdicion(empleadoSeleccionado);

                panelContenido.getChildren().setAll(vista);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void alta() {

        BorderPane root = (BorderPane) btnAlta.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        ObservableList<Empleado> empleadosSeleccionados = tablaEmpleados.getSelectionModel().getSelectedItems();

        if (empleadosSeleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia( panelContenido, "Seleccioná al menos un empleado para habilitar.");

        } else {

            List<Integer> idsEmpleados = new ArrayList<>();

            for (Empleado empleado : empleadosSeleccionados) {

                if (!empleado.isHabilitadoUsuario()) {
                    idsEmpleados.add(empleado.getIdEmpleado());
                }
            }

            if (idsEmpleados.isEmpty()) {

                Notificaciones.mostrarAdvertencia(panelContenido, "Los empleados seleccionados ya están habilitados.");

            } else {

                try {
                    empleadoDAO.cambiarEstado(idsEmpleados, true);

                    cargarEmpleados();

                    Notificaciones.mostrarExito(panelContenido, "Los empleados seleccionados se habilitaron correctamente.");

                } catch (SQLException e) {

                    Notificaciones.mostrarError(panelContenido, "No fue posible habilitar los empleados seleccionados.");

                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void baja() {

        BorderPane root = (BorderPane) btnBaja.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        ObservableList<Empleado> empleadosSeleccionados =  tablaEmpleados.getSelectionModel().getSelectedItems();

        if (empleadosSeleccionados.isEmpty()) {

            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná al menos un empleado para deshabilitar.");

        } else {

            List<Integer> idsEmpleados = new ArrayList<>();

            for (Empleado empleado : empleadosSeleccionados) {

                if (empleado.isHabilitadoUsuario()) {
                    idsEmpleados.add(empleado.getIdEmpleado());
                }
            }

            if (idsEmpleados.isEmpty()) {

                Notificaciones.mostrarAdvertencia(panelContenido, "Los empleados seleccionados ya están deshabilitados.");

            } else {

                try {
                    empleadoDAO.cambiarEstado(idsEmpleados, false);

                    cargarEmpleados();

                    Notificaciones.mostrarExito(panelContenido, "Los empleados seleccionados se deshabilitaron correctamente.");

                } catch (SQLException e) {

                    Notificaciones.mostrarError(panelContenido, "No fue posible deshabilitar los empleados seleccionados.");

                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarSeleccionEmpleados();
        cargarEmpleados();
    }

}
