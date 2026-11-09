package Controllers;

import Usuarios.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;

public class MenuPrincipalRootController {
    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblBienvenida;

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
    private Button btnUsuarios;

    @FXML
    private Button btnRoles;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private void initialize() {
        btnDashboard.setGraphic(new FontIcon(FontAwesomeSolid.TACHOMETER_ALT));
        btnUsuarios.setGraphic(new FontIcon(FontAwesomeSolid.USERS));
        btnRoles.setGraphic(new FontIcon(FontAwesomeSolid.USER_SHIELD));
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

    public void setUsuario(Usuario usuario) {
        lblNombreUsuario.setText(usuario.getNombreUsuario());
        lblRol.setText(usuario.getRol().getNombreRol());
    }
}
