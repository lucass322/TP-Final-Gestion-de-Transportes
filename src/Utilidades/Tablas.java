package Utilidades;

import javafx.scene.control.TableColumn;

public class Tablas {

    private Tablas() {

    }

    public static void centrarColumnas(TableColumn<?, ?>... columnas) {
        for (TableColumn<?, ?> columna : columnas) {
            columna.setStyle("-fx-alignment: CENTER;");
        }
    }
}
