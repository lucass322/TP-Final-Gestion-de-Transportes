package Controllers;

import DAO.RolDAO;
import Roles.Rol;
import Utilidades.Notificaciones;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;

public class FormularioRolController {

    @FXML
    private TextField txtNombreRol;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnAgregar;

    private StackPane contenedorNotificaciones;

    private final RolDAO rolDAO = new RolDAO();
    private Rol rolEditar;
    private boolean modoEdicion = false;

    public void setContenedorNotificaciones(StackPane contenedor) {
        this.contenedorNotificaciones = contenedor;
    }

    public void prepararEdicion(Rol rol) {
        this.rolEditar = rol;
        this.modoEdicion = true;

        // Cambiar los textos del formulario.
        txtNombreRol.setText(rol.getNombreRol());

        // Cambiar el texto del botón.
        btnAgregar.setText("Guardar cambios");
    }

    @FXML
    private void cancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionRoles.fxml"));

            Parent vista = loader.load();

            BorderPane root = (BorderPane) btnCancelar.getScene().getRoot();
            StackPane panelContenido = (StackPane) root.getCenter();

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void agregar() {

        String nombre = txtNombreRol.getText().trim();

        if (nombre.isEmpty()) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ingresá el nombre del rol.");
            return;
        }

        try {

            if (modoEdicion) {
                rolEditar.setNombreRol(nombre);
                rolDAO.modificar(rolEditar);

            } else {
                Rol rol = new Rol(nombre);
                rolDAO.alta(rol);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionRoles.fxml"));

            Parent vista = loader.load();

            BorderPane root = (BorderPane) btnAgregar.getScene().getRoot();
            StackPane panelContenido = (StackPane) root.getCenter();

            panelContenido.getChildren().setAll(vista);

            if (modoEdicion) {
                Notificaciones.mostrarExito(
                        panelContenido, "Rol modificado correctamente.");
            } else {
                Notificaciones.mostrarExito(panelContenido, "Rol registrado correctamente.");
            }

        } catch (IllegalArgumentException e) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,e.getMessage());

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo guardar el rol.");
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

