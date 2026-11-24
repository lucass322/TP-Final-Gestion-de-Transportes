package Usuarios;
import DAO.UsuarioDAO;
import Utilidades.Encriptacion;

import java.sql.SQLException;
import java.util.Scanner;

public class ClientePasajero extends Usuario{
    private int idCliente;
    private Scanner sc = new Scanner(System.in);
    UsuarioDAO dao = new UsuarioDAO();


    public String verPerfil (){
            return super.toString() + "\n| ID Cliente:" + idCliente;
    }

    public boolean editarPerfil() throws SQLException {
        boolean cambio = false;
        String nuevo;
        System.out.println("Dejar en blanco en caso de no querer cambiar");

        System.out.println("Nombre de usuario actual: " + getNombreUsuario());
        System.out.println("Ingrese nuevo nombre de usuario:");
        nuevo = sc.nextLine();
        if(!nuevo.isBlank()){
            while(dao.existeNombreUsuario(nuevo)){
                System.out.println("Nombre de usuario ya en uso.");
                nuevo = sc.nextLine();
            }
            this.setNombreUsuario(nuevo);
            cambio=true;
        }

        System.out.println("Nueva contraseña:");
        nuevo = sc.nextLine();
        if(!nuevo.isBlank()){
            this.setPasswordUsuario(Encriptacion.hashearPassword(nuevo));
            cambio=true;
        }

        return cambio;
    }
}
