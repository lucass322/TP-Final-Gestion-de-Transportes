package Usuarios;

import DAO.UsuarioDAO;
import Personas.Persona;
import Roles.Rol;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Administrador extends Usuario {

    int idAdmin;
    UsuarioDAO dao = new UsuarioDAO();

    Scanner sc = new Scanner(System.in);

    public void altaUsuario(int idUsuario) throws SQLException {

        Usuario usuarioConsultado = dao.consultar(idUsuario);
        if(usuarioConsultado.isHabilitadoUsuario()){
            System.out.println("Este usuario ya esta habilitado.");
        }   else usuarioConsultado.setHabilitadoUsuario(true); dao.modificar(usuarioConsultado);
    }

    public void bajaUsuario(int idUsuario) throws SQLException{

        Usuario usuarioConsultado = dao.consultar(idUsuario);
        if(!usuarioConsultado.isHabilitadoUsuario()){
            System.out.println("Este usuario ya esta deshabilitado.");
        }   else usuarioConsultado.setHabilitadoUsuario(false); dao.modificar(usuarioConsultado);
    }

    public Usuario editarUsuario(int idUsuario) throws SQLException{
        Usuario usuarioEditado = dao.consultar(idUsuario);

        if (usuarioEditado == null) {
            System.out.println("No existe un usuario con ese ID.");
            return null;
        }

        System.out.println("-| !!! Dejar vacio para no alterar el dato. !!! |-");

        System.out.print("Ingrese nuevo nombre de usuario:");
        String nombre = sc.nextLine();

        if (!nombre.isBlank()) {
            usuarioEditado.setNombreUsuario(nombre);
        }

        System.out.print("Ingrese nueva password a continuacion:");
        String password = sc.nextLine();
        if (!password.isBlank()) {
            usuarioEditado.setPasswordUsuario(password);
        }

        return usuarioEditado;
    }

    public Map<Integer,Usuario> listarUsuarios() throws SQLException{
        Map<Integer,Usuario> mapaUsuarios = new HashMap<>();

        mapaUsuarios=dao.listarMap();

        return mapaUsuarios;
    }

    public Administrador() {
    }

    public Administrador(Rol rol, String nombreUsuario, String passwordUsuario, int idAdmin) {
        super(rol, nombreUsuario, passwordUsuario);
        this.idAdmin = idAdmin;
    }
    public Administrador(Persona persona, Rol rol, String nombreUsuario, String passwordUsuario, int idAdmin) {
        super(persona, rol, nombreUsuario, passwordUsuario);
        this.idAdmin = idAdmin;
    }
    public Administrador(int idUsuario, Persona persona, Rol rol, String nombreUsuario, String passwordUsuario, boolean habilitadoUsuario, int idAdmin) {
        super(idUsuario, persona, rol, nombreUsuario, passwordUsuario, habilitadoUsuario);
        this.idAdmin = idAdmin;
    }

    public int getIdAdmin() {
        return idAdmin;
    }
    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }
}
