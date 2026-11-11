package Utilidades;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Notificaciones {
    private static void mostrar(
            StackPane contenedor,
            String mensaje,
            String tipo,
            Runnable accionFinal
    ) {
        String rutaCSS = Notificaciones.class
                .getResource("/styles/componentes/notificaciones.css")
                .toExternalForm();

        if (!contenedor.getStylesheets().contains(rutaCSS)) {
            contenedor.getStylesheets().add(rutaCSS);
        }

        Label notificacion = new Label(mensaje);
        notificacion.getStyleClass().addAll("notificacion", tipo);

        notificacion.setPrefWidth(Label.USE_COMPUTED_SIZE);
        notificacion.setMaxWidth(350);
        notificacion.setWrapText(true);
        notificacion.setOpacity(0);

        contenedor.getChildren().add(notificacion);

        StackPane.setAlignment(notificacion, Pos.TOP_RIGHT);
        StackPane.setMargin(notificacion, new Insets(20));

        FadeTransition entrada = new FadeTransition(Duration.millis(300), notificacion);

        entrada.setFromValue(0);
        entrada.setToValue(1);

        TranslateTransition desplazamiento = new TranslateTransition(Duration.millis(300),notificacion);

        desplazamiento.setFromY(-20);
        desplazamiento.setToY(0);

        entrada.play();
        desplazamiento.play();

        PauseTransition espera = new PauseTransition(Duration.seconds(3));

        FadeTransition salida = new FadeTransition(Duration.millis(300),notificacion);

        salida.setFromValue(1);
        salida.setToValue(0);

        espera.setOnFinished(event -> salida.play());

        espera.play();

        salida.setOnFinished(event -> {
            contenedor.getChildren().remove(notificacion);

            if (accionFinal != null) {
                accionFinal.run();
            }
        });

    }

    public static void mostrarExito(StackPane contenedor,String mensaje) {
        mostrar(contenedor, mensaje, "exito", null);
    }

    public static void mostrarExito(StackPane contenedor, String mensaje, Runnable accionFinal) {
        mostrar(contenedor, mensaje, "exito", accionFinal);
    }

    public static void mostrarError(StackPane contenedor, String mensaje) {
        mostrar(contenedor, mensaje, "error", null);
    }

    public static void mostrarAdvertencia(StackPane contenedor, String mensaje) {
        mostrar(contenedor, mensaje, "advertencia", null);
    }

    public static void mostrarInformacion(StackPane contenedor, String mensaje) {
        mostrar(contenedor, mensaje, "informacion", null);
    }
}
