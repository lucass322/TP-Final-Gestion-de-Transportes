package Controllers;

import DAO.UsuarioDAO;
import Usuarios.Usuario;
import Utilidades.Notificaciones;
import Utilidades.Tablas;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.collections.ObservableList;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosController {
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnBaja;
    @FXML
    private Button btnAlta;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private TableColumn<Usuario, Integer> colId;
    @FXML
    private TableColumn<Usuario, String> colNombreUsuario;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, String> colEstado;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private void cargarUsuarios() {
        try {
            tablaUsuarios.getItems().setAll(usuarioDAO.listar());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));

        colRol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getRol() != null
                                ? cellData.getValue().getRol().getNombreRol()
                                : "Sin asignar"
                )
        );
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().isHabilitadoUsuario()
                                ? "Habilitado"
                                : "Deshabilitado"
                )
        );

        Tablas.centrarColumnas(
                colId,
                colNombreUsuario,
                colRol,
                colEstado);

        colId.setStyle("-fx-alignment: CENTER;");
        colNombreUsuario.setStyle("-fx-alignment: CENTER;");
        colRol.setStyle("-fx-alignment: CENTER;");
        colEstado.setStyle("-fx-alignment: CENTER;");

        configurarSeleccionUsuarios();

        cargarUsuarios();
    }

    private void configurarSeleccionUsuarios() {

        tablaUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tablaUsuarios.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<Usuario>) change -> {

                    while (change.next()) {

                        if (change.wasAdded()) {

                            for (Usuario usuario : change.getAddedSubList()) {

                                if (usuario.getRol() != null &&
                                        usuario.getRol().getNombreRol().equalsIgnoreCase("ROOT")) {

                                    Platform.runLater(() -> {

                                        int indice = tablaUsuarios.getItems().indexOf(usuario);

                                        if (indice >= 0) {
                                            tablaUsuarios.getSelectionModel().clearSelection(indice);
                                        }

                                        BorderPane root = (BorderPane) tablaUsuarios.getScene().getRoot();
                                        StackPane panelContenido = (StackPane) root.getCenter();

                                        Notificaciones.mostrarAdvertencia(panelContenido,
                                                "El usuario ROOT no puede seleccionarse."
                                        );
                                    });
                                }
                            }
                        }
                    }
                }
        );
    }

    @FXML
    private void abrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioUsuario.fxml"));

            Parent vista = loader.load();

            BorderPane root = (BorderPane) btnRegistrar.getScene().getRoot();
            StackPane panelContenido = (StackPane) root.getCenter();

            FormularioUsuarioController controller = loader.getController();
            controller.setContenedorNotificaciones(panelContenido);

            panelContenido.getChildren().setAll(vista);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirEdicion() {
        ObservableList<Usuario> usuariosSeleccionados = tablaUsuarios.getSelectionModel().getSelectedItems();
        BorderPane root = (BorderPane) btnEditar.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        if (usuariosSeleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná un usuario para editar.");
            
        }else if (usuariosSeleccionados.size() > 1) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Para editar, seleccioná un solo usuario.");

        }else {
            Usuario usuarioSeleccionado = usuariosSeleccionados.get(0);

            if(usuarioSeleccionado.getRol().getNombreRol().equalsIgnoreCase("ROOT")) {
                Notificaciones.mostrarAdvertencia(panelContenido, "El usuario ROOT no puede modificarse.");
            }else {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FormularioUsuario.fxml"));

                    Parent vista = loader.load();

                    FormularioUsuarioController controller = loader.getController();

                    controller.setContenedorNotificaciones(panelContenido);
                    controller.prepararEdicion(usuarioSeleccionado);

                    panelContenido.getChildren().setAll(vista);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //cambia el estado del usuario
    @FXML
    private void alta() {
        BorderPane root = (BorderPane) btnAlta.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        ObservableList<Usuario> usuariosSeleccionados = tablaUsuarios.getSelectionModel().getSelectedItems();

        if (usuariosSeleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná al menos un usuario para habilitar.");
        } else {
            List<Integer> idsUsuarios = new ArrayList<>();

            for (Usuario usuario : usuariosSeleccionados) {
                if (!usuario.isHabilitadoUsuario()) {
                    idsUsuarios.add(usuario.getIdUsuario());
                }
            }

            if (idsUsuarios.isEmpty()) {
                Notificaciones.mostrarAdvertencia( panelContenido, "Los usuarios seleccionados ya están habilitados.");

            } else {
                try {
                    usuarioDAO.cambiarEstado(idsUsuarios, true);

                    cargarUsuarios();

                    Notificaciones.mostrarExito(panelContenido,"Los usuarios seleccionados se habilitaron correctamente.");

                } catch (SQLException e) {
                    Notificaciones.mostrarError(panelContenido, "No fue posible habilitar los usuarios seleccionados.");
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void baja() {
        BorderPane root = (BorderPane) btnBaja.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        ObservableList<Usuario> usuariosSeleccionados = tablaUsuarios.getSelectionModel().getSelectedItems();

        if (usuariosSeleccionados.isEmpty()) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná al menos un usuario para deshabilitar.");
        } else {
            List<Integer> idsUsuarios = new ArrayList<>();
            boolean rootSeleccionado = false;

            //Recorre los usuarios seleccionados para identificar cuáles pueden deshabilitarse.
            for (Usuario usuario : usuariosSeleccionados) {

                //Comprueba si el usuario tiene un rol asignado y si tiene el rol ROOT.
                boolean esRoot = usuario.getRol() != null && usuario.getRol().getNombreRol().equalsIgnoreCase("ROOT");

                if (esRoot) {
                    rootSeleccionado = true;

                } else if (usuario.isHabilitadoUsuario()) {
                    //Agrega el ID de los usuarios habilitados que pueden darse de baja
                    idsUsuarios.add(usuario.getIdUsuario());
                }
            }

            // Verifica si hay usuarios que necesiten ser deshabilitados.
            if (idsUsuarios.isEmpty()) {
                if (rootSeleccionado) { //Informa que ROOT no puede deshabilitarse.
                    Notificaciones.mostrarAdvertencia(panelContenido, "No es posible dar de baja un usuario ROOT.");
                } else {
                    //Notifica que la operación se realizó correctamente.
                    Notificaciones.mostrarExito(panelContenido,"Los usuarios seleccionados se deshabilitaron correctamente.");
                }

            } else {
                try {
                    //Actualiza masivamente el estado de los usuarios en la base de datos.
                    usuarioDAO.cambiarEstado(idsUsuarios, false);

                    // Recarga la tabla para reflejar los estados actualizados.
                    cargarUsuarios();

                    Notificaciones.mostrarExito( panelContenido,"Los usuarios seleccionados se deshabilitaron correctamente.");

                } catch (SQLException e) {
                    Notificaciones.mostrarError(panelContenido, "No fue posible deshabilitar los usuarios seleccionados.");
                    e.printStackTrace();
                }
            }
        }
    }
}
