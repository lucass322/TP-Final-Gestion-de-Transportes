package Controllers;

import Usuarios.Usuario;
import Personas.Persona;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MiPerfilController {

    @FXML private Label lblNombre;
    @FXML private Label lblApellido;
    @FXML private Label lblDni;
    @FXML private Label lblTelefono;
    @FXML private Label lblEmail;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRol;

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarDatos();
    }

    private void cargarDatos() {

        if (usuarioActual == null) {
            return;
        }

        // Datos de la cuenta
        lblNombreUsuario.setText(usuarioActual.getNombreUsuario());
        lblRol.setText(usuarioActual.getRol().getNombreRol());

        // Datos personales
        Persona persona = usuarioActual.getPersona();

        if (persona != null) {
            lblNombre.setText(persona.getNombre());
            lblApellido.setText(persona.getApellido());
            lblDni.setText(persona.getDni());
            lblTelefono.setText(persona.getTelefono());
            lblEmail.setText(persona.getEmail());
        } else {
            lblNombre.setText("No disponible");
            lblApellido.setText("No disponible");
            lblDni.setText("No disponible");
            lblTelefono.setText("No disponible");
            lblEmail.setText("No disponible");
        }
    }
}

