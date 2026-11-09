import DAO.UsuarioDAO;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle(titulo);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }
}