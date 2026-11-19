package Controllers;

import DAO.UsuarioDAO;
import Usuarios.Usuario;
import Utilidades.Encriptacion;
import Utilidades.Notificaciones;

import Utilidades.Validaciones;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;

public class SeguridadController {

    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtPasswordNueva;
    @FXML private PasswordField txtConfirmarPassword;

    @FXML private StackPane contenedorNotificaciones;

    private Usuario usuarioActual;
    private StackPane panelContenido;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    @FXML
    private void guardarCambios() {

        String passwordActual = txtPasswordActual.getText();
        String passwordNueva = txtPasswordNueva.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        if (usuarioActual == null) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo identificar al usuario.");
            return;
        }

        if (passwordActual.isBlank() ||
                passwordNueva.isBlank() ||
                confirmarPassword.isBlank()) {

            Notificaciones.mostrarAdvertencia( contenedorNotificaciones, "Completá todos los campos.");
            return;
        }

        if (!Encriptacion.verificarPassword(passwordActual, usuarioActual.getPasswordUsuario())) {

            Notificaciones.mostrarError(contenedorNotificaciones, "La contraseña actual es incorrecta.");
            return;
        }

        if (passwordNueva.equals(passwordActual)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,
                    "La nueva contraseña debe ser diferente a la actual.");
            return;
        }

        if (!Validaciones.passwordValida(passwordNueva)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,
                    "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número.");
            return;
        }

        if (!passwordNueva.equals(confirmarPassword)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Las contraseñas nuevas no coinciden.");
            return;
        }

        try {

            usuarioDAO.actualizarPassword( usuarioActual.getIdUsuario(),passwordNueva);

            // Actualiza el hash en el usuario que permanece en memoria.
            usuarioActual.setPasswordUsuario(Encriptacion.hashearPassword(passwordNueva));

            limpiarCampos();

            Notificaciones.mostrarExito(contenedorNotificaciones, "La contraseña se actualizó correctamente.");

        } catch (SQLException e) {

            e.printStackTrace();

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo actualizar la contraseña.");
        }
    }

    @FXML
    private void cancelar() {

        try {

            FXMLLoader loader = new FXMLLoader( getClass().getResource("/views/Configuracion.fxml"));

            Parent vista = loader.load();

            ConfiguracionController controller = loader.getController();

            controller.setUsuario(usuarioActual);
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            e.printStackTrace();

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a Configuración.");
        }
    }

    private void limpiarCampos() {
        txtPasswordActual.clear();
        txtPasswordNueva.clear();
        txtConfirmarPassword.clear();
    }
}
