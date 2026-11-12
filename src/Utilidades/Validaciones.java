package Utilidades;

public class Validaciones {
    public static boolean campoVacio(String valor) { //verifica si el campo está vacío
        return valor == null || valor.isBlank();
    }

    public static boolean emailValido(String email) {
        return email != null &&
                email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        //comprueba que exista un dominio y una extensión.
    }

    public static boolean camposCompletos(
            String nombreUsuario,
            String password,
            String confirmarPassword,
            boolean rolSeleccionado
    ) {
        return !campoVacio(nombreUsuario)
                && !campoVacio(password)
                && !campoVacio(confirmarPassword)
                && rolSeleccionado;
    }

    public static boolean camposCompletos(
            String nombreUsuario,
            boolean rolSeleccionado
    ) {
        return !campoVacio(nombreUsuario)
                && rolSeleccionado;
    }

    public static boolean camposCompletos(
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String email,
            String nombreUsuario,
            String password,
            String confirmarPassword,
            boolean rolSeleccionado
    ) {
        return !campoVacio(nombre)
                && !campoVacio(apellido)
                && !campoVacio(dni)
                && !campoVacio(telefono)
                && !campoVacio(email)
                && !campoVacio(nombreUsuario)
                && !campoVacio(password)
                && !campoVacio(confirmarPassword)
                && rolSeleccionado;
    }

    public static boolean camposCompletos(
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String email,
            String nombreUsuario,
            boolean rolSeleccionado
    ) {
        return !campoVacio(nombre)
                && !campoVacio(apellido)
                && !campoVacio(dni)
                && !campoVacio(telefono)
                && !campoVacio(email)
                && !campoVacio(nombreUsuario)
                && rolSeleccionado;
    }

    public static boolean nombreValido(String nombre) {
        return nombre != null &&
                nombre.matches("^[\\p{L}]+([ '-][\\p{L}]+)*$"); //Permite letras, acentos, espacios, apóstrofes y guiones.
    }

    public static boolean dniValido(String dni) {
        return dni != null && dni.matches("\\d{7,8}"); //Acepta DNI de 7 u 8 dígitos, sin puntos.
    }

    public static boolean telefonoValido(String telefono) {
        return telefono != null && telefono.matches("^[0-9]{8,15}$"); //permite teléfonos de entre 8 y 15 dígitos.
    }
}
