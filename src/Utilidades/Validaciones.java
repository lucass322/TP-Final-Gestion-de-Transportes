package Utilidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validaciones {
    public static boolean campoVacio(String valor) { //verifica si el campo está vacío
        return valor == null || valor.isBlank();
    }

    public static boolean emailValido(String email) {
        return email != null &&
                email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
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
                nombre.matches("^[\\p{L}]+([ '-][\\p{L}]+)*$");
    }

    public static boolean dniValido(String dni) {
        return dni != null && dni.matches("\\d{7,8}");
    }

    public static boolean telefonoValido(String telefono) {
        return telefono != null && telefono.matches("^[0-9]{8,15}$");
    }

    public static boolean passwordValida(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*");
    }

    public static boolean enteroPositivoValido(String valor) {

        if (campoVacio(valor)) {
            return false;
        }

        try {
            return Integer.parseInt(valor.trim()) > 0;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean decimalPositivoValido(String valor) {

        if (campoVacio(valor)) {
            return false;
        }

        try {
            double numero = Double.parseDouble(valor.trim().replace(",", "."));

            return Double.isFinite(numero) && numero > 0;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean fechaNoAnterior(LocalDate fecha) {
        return fecha != null && !fecha.isBefore(LocalDate.now());
    }

    public static boolean horaValida(String hora) {
        if (campoVacio(hora)) {
            return false;
        }

        try {
            LocalTime.parse(hora.trim(),DateTimeFormatter.ofPattern("HH:mm"));

            return true;

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean fechaHoraNoAnterior(LocalDate fecha, String hora) {

        if (fecha == null || !horaValida(hora)) {
            return false;
        }

        LocalDateTime fechaHoraSalida = LocalDateTime.of(fecha,LocalTime.parse(hora.trim(),DateTimeFormatter.ofPattern("HH:mm")));

        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);

        return !fechaHoraSalida.isBefore(ahora);
    }
}
