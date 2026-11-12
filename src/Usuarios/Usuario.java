package Usuarios;

import Roles.Rol;
import Personas.Persona;

public class Usuario {
    private int idUsuario;
    private Rol rol;
    private String nombreUsuario;
    private String passwordUsuario;
    private boolean habilitadoUsuario;
    private Persona persona;

    public Usuario() {

    }

    // Constructor para usuarios sin persona asociada (ROOT).
    public Usuario(Rol rol, String nombreUsuario, String passwordUsuario) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        this.passwordUsuario = passwordUsuario;
    }

    public Usuario(Persona persona, Rol rol, String nombreUsuario, String passwordUsuario) {
        this.persona = persona;
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        this.passwordUsuario = passwordUsuario;
    }

    public Usuario(int idUsuario, Persona persona, Rol rol, String nombreUsuario, String passwordUsuario, boolean habilitadoUsuario) {
        this.idUsuario = idUsuario;
        this.persona = persona;
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        this.passwordUsuario = passwordUsuario;
        this.habilitadoUsuario = habilitadoUsuario;
    }

    public int getIdUsuario() {

        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {

        this.idUsuario = idUsuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPasswordUsuario() {
        return passwordUsuario;
    }

    public void setPasswordUsuario(String passwordUsuario) {
        this.passwordUsuario = passwordUsuario;
    }


    public boolean isHabilitadoUsuario() {
        return habilitadoUsuario;
    }

    public void setHabilitadoUsuario(boolean habilitadoUsuario) {
        this.habilitadoUsuario = habilitadoUsuario;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public String toString() {
        return "\nID: " + idUsuario + "\n" +
                "| Persona: " + (persona != null ? persona.getNombre() + " " + persona.getApellido() : "Sin asignar") + "\n" +
                "| Usuario: " + nombreUsuario + "\n" +
                "| Rol: " + (rol != null ? rol.getNombreRol() : "Sin asignar") + "\n" +
                "| Habilitado: " + (habilitadoUsuario ? "Sí" : "No");
    }
}
