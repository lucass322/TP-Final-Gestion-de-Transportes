package Utilidades;

public class Validaciones {
    public static boolean campoVacio(String valor) { //verifica si el campo está vacío
        return valor == null || valor.isBlank();
    }

    /**
     * Verifica que el correo electrónico cumpla con el formato establecido.
     *
     * @param email Cadena que debe contener caracteres alfanuméricos,
     *              puntos, guiones, guion bajo o signo + antes del @,
     *              y un dominio compuesto por letras, números,
     *              puntos o guiones.
     */
    public static boolean emailValido(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
