package Roles;

public class Rol {
    private int idRol;
    private String nombreRol;
    private boolean habilitadoRol;

    public Rol() {

    }

    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Rol(int idRol, String nombreRol, boolean habilitadoRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
        this.habilitadoRol = habilitadoRol;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public boolean isHabilitadoRol() {
        return habilitadoRol;
    }

    public void setHabilitadoRol(boolean habilitadoRol) {
        this.habilitadoRol = habilitadoRol;
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}
