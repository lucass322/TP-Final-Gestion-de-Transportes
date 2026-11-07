package Controllers;

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

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final FontIcon iconoUsuario = new FontIcon(FontAwesomeSolid.USER);
    private final FontIcon iconoPassword = new FontIcon(FontAwesomeSolid.LOCK);
    private final FontIcon iconoMostrar = new FontIcon(FontAwesomeSolid.EYE);
    private final FontIcon iconoOcultar = new FontIcon(FontAwesomeSolid.EYE_SLASH);

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

        contenedorCarga.setVisible(true);
        contenedorCarga.setManaged(true);

        btnIngresar.setDisable(true);

        Task<Void> tarea = new Task<>() {

            @Override
            protected Void call() throws Exception {

                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(35);
                    updateProgress(i, 100);
                }

                return null;
            }
        };

        lblCargando.textProperty().bind(
                tarea.progressProperty()
                        .multiply(100)
                        .asString("Cargando... %.0f %%")
        );

        tarea.setOnSucceeded(event -> finalizarCarga());

        tarea.setOnFailed(event -> {
            finalizarCarga();
            System.out.println("\nError al iniciar sesión: " + tarea.getException().getMessage());
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
}
