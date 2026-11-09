package Usuarios;

import Roles.Rol;

public class Usuario {
    private int idUsuario;
    private Rol rol;
    private String nombreUsuario;
    private String passwordUsuario;
    private String email;
    private boolean habilitadoUsuario;

    public Usuario() {

    }

    public Usuario(Rol rol, String nombreUsuario, String passwordUsuario, String email) {
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        this.passwordUsuario = passwordUsuario;
        this.email = email;
    }

    public Usuario(int idUsuario, Rol rol, String nombreUsuario, String passwordUsuario, String email, boolean habilitadoUsuario) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        this.passwordUsuario = passwordUsuario;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHabilitadoUsuario() {
        return habilitadoUsuario;
    }

    public void setHabilitadoUsuario(boolean habilitadoUsuario) {
        this.habilitadoUsuario = habilitadoUsuario;
    }

    @Override
    public String toString() {
        return "\nID: " + idUsuario + "\n" +
                "| Usuario: " + nombreUsuario + "\n" +
                "| E-mail: " + email + "\n" +
                "| Rol: " + (rol != null ? rol.getNombreRol() : "Sin asignar") + "\n" +
                "| Habilitado: " + habilitadoUsuario;
    }
}
