package Controllers;

import DAO.RecorridoDAO;
import Viajes.Recorrido;
import Utilidades.Notificaciones;
import Utilidades.Validaciones;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

import java.sql.SQLException;

public class FormularioRecorridoController {

    @FXML
    private StackPane contenedorNotificaciones;

    @FXML
    private TextField txtOrigen;

    @FXML
    private TextField txtDestino;

    @FXML
    private TextField txtDistancia;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnCancelar;

    private final RecorridoDAO recorridoDAO = new RecorridoDAO();
    private StackPane panelContenido;
    private Recorrido recorridoEnEdicion;

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    private boolean validarCampos() {

        String origen = txtOrigen.getText().trim();
        String destino = txtDestino.getText().trim();
        String distanciaTexto = txtDistancia.getText().trim();

        if (Validaciones.campoVacio(origen) ||
                Validaciones.campoVacio(destino) ||
                Validaciones.campoVacio(distanciaTexto)) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Completá todos los campos.");

            return false;
        }

        if (origen.equalsIgnoreCase(destino)) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El origen y el destino no pueden ser iguales.");

            return false;
        }

        try {

            double distancia = Double.parseDouble(distanciaTexto.replace(",", "."));

            if (!Double.isFinite(distancia) || distancia <= 0) {

                Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "La distancia debe ser un número mayor que cero.");

                return false;
            }

        } catch (NumberFormatException e) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "La distancia debe tener un formato numérico válido.");

            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtOrigen.clear();
        txtDestino.clear();
        txtDistancia.clear();
    }

    @FXML
    private void registrarRecorrido() {

        if (!validarCampos()) {
            return;
        }

        try {
            String origen = txtOrigen.getText().trim();
            String destino = txtDestino.getText().trim();

            double distancia = Double.parseDouble(txtDistancia.getText().trim().replace(",", "."));

            if (recorridoEnEdicion == null) {

                // Registrar un recorrido nuevo.
                Recorrido recorrido = new Recorrido(
                        0,
                        origen,
                        destino,
                        distancia
                );

                recorridoDAO.alta(recorrido);

                Notificaciones.mostrarExito(contenedorNotificaciones, "Recorrido registrado correctamente.");

                limpiarCampos();

            } else {

                // Modificar el recorrido existente.
                recorridoEnEdicion.setOrigen(origen);
                recorridoEnEdicion.setDestino(destino);
                recorridoEnEdicion.setDistanciaKM(distancia);

                recorridoDAO.modificar(recorridoEnEdicion);

                volverGestionRecorridos("Recorrido actualizado correctamente.");
            }

        } catch (SQLException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo guardar el recorrido: " + e.getMessage());
        }
    }

    public void prepararEdicion(Recorrido recorrido) {

        if (recorrido == null) {
            throw new IllegalArgumentException("El recorrido no puede ser nulo.");
        }

        this.recorridoEnEdicion = recorrido;

        txtOrigen.setText(recorrido.getOrigen());
        txtDestino.setText(recorrido.getDestino());
        txtDistancia.setText(String.valueOf(recorrido.getDistanciaKM()));

        btnRegistrar.setText("Guardar cambios");
    }

    private void volverGestionRecorridos(String mensaje) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionRecorridos.fxml"));

            Parent vista = loader.load();

            GestionRecorridosController controller = loader.getController();
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

            controller.mostrarExito(mensaje);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a Gestión de Recorridos: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionRecorridos.fxml"));

            Parent vista = loader.load();

            GestionRecorridosController controller = loader.getController();
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError( contenedorNotificaciones, "No se pudo volver a Gestión de Recorridos: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {

    }
}
