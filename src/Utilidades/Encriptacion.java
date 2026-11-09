package Utilidades;

import org.mindrot.jbcrypt.BCrypt;

public class Encriptacion {
    public static String hashearPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verificarPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
