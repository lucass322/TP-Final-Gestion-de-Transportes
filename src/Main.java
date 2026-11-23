import DAO.UsuarioDAO;
import Database.InicializadorDB;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        InicializadorDB.inicializar();

        UsuarioDAO usuarioDao = new UsuarioDAO();
        boolean existeRoot = usuarioDao.hayRoot();
        String vista;
        String titulo;

        if(existeRoot) {
            vista = "/views/Login.fxml";
            titulo = "RutaGo - Iniciar sesión";
        }else{
            vista = "/views/RegistroRoot.fxml";
            titulo = "RutaGo - Crear usuario ROOT";
        }

        URL recursoVista = getClass().getResource(vista);

        if (recursoVista == null) {
            throw new IOException("No se encontró la vista: " + vista);
        }

        FXMLLoader loader = new FXMLLoader(recursoVista);

        Parent root = loader.load();
        Scene scene = new Scene(root);

        URL recursoCSS = getClass().getResource("/styles/vistas/login.css");

        if (recursoCSS == null) {
            throw new IOException("No se encontró el archivo CSS de Login.");
        }

        scene.getStylesheets().add(recursoCSS.toExternalForm());

        stage.setScene(scene);
        stage.setTitle(titulo);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }
}