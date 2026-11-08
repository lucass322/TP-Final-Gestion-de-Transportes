package Utilidades;

public class Validaciones {
    public static boolean campoVacio(String valor) { //verifica si el campo esta vacío
        return valor == null || valor.isBlank();
    }


}
