package Controllers;

import DAO.RolDAO;
import DAO.UsuarioDAO;
import Roles.Rol;
import Usuarios.Usuario;
import Utilidades.Validaciones;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

public class RegistroRootController {
    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmarPassword;

    @FXML
    private Label lblMensaje;

    @FXML
    private Button btnCrearRoot;

    @FXML
    private VBox contenedorCarga;

    @FXML
    private Label lblCargando;

    @FXML
    private void initialize() {
        contenedorCarga.setVisible(false);
        contenedorCarga.setManaged(false);
    }

    @FXML
    private void crearRoot() {
        String nombreUsuario = txtNombreUsuario.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        if (Validaciones.campoVacio(nombreUsuario) ||
                Validaciones.campoVacio(email) ||
                Validaciones.campoVacio(password) ||
                Validaciones.campoVacio(confirmarPassword)) {

            mostrarMensaje("Todos los campos son obligatorios.", false, null);
            return;
        }

        if (!Validaciones.emailValido(email)) {
            mostrarMensaje("Correo electrónico no válido.", false, null);
            return;
        }

        if (!password.equals(confirmarPassword)) {
            mostrarMensaje("Las contraseñas no coinciden.", false, null);
            return;
        }

        UsuarioDAO usuarioDao = new UsuarioDAO();
        RolDAO rolDao = new RolDAO();
        Rol rol;

        //Se verifica los datos en la base de datos.
        try {
            if(usuarioDao.hayRoot()) {
                mostrarMensaje("Ya existe un usuario ROOT registrado.", false, null);
                return;
            }

            if(usuarioDao.existeNombreUsuario(nombreUsuario)) {
                mostrarMensaje("El nombre de usuario ya está registrado.", false, null);
                return;
            }

            if (usuarioDao.existeEmail(email)) {
                mostrarMensaje("El correo electrónico ya está registrado.", false, null);
                return;
            }

            rol = rolDao.buscarRolPorNombre("ROOT");

            if(rol == null || !rol.isHabilitadoRol()) {
                mostrarMensaje("El rol ROOT no está disponible.", false, null);
                return;
            }

        }catch (SQLException e) {
            mostrarMensaje("No se pudieron verificar los datos.", false, null);
            System.err.println("\nError en el registro: " + e.getMessage());
            return;
        }

        Usuario usuario = new Usuario(rol, nombreUsuario, password, email);

        // Se crea la tarea para registrar el usuario ROOT.
        Task<Boolean> tarea = new Task<>() {

            @Override
            protected Boolean call() throws Exception {
                return usuarioDao.registrarUsuario(usuario);
            }
        };

        //Si el registro finaliza correctamente.
        tarea.setOnSucceeded(event -> {
            finalizarCarga();

            if (tarea.getValue()) {
                mostrarMensaje("Usuario ROOT creado correctamente.",true, this::abrirLogin);
            } else {
                mostrarMensaje("No se pudo registrar el usuario ROOT.", false, null);
            }
        });

        //Sí ocurre un error durante el registro.
        tarea.setOnFailed(event -> {
            finalizarCarga();

            System.err.println("Error al registrar usuario ROOT: " + tarea.getException().getMessage());

            mostrarMensaje("Error al registrar el usuario ROOT.", false, null);
        });

        //Se muestra el indicador de carga.
        contenedorCarga.setVisible(true);
        contenedorCarga.setManaged(true);
        lblCargando.setText("Registrando usuario ROOT...");

        //Se deshabilita el botón mientras se realiza el registro.
        btnCrearRoot.setDisable(true);

        //Se ejecuta la tarea en un hilo secundario.
        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

            Stage stage = (Stage) btnCrearRoot.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("RutaGo - Iniciar sesión");
            stage.sizeToScene();

        } catch (Exception e) {
            System.err.println("Error al abrir el Login: " + e.getMessage());
        }
    }

    private void finalizarCarga() {
        contenedorCarga.setVisible(false);
        contenedorCarga.setManaged(false);
        btnCrearRoot.setDisable(false);
    }

    /**
     * Muestra un mensaje con una animación de aparición gradual.
     *
     * @param mensaje Texto que se desea mostrar.
     */
    private void mostrarMensaje(String mensaje, boolean exitoso, Runnable accionFinal) {

        lblMensaje.setText(mensaje);

        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");

        lblMensaje.getStyleClass().add(exitoso ? "mensaje-exito" : "mensaje-error");

        lblMensaje.setOpacity(0);

        FadeTransition aparecer = new FadeTransition(Duration.millis(350), lblMensaje);
        aparecer.setFromValue(0);
        aparecer.setToValue(1);

        PauseTransition pausa = new PauseTransition(Duration.seconds(3));

        FadeTransition desaparecer = new FadeTransition(Duration.millis(350), lblMensaje);
        desaparecer.setFromValue(1);
        desaparecer.setToValue(0);

        SequentialTransition secuencia = new SequentialTransition(aparecer, pausa, desaparecer);

        secuencia.setOnFinished(event -> {
            if (accionFinal != null) {
                accionFinal.run();
            }
        });

        secuencia.play();
    }
}
