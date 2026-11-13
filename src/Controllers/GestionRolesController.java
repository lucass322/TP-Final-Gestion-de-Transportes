
package Controllers;

import DAO.RolDAO;
import Roles.Rol;
import Utilidades.Notificaciones;
import Utilidades.Tablas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;

public class GestionRolesController {

    @FXML private TableView<Rol> tablaRoles;

    @FXML private TableColumn<Rol, Number> colId;
    @FXML private TableColumn<Rol, String> colNombreRol;
    @FXML private TableColumn<Rol, String> colEstado;

    @FXML private Button btnRegistrar;
    @FXML private Button btnEditar;
    @FXML private Button btnBaja;
    @FXML private Button btnAlta;

    private final RolDAO rolDAO = new RolDAO();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarRoles();
    }

    private StackPane obtenerPanelContenido() {
        BorderPane root = (BorderPane) btnRegistrar.getScene().getRoot();
        return (StackPane) root.getCenter();
    }

    @FXML
    private void abrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioRol.fxml"));

            Parent vista = loader.load();

            StackPane panelContenido = obtenerPanelContenido();

            FormularioRolController controller = loader.getController();
            controller.setContenedorNotificaciones(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(
                celda -> new javafx.beans.property.SimpleIntegerProperty(
                        celda.getValue().getIdRol()
                )
        );

        colNombreRol.setCellValueFactory(
                celda -> new javafx.beans.property.SimpleStringProperty(
                        celda.getValue().getNombreRol()
                )
        );

        colEstado.setCellValueFactory(
                celda -> new javafx.beans.property.SimpleStringProperty(
                        celda.getValue().isHabilitadoRol() ? "Habilitado" : "Deshabilitado"
                )
        );

        Tablas.centrarColumnas(colId, colNombreRol, colEstado);
    }

    private void cargarRoles() {
        try {
            tablaRoles.getItems().setAll(rolDAO.listar());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void abrirEdicion() {
        Rol rolSeleccionado = tablaRoles.getSelectionModel().getSelectedItem();
        StackPane panelContenido = obtenerPanelContenido();

        if (rolSeleccionado == null) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná un rol para editar.");
            return;
        }

        if (!rolSeleccionado.isHabilitadoRol()) {
            Notificaciones.mostrarAdvertencia(panelContenido, "No se pueden editar roles deshabilitados.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioRol.fxml"));

            Parent vista = loader.load();

            FormularioRolController controller = loader.getController();
            controller.prepararEdicion(rolSeleccionado);
            controller.setContenedorNotificaciones(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void baja() {

        Rol rolSeleccionado = tablaRoles.getSelectionModel().getSelectedItem();

        StackPane panelContenido = obtenerPanelContenido();

        if (rolSeleccionado == null) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná un rol para deshabilitar.");

            return;
        }

        if (!rolSeleccionado.isHabilitadoRol()) {
            Notificaciones.mostrarAdvertencia(panelContenido, "El rol seleccionado ya está deshabilitado.");

            return;
        }

        if ("ROOT".equalsIgnoreCase(rolSeleccionado.getNombreRol())) {
            Notificaciones.mostrarAdvertencia( panelContenido, "No se puede deshabilitar el rol ROOT.");

            return;
        }

        try {
            rolDAO.baja(rolSeleccionado.getIdRol());

            Notificaciones.mostrarExito(panelContenido, "Rol deshabilitado correctamente.");

            cargarRoles();

        } catch (SQLException e) {
            Notificaciones.mostrarError(panelContenido, "No se pudo deshabilitar el rol.");
            e.printStackTrace();
        }
    }

    @FXML
    private void alta() {

        Rol rolSeleccionado = tablaRoles.getSelectionModel().getSelectedItem();

        StackPane panelContenido = obtenerPanelContenido();

        if (rolSeleccionado == null) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná un rol para habilitar.");

            return;
        }

        if (rolSeleccionado.isHabilitadoRol()) {
            Notificaciones.mostrarAdvertencia(panelContenido, "El rol seleccionado ya está habilitado.");
            return;
        }

        try {
            rolDAO.alta(rolSeleccionado.getIdRol());

            Notificaciones.mostrarExito(panelContenido, "Rol habilitado correctamente.");

            cargarRoles();

        } catch (SQLException e) {
            Notificaciones.mostrarError(panelContenido,"No se pudo habilitar el rol.");
            e.printStackTrace();
        }
    }

}

