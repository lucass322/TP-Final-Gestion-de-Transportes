package Pasajeros;

public class Pasajero {
    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    // Constructor
    public Pasajero(String dni, String nombre, String apellido, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    public Pasajero(int id ,String dni, String nombre, String apellido, String telefono, String email) {
        this.dni = dni;
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    public void mostrarDatosPasajero() {
        System.out.println("Pasajero: " + apellido + ", " + nombre + " | DNI: " + dni);
        System.out.println("Contacto: " + telefono + " - " + email);
    }

    // Getters y Setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }
}