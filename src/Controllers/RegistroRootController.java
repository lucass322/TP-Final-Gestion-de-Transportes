package Controllers;

import DAO.RolDAO;
import DAO.UsuarioDAO;
import Roles.Rol;
import Usuarios.Usuario;
import Utilidades.Validaciones;
import Utilidades.Notificaciones;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Button btnCrearRoot;

    @FXML
    private VBox contenedorCarga;

    @FXML
    private Label lblCargando;

    @FXML
    private StackPane contenedorNotificaciones;

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

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Todos los campos son obligatorios.");
            return;
        }

        if (!Validaciones.emailValido(email)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Correo electrónico no válido.");
            return;
        }

        if (!password.equals(confirmarPassword)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Las contraseñas no coinciden.");
            return;
        }

        UsuarioDAO usuarioDao = new UsuarioDAO();
        RolDAO rolDao = new RolDAO();
        Rol rol;

        //Se verifica los datos en la base de datos.
        try {
            if(usuarioDao.hayRoot()) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ya existe un usuario ROOT registrado.");
                return;
            }

            if(usuarioDao.existeNombreUsuario(nombreUsuario)) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El nombre de usuario ya está registrado.");
                return;
            }

            if (usuarioDao.existeEmail(email)) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico ya está registrado.");
                return;
            }

            rol = rolDao.buscarRolPorNombre("ROOT");

            if(rol == null || !rol.isHabilitadoRol()) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El rol ROOT no está disponible.");
                return;
            }

        }catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudieron verificar los datos.");
            System.err.println("\nError en el registro: " + e.getMessage());
            return;
        }

        Usuario usuario = new Usuario(rol, nombreUsuario, password, email);

        // Se crea la tarea para registrar el usuario ROOT.
        Task<Boolean> tarea = new Task<>() {

            @Override
            protected Boolean call() throws Exception {
                usuarioDao.alta(usuario);
                return true;
            }
        };

        //Si el registro finaliza correctamente.
        tarea.setOnSucceeded(event -> {
            finalizarCarga();

            if (tarea.getValue()) {
                Notificaciones.mostrarExito(contenedorNotificaciones, "Usuario ROOT creado correctamente.", this::abrirLogin);
            } else {
                Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo registrar el usuario ROOT.");
            }
        });

        //Sí ocurre un error durante el registro.
        tarea.setOnFailed(event -> {
            finalizarCarga();

            System.err.println("Error al registrar usuario ROOT: " + tarea.getException().getMessage());

            Notificaciones.mostrarError(contenedorNotificaciones, "Error al registrar el usuario ROOT.");
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

            scene.getStylesheets().add(getClass().getResource("/styles/vistas/login.css").toExternalForm());

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
}
