package Pasajeros;

import Usuarios.Usuario;
import Personas.Persona;
import Roles.Rol;
import Usuarios.Usuario;
import Utilidades.Encriptacion;

import java.util.Objects;


public class Pasajero extends Usuario {
    private int idPasajero;

    public Pasajero(int idPasajero, int idUsuario, Persona persona, Rol rol, String nombreUsuario,
                    String passwordUsuario, boolean habilitadoUsuario) {

        super(idUsuario, persona, rol, nombreUsuario,
                passwordUsuario, habilitadoUsuario);

        this.idPasajero = idPasajero;
    }

    public void verPerfil(){
        System.out.println(super.toString());
        System.out.println("| Tipo usuario: Pasajero");
        System.out.println("| ID Pasajero: " + idPasajero);
    }

    public void editarPerfil(int opcion, String nuevoValor){
        switch (opcion){
            case 1 -> setNombreUsuario(nuevoValor);
            case 2 -> setPasswordUsuario(nuevoValor);
        }
    }

    public int getIdPasajero() {
        return idPasajero;
    }

    public void setIdPasajero(int idPasajero) {
        this.idPasajero = idPasajero;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pasajero pasajero)) return false;
        return idPasajero == pasajero.idPasajero;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idPasajero);
    }
}