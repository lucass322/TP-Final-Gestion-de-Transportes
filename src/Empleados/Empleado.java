package Empleados;

import Personas.Persona;
import Roles.Rol;
import Usuarios.Usuario;

public class Empleado extends Usuario {
    private int idEmpleado;

    public Empleado() {

    }

    public Empleado (int idEmpleado, int idUsuario, Persona persona, Rol rol,
                     String nombreUsuario, String passwordUsuario, boolean habilitadoUsuario) {
        super(idUsuario, persona, rol, nombreUsuario, passwordUsuario, habilitadoUsuario);
        this.idEmpleado = idEmpleado;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
}
