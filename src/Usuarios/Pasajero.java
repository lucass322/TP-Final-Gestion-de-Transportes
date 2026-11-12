package Usuarios;
import Utilidades.Encriptacion;

public class Pasajero extends Usuario {
    private int idCliente;

    public void verPerfil(){
        System.out.println(super.toString());
        System.out.println("| Tipo usuario: Pasajero");
        System.out.println("| ID Pasajero: "+idCliente);
    }

    public void editarPerfil(int opcion, String nuevoValor){
            switch (opcion){
                case 1 -> setNombreUsuario(nuevoValor);
                case 2 -> setPasswordUsuario(Encriptacion.hashearPassword(nuevoValor));
            }
    }
}
