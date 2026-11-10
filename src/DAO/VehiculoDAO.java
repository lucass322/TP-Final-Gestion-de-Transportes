package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Conexion.ConexionDB;
import Vehiculos.TiposVehiculos.Avion;
import Vehiculos.TiposVehiculos.Colectivo;
import Vehiculos.TiposVehiculos.Tren;
import Vehiculos.Vehiculo;

public class VehiculoDAO implements iDAO<Vehiculo> {

    public void alta(Vehiculo v) throws SQLException{
        String sql = "INSERT INTO vehiculos (tipo,id,patente,capacidadPasajeros,costoPorKm,activo,cantTurbinas,autonomiaKM,cantPisos,tieneBanio,cantVagones,tipoTraccion) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps= con.prepareStatement(sql)){
            ps.setInt(2,v.getId());
            ps.setString(3, v.getPatente());
            ps.setInt(4,v.getCapacidadPasajeros());
            ps.setDouble(5,v.getCostoPorKm());
            ps.setBoolean(6,v.isActivo());
            if(v instanceof Avion a){
            ps.setString(1,"AVION");
            ps.setInt(7,a.getCantTurbinas());
            ps.setDouble(8,a.getAutonomiaKM());
            ps.setNull(9, Types.INTEGER);
            ps.setNull(10, Types.BOOLEAN);
            ps.setNull(11, Types.INTEGER);
            ps.setNull(12, Types.VARCHAR);
            }   else if(v instanceof Colectivo c){
                ps.setString(1,"COLECTIVO");
                ps.setInt(9,c.getCantidadPisos());
                ps.setBoolean(10,c.isTieneBaño());
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.DOUBLE);
                ps.setNull(11, Types.INTEGER);
                ps.setNull(12, Types.VARCHAR);
            }   else if(v instanceof Tren t){
                ps.setString(1,"TREN");
                ps.setInt(11,t.getCantVagones());
                ps.setString(12,t.getTipoTraccion());
                ps.setNull(9, Types.INTEGER);
                ps.setNull(10, Types.BOOLEAN);
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.DOUBLE);
            }

            ps.executeUpdate();
        }
    }

    public void baja(int id) throws SQLException{
        String sql = "DELETE FROM vehiculos WHERE id = ?";
        try(Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1,id);
            ps.executeUpdate();
        }
    }

    public void modificar(Vehiculo v) throws SQLException{
        String sql = "UPDATE vehiculos patente = ?, capacidadPasajeros = ?, costoPorKm = ?, activo=?, autonomiaKm=?, cantTurbinas=?, cantidadPisos=?, tieneBanio=?,cantVagones=?,tipoTraccion=? WHERE id = ?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1,v.getPatente());
            ps.setInt(2,v.getCapacidadPasajeros());
            ps.setDouble(3,v.getCostoPorKm());
            ps.setBoolean(4,v.isActivo());

            ps.setDouble(5,Types.DOUBLE);
            ps.setInt(6,Types.INTEGER);
            ps.setNull(7,Types.INTEGER);
            ps.setNull(8,Types.BOOLEAN);
            ps.setNull(9,Types.INTEGER);
            ps.setNull(10,Types.VARCHAR);
            if(v instanceof Avion a){
                ps.setDouble(5,a.getAutonomiaKM());
                ps.setInt(6,a.getCantTurbinas());
            }   else if (v instanceof Colectivo c){
                ps.setInt(7, c.getCantidadPisos());
                ps.setBoolean(8,c.isTieneBaño());
            }   else if (v instanceof Tren t){
                ps.setInt(9,t.getCantVagones());
                ps.setString(10, t.getTipoTraccion());
            }
        }
    }

    public Vehiculo consultar(int id) throws SQLException{
        String sql="SELECT * FROM vehiculos WHERE id=?";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql);){
            ps.setInt(1,id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return mapear(rs);
                }
            }
        }
    return null;
    }

    public List<Vehiculo> listar() throws SQLException{
        List<Vehiculo> lista = new ArrayList<>();
        String sql="SELECT * FROM vehiculos";
        try (Connection con = ConexionDB.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs=ps.executeQuery()){
            while (rs.next()){
                lista.add(mapear(rs));
            }
        }
    return lista;
    }



    private Vehiculo mapear(ResultSet rs) throws SQLException{
        int id=rs.getInt("id");
        String patente=rs.getString("patente");
        int capacidadPasajeros=rs.getInt("capacidadPasajeros");
        double costoPorKm=rs.getDouble("costoPorKm");
        boolean activo=rs.getBoolean("activo");

        return switch (rs.getString("tipo")) {
            case "AVION" ->
                    new Avion(id, patente, capacidadPasajeros, costoPorKm, activo, rs.getDouble("autonomiaKM"), rs.getInt("cantTurbinas"));
            case "COLECTIVO" ->
                    new Colectivo(id, patente, capacidadPasajeros, costoPorKm, activo, rs.getInt("cantidadPisos"), rs.getBoolean("tieneBanio"));
            case "TREN" ->
                    new Tren(id, patente, capacidadPasajeros, costoPorKm, activo, rs.getInt("cantVagones"), rs.getString("tipoTraccion"));

            default -> throw new SQLException("Tipo de vehiculo desconocido.");
        };
    }


}
