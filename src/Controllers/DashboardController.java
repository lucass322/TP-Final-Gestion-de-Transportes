package Controllers;

import DAO.UsuarioDAO;
import DAO.RolDAO;

import java.io.IOException;
import java.sql.SQLException;

import Usuarios.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.stage.Stage;

public class DashboardController {
    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblRol;

    @FXML
    private HBox contenedorIconoUsuarios;

    @FXML
    private Label lblCantidadUsuarios;

    @FXML
    private HBox contenedorIconoRoles;

    @FXML
    private Label lblCantidadRoles;

    @FXML
    private HBox contenedorIconoHabilitados;

    @FXML
    private Label lblCantidadUsuariosHabilitados;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnRoles;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnEmpleados;

    @FXML
    private Button btnReservas;

    @FXML
    private Button btnViajes;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private StackPane panelContenido;

    @FXML
    private VBox contenedorDashboard;

    private Usuario usuarioActual;

    @FXML
    private void initialize() {
        //iconos botones menu principal
        btnDashboard.setGraphic(new FontIcon(FontAwesomeSolid.TACHOMETER_ALT));
        btnRoles.setGraphic(new FontIcon(FontAwesomeSolid.USER_SHIELD));
        btnUsuarios.setGraphic(new FontIcon(FontAwesomeSolid.USERS));
        btnEmpleados.setGraphic(new FontIcon(FontAwesomeSolid.USER_TIE));
        btnReservas.setGraphic(new FontIcon(FontAwesomeSolid.CALENDAR_ALT));
        btnViajes.setGraphic(new FontIcon(FontAwesomeSolid.PLANE));
        btnConfiguracion.setGraphic(new FontIcon(FontAwesomeSolid.COG));
        btnCerrarSesion.setGraphic(new FontIcon(FontAwesomeSolid.SIGN_OUT_ALT));

        FontIcon iconoUsuarios = new FontIcon(FontAwesomeSolid.USERS);
        FontIcon iconoRoles = new FontIcon(FontAwesomeSolid.SHIELD_ALT);
        FontIcon iconoHabilitados = new FontIcon(FontAwesomeSolid.USER_CHECK);

        contenedorIconoUsuarios.getChildren().add(iconoUsuarios);
        contenedorIconoRoles.getChildren().add(iconoRoles);
        contenedorIconoHabilitados.getChildren().add(iconoHabilitados);

        iconoUsuarios.getStyleClass().add("card-icon");
        iconoRoles.getStyleClass().add("card-icon");
        iconoHabilitados.getStyleClass().add("card-icon");

        cargarEstadisticas();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario; //guarda el usuario autenticado
        lblNombreUsuario.setText(usuario.getNombreUsuario());
        lblRol.setText(usuario.getRol().getNombreRol());

        configurarMenuPorRol();
    }

    private String obtenerNombreRol() {
        return usuarioActual.getRol().getNombreRol();
    }

    private void configurarMenuPorRol() {
        btnRoles.setVisible(false);
        btnRoles.setManaged(false);

        btnUsuarios.setVisible(false);
        btnUsuarios.setManaged(false);

        btnEmpleados.setVisible(false);
        btnEmpleados.setManaged(false);

        btnReservas.setVisible(false);
        btnReservas.setManaged(false);

        btnViajes.setVisible(false);
        btnViajes.setManaged(false);

        btnConfiguracion.setVisible(false);
        btnConfiguracion.setManaged(false);

        String nombreRol = obtenerNombreRol();

        switch (nombreRol) {
            case "ROOT":
                btnRoles.setVisible(true);
                btnRoles.setManaged(true);

                btnUsuarios.setVisible(true);
                btnUsuarios.setManaged(true);

                btnEmpleados.setVisible(true);
                btnEmpleados.setManaged(true);

                btnReservas.setVisible(true);
                btnReservas.setManaged(true);

                btnViajes.setVisible(true);
                btnViajes.setManaged(true);

                btnConfiguracion.setVisible(true);
                btnConfiguracion.setManaged(true);
                break;

            case "Empleado":
                btnReservas.setVisible(true);
                btnReservas.setManaged(true);

                btnViajes.setVisible(true);
                btnViajes.setManaged(true);
                break;

            case "Cliente":
                btnReservas.setVisible(true);
                btnReservas.setManaged(true);

                btnViajes.setVisible(true);
                btnViajes.setManaged(true);
                break;
        }
    }

    private void cargarEstadisticas() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            RolDAO rolDAO = new RolDAO();

            lblCantidadUsuarios.setText(String.valueOf(usuarioDAO.contarUsuarios()));
            lblCantidadRoles.setText(String.valueOf(rolDAO.contarRoles()));
            lblCantidadUsuariosHabilitados.setText(String.valueOf(usuarioDAO.contarUsuariosHabilitados()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        private void mostrarGestionUsuarios() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionUsuarios.fxml"));

                Parent vista = loader.load();

                panelContenido.getChildren().setAll(vista);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    @FXML
    private void abrirGestionUsuarios() {
        mostrarGestionUsuarios();
    }

    private void mostrarGestionEmpleados() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/GestionEmpleados.fxml")
            );

            Parent vista = loader.load();

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGestionEmpleados() {
        mostrarGestionEmpleados();
    }

    @FXML
    private void volverDashboard() {
        panelContenido.getChildren().setAll(contenedorDashboard);
        cargarEstadisticas();
    }

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));

            Parent root = loader.load();
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("RutaGo - Iniciar sesión");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
