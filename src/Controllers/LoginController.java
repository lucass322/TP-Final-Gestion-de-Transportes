package Controllers;

import DAO.UsuarioDAO;
import Usuarios.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;
import javafx.scene.control.Control;
import java.util.Arrays;
import javafx.concurrent.Task;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import Utilidades.Validaciones;

public class LoginController implements Initializable {
    private final FontIcon iconoUsuario = new FontIcon(FontAwesomeSolid.USER);
    private final FontIcon iconoPassword = new FontIcon(FontAwesomeSolid.LOCK);
    private final FontIcon iconoMostrar = new FontIcon(FontAwesomeSolid.EYE);
    private final FontIcon iconoOcultar = new FontIcon(FontAwesomeSolid.EYE_SLASH);
    private PauseTransition pausaError;

    @FXML
    private HBox contenedorUsuario;

    @FXML
    private TextField txtUsuario;

    @FXML
    private HBox contenedorPasswordCompleto;

    @FXML
    private HBox contenedorPassword;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private Button btnMostrarPassword;

    @FXML
    private Button btnIngresar;

    @FXML
    private VBox contenedorCarga;

    @FXML
    private ProgressIndicator progressLogin;

    @FXML
    private Label lblCargando;

    @FXML
    private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iconoUsuario.setIconSize(14);
        iconoUsuario.setIconColor(Color.web("#999999"));

        iconoPassword.setIconSize(14);
        iconoPassword.setIconColor(Color.web("#999999"));

        iconoMostrar.setIconSize(18);
        iconoMostrar.setIconColor(Color.web("#FF8C42"));

        iconoOcultar.setIconSize(18);
        iconoOcultar.setIconColor(Color.web("#FF8C42"));

        btnMostrarPassword.setGraphic(iconoMostrar);

        btnMostrarPassword.setOnMouseEntered(event -> {
            if (btnMostrarPassword.getGraphic() == iconoMostrar) {
                iconoMostrar.setIconColor(Color.web("#D0D7E2"));
            } else {
                iconoOcultar.setIconColor(Color.web("#D0D7E2"));
            }
        });

        btnMostrarPassword.setOnMouseExited(event -> {
            iconoMostrar.setIconColor(Color.web("#FF8C42"));
            iconoOcultar.setIconColor(Color.web("#FF8C42"));
        });

        contenedorUsuario.getChildren().add(0, iconoUsuario);

        contenedorPassword.getChildren().add(0, iconoPassword);

        configurarFoco(contenedorUsuario, txtUsuario);

        configurarFoco(contenedorPassword, txtPassword, txtPasswordVisible);

        txtUsuario.setOnAction(event -> txtPassword.requestFocus());
        txtPassword.setOnAction(event -> iniciarSesion());
        txtPasswordVisible.setOnAction(event -> iniciarSesion());
    }

    private void configurarFoco(HBox contenedor, Control... campos) {
        for (Control campo : campos) {
            campo.focusedProperty().addListener((observable, anterior, nuevo) -> {
                boolean tieneFoco = Arrays.stream(campos).anyMatch(Control::isFocused);

                if (tieneFoco) {
                    contenedor.getStyleClass().add("contenedor-input-activo");
                } else {
                    contenedor.getStyleClass().remove("contenedor-input-activo");
                }
            });
        }
    }

    @FXML
    private void alternarPassword() {
        if (txtPassword.isVisible()) {

            txtPasswordVisible.setText(txtPassword.getText());

            txtPassword.setVisible(false);
            txtPassword.setManaged(false);

            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);

            btnMostrarPassword.setGraphic(iconoOcultar);

        } else {

            txtPassword.setText(txtPasswordVisible.getText());

            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);

            txtPassword.setVisible(true);
            txtPassword.setManaged(true);

            btnMostrarPassword.setGraphic(iconoMostrar);
        }
    }

    @FXML
    private void iniciarSesion() {
        //Se obtiene los valores de los campos usuario y password
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (Validaciones.campoVacio(usuario) || Validaciones.campoVacio(password)) {
            mostrarError("Completá todos los campos."); //aparece el mensaje y después desaparece en 2 seg
            return;
        }

        contenedorCarga.setVisible(true);
        contenedorCarga.setManaged(true);

        btnIngresar.setDisable(true);

        lblCargando.setText("Verificando credenciales...");

        Task<Usuario> tarea = new Task<>() {

            @Override
            protected Usuario call() throws Exception {

                UsuarioDAO usuarioDAO = new UsuarioDAO();

                return usuarioDAO.obtenerUsuarioAutenticado(usuario, password);
            }
        };

        tarea.setOnSucceeded(event -> {
            finalizarCarga();

            Usuario usuarioAutenticado = tarea.getValue();

            if (usuarioAutenticado == null) {
                mostrarError("Usuario o contraseña incorrectos.");
                return;
            }

            System.out.println("\nInicio de sesión exitoso.");

            String nombreRol = usuarioAutenticado.getRol().getNombreRol();

            if (nombreRol.equals("ROOT")) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));

                    Parent root = loader.load();

                    DashboardController controller = loader.getController();
                    controller.setUsuario(usuarioAutenticado);

                    Stage stage = (Stage) btnIngresar.getScene().getWindow();

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/styles/vistas/dashboard.css").toExternalForm());

                    stage.setScene(scene);
                    stage.setTitle("RutaGo - Panel ROOT");
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.centerOnScreen();

                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarError("No se pudo cargar el panel de administración.");
                }
            }
        });

        tarea.setOnFailed(event -> {
            finalizarCarga();

            System.out.println("\nError al iniciar sesión: " + tarea.getException().getMessage());

            mostrarError("Ocurrió un error al iniciar sesión.");
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void finalizarCarga() {

        lblCargando.textProperty().unbind();

        contenedorCarga.setVisible(false);
        contenedorCarga.setManaged(false);

        btnIngresar.setDisable(false);
    }

    private void mostrarError(String mensaje) {

        //Si existe una pausa anterior, la detiene
        if (pausaError != null) {
            pausaError.stop();
        }

        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);

        PauseTransition pausa = new PauseTransition(Duration.seconds(2));

        pausa.setOnFinished(event -> {
            lblError.setVisible(false);
            lblError.setManaged(false);
        });

        pausa.play();
    }
}
