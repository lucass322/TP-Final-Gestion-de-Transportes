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
    private HBox contenedorTarjetas;

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

    private String obtenerNombreRol() {

        if(usuarioActual == null || usuarioActual.getRol() == null) {
            return "";
        }

        return usuarioActual.getRol().getNombreRol();
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario; //guarda el usuario autenticado

        lblNombreUsuario.setText(usuario.getNombreUsuario());
        lblRol.setText(obtenerNombreRol());

        configurarMenuPorRol();

        contenedorTarjetas.setVisible(esRoot());
        contenedorTarjetas.setManaged(esRoot());

        if(esRoot()) {
            cargarEstadisticas();
        }
    }

    private void configurarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    private void configurarMenuRoot() {
        configurarBoton(btnRoles, true);
        configurarBoton(btnUsuarios, true);
        configurarBoton(btnEmpleados, true);
        configurarBoton(btnReservas, true);
        configurarBoton(btnViajes, true);
        configurarBoton(btnConfiguracion, true);
    }

    private void configurarMenuEmpleado() {
        configurarBoton(btnReservas, true);
        configurarBoton(btnViajes, true);
        configurarBoton(btnConfiguracion, true);
    }

    private void configurarMenuPredeterminado() {
        configurarBoton(btnReservas, true);
        configurarBoton(btnViajes, true);
        configurarBoton(btnConfiguracion, true);
    }

    private void configurarMenuPorRol() {

        String nombreRol = obtenerNombreRol().toUpperCase();

        // Ocultar todas las opciones.
        configurarBoton(btnRoles, false);
        configurarBoton(btnUsuarios, false);
        configurarBoton(btnEmpleados, false);
        configurarBoton(btnReservas, false);
        configurarBoton(btnViajes, false);
        configurarBoton(btnConfiguracion, false);

        switch (nombreRol) {

            case "ROOT":
                configurarMenuRoot();
                break;

            case "EMPLEADO":
                configurarMenuEmpleado();
                break;

            default:
                configurarMenuPredeterminado();
                break;
        }
    }

    private void mostrarGestionRoles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionRoles.fxml"));

            Parent vista = loader.load();

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGestionRoles() {

        if(!esRoot()) {
            return;
        }

        mostrarGestionRoles();
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
        if(!esRoot()) {
            return;
        }

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

        if(!esRoot()){
            return;
        }

        mostrarGestionEmpleados();
    }

    private void mostrarConfiguracion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Configuracion.fxml"));

            Parent vista = loader.load();

            ConfiguracionController controller = loader.getController();
            controller.setUsuario(usuarioActual);
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirConfiguracion() {

        mostrarConfiguracion();
    }


    @FXML
    private void volverDashboard() {
        panelContenido.getChildren().setAll(contenedorDashboard);

        if(esRoot()) {
            cargarEstadisticas();
        }
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

    private boolean esRoot() {
        return "ROOT".equalsIgnoreCase(obtenerNombreRol());
    }
}
