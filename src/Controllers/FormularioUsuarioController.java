package Controllers;

import DAO.RolDAO;
import DAO.UsuarioDAO;
import Roles.Rol;
import Usuarios.Usuario;
import Utilidades.Validaciones;
import Utilidades.Notificaciones;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

import java.io.IOException;

import java.sql.SQLException;

public class FormularioUsuarioController {
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private ComboBox<Rol> cbRol;
    @FXML private Button btnCancelar;
    @FXML private Button btnRegistrar;
    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;
    @FXML private Label lblPassword;
    @FXML private Label lblConfirmarPassword;

    @FXML
    private StackPane contenedorNotificaciones;

    private final RolDAO rolDAO = new RolDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioEditar;
    private boolean modoEdicion = false;

    @FXML
    private void initialize() {

        cargarRoles();
    }

    public void prepararEdicion(Usuario usuario) {
        this.usuarioEditar = usuario;
        this.modoEdicion = true;

        lblTitulo.setText("Editar usuario");
        lblDescripcion.setText("Modificar los datos del usuario seleccionado.");

        btnRegistrar.setText("Guardar cambios");

        lblPassword.setVisible(false);
        lblPassword.setManaged(false);

        txtPassword.setVisible(false);
        txtPassword.setManaged(false);

        lblConfirmarPassword.setVisible(false);
        lblConfirmarPassword.setManaged(false);

        txtConfirmarPassword.setVisible(false);
        txtConfirmarPassword.setManaged(false);

        txtNombreUsuario.setText(usuario.getNombreUsuario());
        txtEmail.setText(usuario.getEmail());

        cbRol.getItems().stream() //se carga automaticamente el rol actual del usuario, antes de editar
                .filter(rol -> rol.getIdRol() == usuario.getRol().getIdRol())
                .findFirst()
                .ifPresent(rol -> cbRol.getSelectionModel().select(rol));
    }

    private void cargarRoles() {
        try {
            cbRol.getItems().clear();

            Rol opcionInicial = new Rol();
            opcionInicial.setNombreRol("Seleccionar Rol");

            cbRol.getItems().add(opcionInicial);
            cbRol.getItems().addAll(rolDAO.listar());

            cbRol.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionUsuarios.fxml"));

            Parent vista = loader.load();

            BorderPane root = (BorderPane) btnCancelar.getScene().getRoot();
            StackPane panelContenido = (StackPane) root.getCenter();

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registrar() {
        if (modoEdicion) {
            actualizar();
            return;
        }

        boolean rolSeleccionado = cbRol.getSelectionModel().getSelectedIndex() > 0;

        if (!Validaciones.camposCompletos(
                txtNombreUsuario.getText(),
                txtEmail.getText(),
                txtPassword.getText(),
                txtConfirmarPassword.getText(),
                rolSeleccionado
        )) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"Todos los campos son obligatorios.");
            return;
        }

        if (!Validaciones.emailValido(txtEmail.getText())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"Ingresá una dirección de correo electrónico válida.");
            return;
        }

        if (!txtPassword.getText().equals(txtConfirmarPassword.getText())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"Las contraseñas no coinciden.");
            return;
        }

        try {
            if (usuarioDAO.existeNombreUsuario(txtNombreUsuario.getText().trim())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"El nombre de usuario ya está registrado.");
                return;
            }
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo verificar el nombre de usuario.");
            e.printStackTrace();
            return;
        }

        try {
            if (usuarioDAO.existeEmail(txtEmail.getText().trim())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico ya está registrado.");
                return;
            }
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo verificar el correo electrónico.");
            e.printStackTrace();
            return;
        }

        Rol rol = cbRol.getSelectionModel().getSelectedItem();

        Usuario nuevoUsuario = new Usuario(
                rol,
                txtNombreUsuario.getText().trim(),
                txtPassword.getText(),
                txtEmail.getText().trim()
        );

        try {
                usuarioDAO.alta(nuevoUsuario);
                Notificaciones.mostrarExito(contenedorNotificaciones,"Usuario registrado correctamente.");

                //limpia los campos
                txtNombreUsuario.clear();
                txtEmail.clear();
                txtPassword.clear();
                txtConfirmarPassword.clear();
                cbRol.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo registrar el usuario.");
            e.printStackTrace();
        }

    }

    private void actualizar() {
        if (!Validaciones.camposCompletos(txtNombreUsuario.getText(),txtEmail.getText(),
                cbRol.getSelectionModel().getSelectedIndex() > 0)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"Todos los campos son obligatorios.");
            return;
        }

        if (!Validaciones.emailValido(txtEmail.getText())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"Ingresá una dirección de correo electrónico válida.");
            return;
        }

        try {
            if (usuarioDAO.existeNombreUsuario(txtNombreUsuario.getText().trim(),usuarioEditar.getIdUsuario())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"El nombre de usuario ya está registrado.");
                return;
            }
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo verificar el nombre de usuario.");
            e.printStackTrace();
            return;
        }

        try {
            if (usuarioDAO.existeEmail(txtEmail.getText().trim(),usuarioEditar.getIdUsuario())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico ya está registrado.");
                return;
            }
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo verificar el correo electrónico.");
            e.printStackTrace();
            return;
        }

        usuarioEditar.setNombreUsuario(txtNombreUsuario.getText().trim());
        usuarioEditar.setEmail(txtEmail.getText().trim());
        usuarioEditar.setRol(cbRol.getSelectionModel().getSelectedItem());

        //La contraseña no se modifica desde este formulario.
        usuarioEditar.setPasswordUsuario(null);

        try {
            usuarioDAO.modificar(usuarioEditar);

            cancelar(); //regresa al inicio y luego muestra la notificación

            Notificaciones.mostrarExito(contenedorNotificaciones, "Usuario actualizado correctamente.");

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo actualizar el usuario.");
            e.printStackTrace();
        }
    }

    public void setContenedorNotificaciones(StackPane contenedorNotificaciones) {
        this.contenedorNotificaciones = contenedorNotificaciones;
    }
}
