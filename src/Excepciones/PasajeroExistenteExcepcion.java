package Excepciones;

import java.sql.SQLException;

public class PasajeroExistenteExcepcion extends SQLException {
    public PasajeroExistenteExcepcion(String message) {

        super(message);
    }
}
