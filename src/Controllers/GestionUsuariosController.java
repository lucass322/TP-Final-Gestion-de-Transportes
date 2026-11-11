package Controllers;

import DAO.UsuarioDAO;
import Usuarios.Usuario;
import Utilidades.Notificaciones;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

import java.sql.SQLException;

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
    private TableColumn<Usuario, String> colEmail;
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
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
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

        colId.setStyle("-fx-alignment: CENTER;");
        colNombreUsuario.setStyle("-fx-alignment: CENTER;");
        colEmail.setStyle("-fx-alignment: CENTER;");
        colRol.setStyle("-fx-alignment: CENTER;");
        colEstado.setStyle("-fx-alignment: CENTER;");

        cargarUsuarios();
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
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        BorderPane root = (BorderPane) btnEditar.getScene().getRoot();
        StackPane panelContenido = (StackPane) root.getCenter();

        if (usuarioSeleccionado == null) {
            Notificaciones.mostrarAdvertencia(panelContenido, "Seleccioná un usuario para editar.");
            
        }else{
            if (usuarioSeleccionado.getRol().getNombreRol().equalsIgnoreCase("ROOT")) {
                Notificaciones.mostrarAdvertencia(panelContenido, "El usuario ROOT no puede modificarse.");
            } else {
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
}
