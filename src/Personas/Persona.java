package Personas;

public class Persona {
    private int idPersona;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private boolean habilitadoPersona;

    public Persona() {

    }

    public Persona(String nombre, String apellido, String dni, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.habilitadoPersona = true;
    }

    public Persona(int idPersona, String nombre, String apellido, String dni,
                   String telefono, String email, boolean habilitadoPersona) {
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.habilitadoPersona = habilitadoPersona;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public boolean isHabilitadoPersona() {
        return habilitadoPersona;
    }

    public void setHabilitadoPersona(boolean habilitadoPersona) {
        this.habilitadoPersona = habilitadoPersona;
    }

    @Override
    public String toString() {
        return "\nID: " + idPersona + "\n" +
                "Nombre: " + nombre + "\n" +
                "Apellido: " + apellido + "\n" +
                "DNI: " + dni + "\n" +
                "Teléfono: " + "\n" +
                "E-mail: " + email + "\n" +
                "Estado: " + (habilitadoPersona ? "Habilitada" : "Deshabilitada");
    }
}
