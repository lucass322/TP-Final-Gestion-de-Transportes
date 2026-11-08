import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

        stage.setScene(scene);

        stage.setTitle("RutaGo - Iniciar sesión");
        stage.setWidth(600);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.show();
    }
}