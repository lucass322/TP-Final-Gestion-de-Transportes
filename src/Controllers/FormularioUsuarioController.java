package Controllers;

import DAO.RolDAO;
import DAO.UsuarioDAO;
import Empleados.Empleado;
import DAO.EmpleadoDAO;
import Excepciones.PasajeroExistenteExcepcion;
import Roles.Rol;
import Usuarios.Usuario;
import Personas.Persona;
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
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtNombreUsuario;
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
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private Usuario usuarioEditar;
    private boolean modoEdicion = false;
    private boolean edicionEmpleado = false;

    @FXML
    private void initialize() {

        cargarRoles();
    }

    public void prepararEdicion(Usuario usuario) {
        this.usuarioEditar = usuario;
        this.modoEdicion = true;
        this.edicionEmpleado = usuario instanceof Empleado;

        lblTitulo.setText("Editar usuario");
        lblDescripcion.setText("Modificar los datos del usuario seleccionado.");

        btnRegistrar.setText("Guardar cambios");

        txtNombreUsuario.setText(usuario.getNombreUsuario());

        lblPassword.setText("Nueva contraseña (opcional)");
        lblConfirmarPassword.setText("Confirmar nueva contraseña (opcional)");
        txtPassword.clear();
        txtConfirmarPassword.clear();

        if (usuario.getPersona() != null) {
            txtNombre.setText(usuario.getPersona().getNombre());
            txtApellido.setText(usuario.getPersona().getApellido());
            txtDni.setText(usuario.getPersona().getDni());
            txtTelefono.setText(usuario.getPersona().getTelefono());
            txtEmail.setText(usuario.getPersona().getEmail());
        }

        cbRol.getItems().stream() //se carga automáticamente el rol actual del usuario, antes de editar
                .filter(rol -> rol.getIdRol() == usuario.getRol().getIdRol())
                .findFirst()
                .ifPresent(rol -> cbRol.getSelectionModel().select(rol));

        cbRol.setDisable(usuario instanceof Empleado);
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
            String vistaDestino = edicionEmpleado ? "/views/GestionEmpleados.fxml" : "/views/GestionUsuarios.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(vistaDestino));

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
                txtNombre.getText(),
                txtApellido.getText(),
                txtDni.getText(),
                txtTelefono.getText(),
                txtEmail.getText(),
                txtNombreUsuario.getText(),
                txtPassword.getText(),
                txtConfirmarPassword.getText(),
                rolSeleccionado
        )) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"Todos los campos son obligatorios.");

            return;
        }

        // Verifica que el nombre y el apellido contengan caracteres válidos.
        if (!Validaciones.nombreValido(txtNombre.getText().trim()) ||
                !Validaciones.nombreValido(txtApellido.getText().trim())) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,
                    "El nombre y el apellido contienen caracteres no válidos.");

            return;
        }

        // Verifica que el DNI tenga un formato válido.
        if (!Validaciones.dniValido(txtDni.getText().trim())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El DNI debe contener entre 7 y 8 dígitos.");

            return;
        }

        // Verifica que el teléfono tenga un formato válido.
        if (!Validaciones.telefonoValido(txtTelefono.getText().trim())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El teléfono debe contener entre 8 y 15 dígitos.");

            return;
        }

        // Verifica que el correo electrónico tenga un formato válido.
        if (!Validaciones.emailValido(txtEmail.getText().trim())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico no tiene un formato válido.");

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

            // Verifica si el DNI ya está registrado.
            if (usuarioDAO.existeDni(txtDni.getText().trim())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El DNI ya está registrado.");
                return;
            }

            //Verifica si el email ya está registrado.
            if (usuarioDAO.existeEmail(txtEmail.getText().trim())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico ya está registrado.");
                return;
            }
        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudieron verificar los datos.");
            e.printStackTrace();
            return;
        }

        // Recupera el rol seleccionado.
        Rol rol = cbRol.getSelectionModel().getSelectedItem();

        // Crea el objeto Persona con los datos personales.
        Persona nuevaPersona = new Persona(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtDni.getText().trim(),
                txtTelefono.getText().trim(),
                txtEmail.getText().trim()
        );

        // Crea el objeto Usuario asociado a la Persona.
        Usuario nuevoUsuario = new Usuario(
                nuevaPersona,
                rol,
                txtNombreUsuario.getText().trim(),
                txtPassword.getText()
        );


        try {
            usuarioDAO.alta(nuevoUsuario);
            Notificaciones.mostrarExito(contenedorNotificaciones, "Usuario registrado correctamente.");

            // Limpia los datos personales.
            txtNombre.clear();
            txtApellido.clear();
            txtDni.clear();
            txtTelefono.clear();
            txtEmail.clear();

            // Limpia los datos de la cuenta.
            txtNombreUsuario.clear();
            txtPassword.clear();
            txtConfirmarPassword.clear();

            // Restablece la selección del rol.
            cbRol.getSelectionModel().selectFirst();

        }catch (PasajeroExistenteExcepcion e) {
            Notificaciones.mostrarError(contenedorNotificaciones, e.getMessage());

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo registrar el usuario.");
            e.printStackTrace();
        }
    }

    private void actualizar() {
        if (!Validaciones.camposCompletos(
                txtNombre.getText(),
                txtApellido.getText(),
                txtDni.getText(),
                txtTelefono.getText(),
                txtEmail.getText(),
                txtNombreUsuario.getText(),
                cbRol.getSelectionModel().getSelectedIndex() > 0
        )) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Todos los campos son obligatorios.");

            return;
        }

        // Verifica que el nombre y el apellido contengan caracteres válidos.
        if (!Validaciones.nombreValido(txtNombre.getText().trim()) ||
                !Validaciones.nombreValido(txtApellido.getText().trim())) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones,
                    "El nombre y el apellido contienen caracteres no válidos.");
            return;
        }

        // Verifica que el DNI tenga un formato válido.
        if (!Validaciones.dniValido(txtDni.getText().trim())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El DNI debe contener entre 7 y 8 dígitos.");

            return;
        }

        // Verifica que el teléfono tenga un formato válido.
        if (!Validaciones.telefonoValido(txtTelefono.getText().trim())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El teléfono debe contener entre 8 y 15 dígitos.");

            return;
        }

        // Verifica que el correo electrónico tenga un formato válido.
        if (!Validaciones.emailValido(txtEmail.getText().trim())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico no tiene un formato válido.");

            return;
        }


        try {
            if (usuarioDAO.existeNombreUsuario(txtNombreUsuario.getText().trim(),usuarioEditar.getIdUsuario())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"El nombre de usuario ya está registrado.");
                return;
            }

            // Verifica si el DNI pertenece a otro usuario.
            if (usuarioDAO.existeDni(txtDni.getText().trim(),usuarioEditar.getIdUsuario())) {
                Notificaciones.mostrarAdvertencia( contenedorNotificaciones, "El DNI ya está registrado.");

                return;
            }

            // Verifica si el email pertenece a otro usuario.
            if (usuarioDAO.existeEmail(txtEmail.getText().trim(), usuarioEditar.getIdUsuario())) {
                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El correo electrónico ya está registrado.");

                return;
            }

        } catch (SQLException e) {
            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudieron verificar los datos.");
            e.printStackTrace();
            return;
        }

        // Recupera los valores de los campos de contraseña.
        String password = txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        // Comprueba si los campos están vacíos.
        boolean passwordVacia = Validaciones.campoVacio(password);
        boolean confirmarPasswordVacia = Validaciones.campoVacio(confirmarPassword);

        // Si solamente se completa uno de los campos, muestra una advertencia.
        if (passwordVacia != confirmarPasswordVacia) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Para cambiar la contraseña, completá ambos campos.");

            return;
        }

        // Comprueba que las contraseñas coincidan cuando se desea modificarlas.
        if (!passwordVacia && !password.equals(confirmarPassword)) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Las contraseñas no coinciden.");

            return;
        }

        // Si ambos campos están vacíos, conserva la contraseña actual.
        if (passwordVacia) {
            usuarioEditar.setPasswordUsuario(null);
        } else {
            usuarioEditar.setPasswordUsuario(password);
        }

        // Recupera la Persona asociada al usuario.
        Persona persona = usuarioEditar.getPersona();

        // Actualiza los datos personales.
        persona.setNombre(txtNombre.getText().trim());
        persona.setApellido(txtApellido.getText().trim());
        persona.setDni(txtDni.getText().trim());
        persona.setTelefono(txtTelefono.getText().trim());
        persona.setEmail(txtEmail.getText().trim());

        usuarioEditar.setNombreUsuario(txtNombreUsuario.getText().trim());
        usuarioEditar.setRol(cbRol.getSelectionModel().getSelectedItem());

        try {
            if (usuarioEditar instanceof Empleado) {
                empleadoDAO.modificar((Empleado) usuarioEditar);
            } else {
                usuarioDAO.modificar(usuarioEditar);
            }

            usuarioEditar.setPasswordUsuario(null); // Evita conservar la contraseña sin hashear en el objeto Java.

            String mensaje = edicionEmpleado ? "Empleado actualizado correctamente." : "Usuario actualizado correctamente.";

            cancelar(); //regresa al inicio y luego muestra la notificación

            Notificaciones.mostrarExito(contenedorNotificaciones, mensaje);

        } catch (SQLException e) {
            usuarioEditar.setPasswordUsuario(null);

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo actualizar el usuario.");
            e.printStackTrace();
        }
    }

    public void setContenedorNotificaciones(StackPane contenedorNotificaciones) {
        this.contenedorNotificaciones = contenedorNotificaciones;
    }
}
