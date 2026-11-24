package Controllers;

import Excepciones.VehiculoNoDisponibleExcepcion;
import Utilidades.Validaciones;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;
import Vehiculos.Vehiculo;
import Vehiculos.GestorVehiculos.GestoraVehiculos;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;
import Utilidades.Notificaciones;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.SQLException;


public class FormularioVehiculoController {

    @FXML
    private StackPane contenedorNotificaciones;

    // Datos generales
    @FXML
    private ComboBox<String> cbTipoVehiculo;

    @FXML
    private TextField txtPatente;

    @FXML
    private TextField txtCapacidadPasajeros;

    @FXML
    private TextField txtCostoPorKm;

    // Panel Avión
    @FXML
    private VBox panelAvion;

    @FXML
    private TextField txtAutonomiaKM;

    @FXML
    private TextField txtCantTurbinas;

    // Panel Colectivo

    @FXML
    private VBox panelColectivo;

    @FXML
    private TextField txtCantidadPisos;

    @FXML
    private CheckBox chkTieneBano;

    // Panel Tren

    @FXML
    private VBox panelTren;

    @FXML
    private TextField txtCantVagones;

    @FXML
    private TextField txtTipoTraccion;

    // Botones
    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnCancelar;

    private GestoraVehiculos gestoraVehiculos;
    private Vehiculo vehiculoEnEdicion;
    private StackPane panelContenido;

    private void mostrarPanelTipo(String tipo) {

        boolean esAvion = "Avión".equals(tipo);
        boolean esColectivo = "Colectivo".equals(tipo);
        boolean esTren = "Tren".equals(tipo);

        panelAvion.setVisible(esAvion);
        panelAvion.setManaged(esAvion);

        panelColectivo.setVisible(esColectivo);
        panelColectivo.setManaged(esColectivo);

        panelTren.setVisible(esTren);
        panelTren.setManaged(esTren);
    }

    public void setPanelContenido(StackPane panelContenido) {
        this.panelContenido = panelContenido;
    }

    private void configurarTipoVehiculo() {

        cbTipoVehiculo.getItems().addAll("Avión", "Colectivo", "Tren");

        cbTipoVehiculo.valueProperty().addListener(
                (observable, anterior, seleccionado) ->
                        mostrarPanelTipo(seleccionado)
        );

        mostrarPanelTipo(null);
    }

    @FXML
    public void initialize() {

        configurarTipoVehiculo();

        try {
            gestoraVehiculos = new GestoraVehiculos();

        } catch (SQLException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "Error al inicializar la gestión de vehículos: " + e.getMessage());
        }
    }

    private boolean validarCampos() {

        String tipo = cbTipoVehiculo.getValue();

        // Validar tipo de vehículo.
        if (Validaciones.campoVacio(tipo)) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Seleccioná un tipo de vehículo.");

            return false;
        }

        // Validar datos generales.
        if (Validaciones.campoVacio(txtPatente.getText())) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ingresá la patente del vehículo.");

            return false;
        }

        if (!Validaciones.enteroPositivoValido(txtCapacidadPasajeros.getText())) {
            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "La capacidad debe ser un entero mayor que cero.");

            return false;
        }

        if (!Validaciones.decimalPositivoValido(txtCostoPorKm.getText())) {

            Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "El costo por KM debe ser un número mayor que cero.");

            return false;
        }

        // Validar datos específicos.
        switch (tipo) {

            case "Avión":

                if (!Validaciones.decimalPositivoValido(txtAutonomiaKM.getText())) {

                    Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "La autonomía debe ser un número mayor que cero.");

                    return false;
                }

                if (!Validaciones.enteroPositivoValido(txtCantTurbinas.getText())) {

                    Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "La cantidad de turbinas debe ser un entero mayor que cero.");

                    return false;
                }
                break;

            case "Colectivo":

                if (!Validaciones.enteroPositivoValido(txtCantidadPisos.getText())) {

                    Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"La cantidad de pisos debe ser un entero mayor que cero.");

                    return false;
                }
                break;

            case "Tren":

                if (!Validaciones.enteroPositivoValido(txtCantVagones.getText())) {

                    Notificaciones.mostrarAdvertencia(contenedorNotificaciones,"La cantidad de vagones debe ser un entero mayor que cero.");

                    return false;
                }

                if (Validaciones.campoVacio(txtTipoTraccion.getText())) {

                    Notificaciones.mostrarAdvertencia(contenedorNotificaciones, "Ingresá el tipo de tracción.");

                    return false;
                }
                break;

            default:
                return false;
        }

        return true;
    }

    private void limpiarCampos() {

        cbTipoVehiculo.setValue(null);

        txtPatente.clear();
        txtCapacidadPasajeros.clear();
        txtCostoPorKm.clear();

        txtAutonomiaKM.clear();
        txtCantTurbinas.clear();

        txtCantidadPisos.clear();
        chkTieneBano.setSelected(false);

        txtCantVagones.clear();
        txtTipoTraccion.clear();

        mostrarPanelTipo(null);
    }

    @FXML
    private void registrarVehiculo() {

        if (!validarCampos()) {
            return;
        }

        if (gestoraVehiculos == null) {
            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo inicializar la gestión de vehículos.");
            return;
        }

        try {

            String patente = txtPatente.getText().trim();

            int capacidad = Integer.parseInt(txtCapacidadPasajeros.getText().trim());

            double costo = Double.parseDouble(txtCostoPorKm.getText().trim().replace(",", "."));

            if (vehiculoEnEdicion == null) {

                // REGISTRAR VEHÍCULO NUEVO
                Vehiculo nuevoVehiculo;

                String tipo = cbTipoVehiculo.getValue();

                switch (tipo) {

                    case "Avión":
                        double autonomia = Double.parseDouble(txtAutonomiaKM.getText().trim().replace(",", "."));

                        int turbinas = Integer.parseInt(txtCantTurbinas.getText().trim());

                        nuevoVehiculo = new Avion(patente, capacidad, costo, true, autonomia, turbinas);
                        break;

                    case "Colectivo":
                        int pisos = Integer.parseInt(txtCantidadPisos.getText().trim());

                        nuevoVehiculo = new Colectivo(patente, capacidad, costo, true, pisos, chkTieneBano.isSelected());
                        break;

                    case "Tren":

                        int vagones = Integer.parseInt(txtCantVagones.getText().trim());

                        String traccion = txtTipoTraccion.getText().trim();

                        nuevoVehiculo = new Tren(patente, capacidad, costo, true, vagones, traccion);
                        break;

                    default:
                        return;
                }

                gestoraVehiculos.altaVehiculo(nuevoVehiculo);

                Notificaciones.mostrarExito(contenedorNotificaciones, "Vehículo registrado correctamente.");

                limpiarCampos();

            } else {
                // MODIFICAR VEHÍCULO EXISTENTE
                vehiculoEnEdicion.setPatente(patente);
                vehiculoEnEdicion.setCapacidadPasajeros(capacidad);
                vehiculoEnEdicion.setCostoPorKm(costo);

                if (vehiculoEnEdicion instanceof Avion avion) {
                    avion.setAutonomiaKM(Double.parseDouble(txtAutonomiaKM.getText().trim().replace(",", ".")));

                    avion.setCantTurbinas(Integer.parseInt(txtCantTurbinas.getText().trim()));

                } else if (vehiculoEnEdicion instanceof Colectivo colectivo) {
                    colectivo.setCantidadPisos(Integer.parseInt(txtCantidadPisos.getText().trim()));

                    colectivo.setTieneBaño(chkTieneBano.isSelected());

                } else if (vehiculoEnEdicion instanceof Tren tren) {
                    tren.setCantVagones(Integer.parseInt(txtCantVagones.getText().trim()));

                    tren.setTipoTraccion(txtTipoTraccion.getText().trim());
                }

                gestoraVehiculos.modificarVehiculo(vehiculoEnEdicion);

                volverGestionVehiculos("Vehículo actualizado correctamente.");
            }

        } catch (SQLException | VehiculoNoDisponibleExcepcion e) {

            Notificaciones.mostrarError(contenedorNotificaciones,"No se pudo guardar el vehículo: " + e.getMessage());
        }
    }

    public void prepararEdicion(Vehiculo vehiculo) {

        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }

        this.vehiculoEnEdicion = vehiculo;

        // Datos generales.
        txtPatente.setText(vehiculo.getPatente());
        txtCapacidadPasajeros.setText(String.valueOf(vehiculo.getCapacidadPasajeros()));
        txtCostoPorKm.setText(String.valueOf(vehiculo.getCostoPorKm()));

        // Identificar el tipo y cargar sus datos específicos.
        if (vehiculo instanceof Avion avion) {

            cbTipoVehiculo.setValue("Avión");

            txtAutonomiaKM.setText(String.valueOf(avion.getAutonomiaKM()));

            txtCantTurbinas.setText(String.valueOf(avion.getCantTurbinas()));

        } else if (vehiculo instanceof Colectivo colectivo) {

            cbTipoVehiculo.setValue("Colectivo");

            txtCantidadPisos.setText(String.valueOf(colectivo.getCantidadPisos()));

            chkTieneBano.setSelected(colectivo.isTieneBaño());

        } else if (vehiculo instanceof Tren tren) {

            cbTipoVehiculo.setValue("Tren");

            txtCantVagones.setText(String.valueOf(tren.getCantVagones()));

            txtTipoTraccion.setText(tren.getTipoTraccion());

        }

        // No se puede cambiar el tipo durante la edición.
        cbTipoVehiculo.setDisable(true);

        btnRegistrar.setText("Guardar cambios");
    }

    private void volverGestionVehiculos(String mensaje) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GestionVehiculos.fxml"));

            Parent vista = loader.load();

            GestionVehiculosController controller = loader.getController();

            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

            controller.mostrarExito(mensaje);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a Gestión de Vehículos: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        try {

            FXMLLoader loader = new FXMLLoader( getClass().getResource("/views/GestionVehiculos.fxml"));

            Parent vista = loader.load();

            GestionVehiculosController controller = loader.getController();
            controller.setPanelContenido(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {

            Notificaciones.mostrarError(contenedorNotificaciones, "No se pudo volver a Gestión de Vehículos: " + e.getMessage());
        }
    }
}
