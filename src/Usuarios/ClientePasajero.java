package Usuarios;
import Utilidades.Encriptacion;

public class ClientePasajero extends Usuario {
    private int idCliente;

    public void verPerfil(){
        System.out.println(super.toString());
        System.out.println("| Tipo usuario: Cliente/Pasajero");
        System.out.println("| ID Cliente: "+idCliente);
    }

    public void editarPerfil(int opcion, String nuevoValor){
            switch (opcion){
                case 1 -> setNombreUsuario(nuevoValor);
                case 2 -> setPasswordUsuario(Encriptacion.hashearPassword(nuevoValor));
            }
    }
}
