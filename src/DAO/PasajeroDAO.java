package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Conexion.ConexionDB;
import Pasajeros.Pasajero;

public class PasajeroDAO {

    public void alta(Pasajero p) throws SQLException{
        String sql = "INSERT INTO pasajeros (dni,nombre,apellido,telefono,email) VALUES (?, ?, ?, ?, ?)";
        try(Connection con = ConexionDB.conectar();
        PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, p.getDni());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getEmail());
            ps.executeUpdate();
        }
    }

    public void baja(int id) throws SQLException{
        String sql = "DELETE FROM pasajeros WHERE id = ?";
        try (Connection con=ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void modificar(Pasajero p) throws SQLException{
        String sql="UPDATE pasajeros SET dni = ?, nombre = ?, apellido = ?, telefono = ?, email = ? WHERE id=?";
        try(Connection con = ConexionDB.conectar();PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1,p.getDni());
            ps.setString(2,p.getNombre());
            ps.setString(3,p.getApellido());
            ps.setString(4,p.getTelefono());
            ps.setString(5,p.getEmail());
            ps.setInt(6,p.getId());
        }
    }

    public Pasajero consulta(int id) throws SQLException{
        String sql = "SELECT * FROM pasajeros WHERE id = ?";
        try(Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return new Pasajero(rs.getInt("id"),rs.getString("dni"),rs.getString("nombre"),rs.getString("apellido"),rs.getString("telefono"),rs.getString("mail"));
                }
            }
        }
        return null;
    }

    public List<Pasajero> listar() throws SQLException{
        List<Pasajero> lista = new ArrayList<>();
        String sql ="SELECT * FROM pasajeros";
        try(Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs= ps.executeQuery()){
        while (rs.next()){
            lista.add(new Pasajero(rs.getInt("id"),rs.getString("dni"),rs.getString("nombre"),rs.getString("apellido"),rs.getString("telefono"),rs.getString("email")));
        }
        }
        return lista;
    }

}
