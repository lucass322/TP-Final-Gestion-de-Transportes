package Controllers;

import Configuracion.PreferenciasNotificaciones;
import DAO.NotificacionesDAO;
import Usuarios.Usuario;
import Utilidades.Notificaciones;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;

public class NotificacionesController {

    @FXML
    private CheckBox chkReservas;

    @FXML
    private CheckBox chkCancelaciones;

    @FXML
    private CheckBox chkCambiosViajes;

    @FXML
    private StackPane contenedorNotificaciones;

    private Usuario usuarioActual;
    private StackPane panelContenido;

    private final NotificacionesDAO notificacionesDAO = new NotificacionesDAO();

    private void cargarPreferencias() {
        try {
            PreferenciasNotificaciones preferencias =
                    notificacionesDAO.consultarPreferencias(usuarioActual.getIdUsuario());

            if (preferencias != null) {
                chkReservas.setSelected(preferencias.isNotificarReservas());
                chkCancelaciones.setSelected(preferencias.isNotificarCancelaciones());
                chkCambiosViajes.setSelected(preferencias.isNotificarCambiosViajes());
            } else {
                // Si todavía no hay preferencias guardadas, se habilitan todas.
                chkReservas.setSelected(true);
                chkCancelaciones.setSelected(true);
                chkCambiosViajes.setSelected(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarCambios() {
        if (usuarioActual == null) {
            return;
        }

        PreferenciasNotificaciones preferencias =
                new PreferenciasNotificaciones(
                        usuarioActual.getIdUsuario(),
                        chkReservas.isSelected(),
                        chkCancelaciones.isSelected(),
                        chkCambiosViajes.isSelected()
                );

        try {
            notificacionesDAO.guardarPreferencias(preferencias);
            Notificaciones.mostrarExito(contenedorNotificaciones, "Preferencias guardadas correctamente.");

        } catch (Exception e) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudieron guardar las preferencias.");
            e.printStackTrace();
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarPreferencias();
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    @FXML
    private void initialize() {

    }
}
