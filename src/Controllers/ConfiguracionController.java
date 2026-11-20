package Controllers;

import DAO.NotificacionesDAO;
import Usuarios.Usuario;
import Controllers.SeguridadController;

import Utilidades.Notificaciones;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class ConfiguracionController {

    @FXML
    private VBox tarjetaPerfil;
    @FXML
    private VBox tarjetaSeguridad;
    @FXML
    private VBox tarjetaNotificaciones;
    @FXML
    private VBox tarjetaAcercaDe;
    @FXML
    private StackPane panelContenido;
    @FXML
    private StackPane contenedorNotificaciones;

    private Usuario usuarioActual;

    @FXML
    private void initialize() {
        agregarIcono(tarjetaPerfil, FontAwesomeSolid.USER);
        agregarIcono(tarjetaSeguridad, FontAwesomeSolid.SHIELD_ALT);
        agregarIcono(tarjetaNotificaciones, FontAwesomeSolid.BELL);
        agregarIcono(tarjetaAcercaDe, FontAwesomeSolid.INFO_CIRCLE);
    }

    private void agregarIcono(VBox tarjeta, FontAwesomeSolid icono) {
        FontIcon fontIcon = new FontIcon(icono);

        fontIcon.setIconSize(26);
        fontIcon.setIconColor(Color.web("#E8752F"));

        tarjeta.getChildren().add(0, fontIcon);
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    @FXML
    private void abrirPerfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MiPerfil.fxml"));

            Parent vista = loader.load();

            MiPerfilController controller = loader.getController();
            controller.setUsuario(usuarioActual);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirSeguridad() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/Seguridad.fxml")
            );

            Parent vista = loader.load();

            SeguridadController controller = loader.getController();
            controller.setUsuario(usuarioActual);
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirNotificaciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Notificaciones.fxml"));

            Parent vista = loader.load();

            NotificacionesController controller = loader.getController();
            controller.setUsuario(usuarioActual);
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo abrir la configuración de notificaciones.");
        }
    }

    @FXML
    private void abrirAcercaDe() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/AcercaDe.fxml")
            );

            Parent vista = loader.load();

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
