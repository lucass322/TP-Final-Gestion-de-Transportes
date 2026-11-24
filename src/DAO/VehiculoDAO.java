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


    public void alta(Vehiculo v) throws SQLException {

        if (v == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }

        String sqlVehiculo = " INSERT INTO vehiculos (patente, capacidadPasajeros, costoPorKm, activo) VALUES (?, ?, ?, ?)";



        String sqlAvion = "INSERT INTO aviones (idVehiculo, autonomiaKM, cantTurbinas) VALUES (?, ?, ?)";

        String sqlColectivo = " INSERT INTO colectivos (idVehiculo, cantidadPisos, tieneBaño) VALUES (?, ?, ?) ";

        String sqlTren = " INSERT INTO trenes (idVehiculo, cantVagones, tipoTraccion) VALUES (?, ?, ?) ";

        try (Connection con = ConexionDB.conectar()) {

            con.setAutoCommit(false);

            try {
                int idGenerado;

                try (PreparedStatement ps = con.prepareStatement(
                        sqlVehiculo, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, v.getPatente());
                    ps.setInt(2, v.getCapacidadPasajeros());
                    ps.setDouble(3, v.getCostoPorKm());
                    ps.setBoolean(4, v.isActivo());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se pudo obtener el ID del vehículo.");
                        }

                        idGenerado = rs.getInt(1);
                    }
                }

                if (v instanceof Avion a) {

                    try (PreparedStatement ps = con.prepareStatement(sqlAvion)) {
                        ps.setInt(1, idGenerado);
                        ps.setDouble(2, a.getAutonomiaKM());
                        ps.setInt(3, a.getCantTurbinas());
                        ps.executeUpdate();
                    }

                } else if (v instanceof Colectivo c) {

                    try (PreparedStatement ps = con.prepareStatement(sqlColectivo)) {
                        ps.setInt(1, idGenerado);
                        ps.setInt(2, c.getCantidadPisos());
                        ps.setBoolean(3, c.isTieneBaño());
                        ps.executeUpdate();
                    }

                } else if (v instanceof Tren t) {

                    try (PreparedStatement ps = con.prepareStatement(sqlTren)) {
                        ps.setInt(1, idGenerado);
                        ps.setInt(2, t.getCantVagones());
                        ps.setString(3, t.getTipoTraccion());
                        ps.executeUpdate();
                    }

                } else {
                    throw new SQLException("Tipo de vehículo desconocido.");
                }

                con.commit();

                v.setId(idGenerado);

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }


    public void baja(int id) throws SQLException{
        String sql = "UPDATE vehiculos SET activo = false WHERE idVehiculo = ? AND activo = true";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el vehículo o ya se encuentra inactivo.");
            }
        }
    }

    public void habilitar(int id) throws SQLException {

        String sql = """
            UPDATE vehiculos
            SET activo = TRUE
            WHERE idVehiculo = ?
            AND activo = FALSE
            """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el vehículo o ya se encuentra activo.");
            }
        }
    }

    public void modificar(Vehiculo v) throws SQLException {

        if (v == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }

        String sqlVehiculo = "UPDATE vehiculos SET patente = ?, capacidadPasajeros = ?, costoPorKm = ? WHERE idVehiculo = ? AND activo = true";

        String sqlAvion = """
            UPDATE aviones
            SET autonomiaKM = ?, cantTurbinas = ?
            WHERE idVehiculo = ?
            """;

        String sqlColectivo = """
            UPDATE colectivos
            SET cantidadPisos = ?, tieneBaño = ?
            WHERE idVehiculo = ?
            """;

        String sqlTren = """
            UPDATE trenes
            SET cantVagones = ?, tipoTraccion = ?
            WHERE idVehiculo = ?
            """;

        try (Connection con = ConexionDB.conectar()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(sqlVehiculo)) {
                    ps.setString(1, v.getPatente());
                    ps.setInt(2, v.getCapacidadPasajeros());
                    ps.setDouble(3, v.getCostoPorKm());
                    ps.setInt(4, v.getId());

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "No se encontró el vehículo o está inactivo."
                        );
                    }
                }

                if (v instanceof Avion a) {

                    try (PreparedStatement ps = con.prepareStatement(sqlAvion)) {
                        ps.setDouble(1, a.getAutonomiaKM());
                        ps.setInt(2, a.getCantTurbinas());
                        ps.setInt(3, v.getId());

                        if (ps.executeUpdate() == 0) {
                            throw new SQLException("No se encontraron los datos del avión.");
                        }
                    }

                } else if (v instanceof Colectivo c) {

                    try (PreparedStatement ps = con.prepareStatement(sqlColectivo)) {
                        ps.setInt(1, c.getCantidadPisos());
                        ps.setBoolean(2, c.isTieneBaño());
                        ps.setInt(3, v.getId());

                        if (ps.executeUpdate() == 0) {
                            throw new SQLException("No se encontraron los datos del colectivo.");
                        }
                    }

                } else if (v instanceof Tren t) {

                    try (PreparedStatement ps = con.prepareStatement(sqlTren)) {
                        ps.setInt(1, t.getCantVagones());
                        ps.setString(2, t.getTipoTraccion());
                        ps.setInt(3, v.getId());

                        if (ps.executeUpdate() == 0) {
                            throw new SQLException("No se encontraron los datos del tren.");
                        }
                    }

                } else {
                    throw new SQLException("Tipo de vehículo desconocido.");
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }


    public Vehiculo consultar(int id) throws SQLException {

        String sql = "SELECT v.idVehiculo, v.patente, v.capacidadPasajeros, v.costoPorKm, v.activo, a.idVehiculo AS idAvion, a.autonomiaKM, a.cantTurbinas, c.idVehiculo AS idColectivo, c.cantidadPisos, c.tieneBaño, t.idVehiculo AS idTren, t.cantVagones, t.tipoTraccion FROM vehiculos v LEFT JOIN aviones a ON v.idVehiculo = a.idVehiculo LEFT JOIN colectivos c ON v.idVehiculo = c.idVehiculo LEFT JOIN trenes t ON v.idVehiculo = t.idVehiculo WHERE v.idVehiculo = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }

    public List<Vehiculo> listar() throws SQLException {

        List<Vehiculo> lista = new ArrayList<>();

        String sql = " SELECT v.idVehiculo, v.patente, v.capacidadPasajeros, v.costoPorKm, v.activo, a.idVehiculo AS idAvion, a.autonomiaKM, a.cantTurbinas, c.idVehiculo AS idColectivo, c.cantidadPisos, c.tieneBaño, t.idVehiculo AS idTren, t.cantVagones, t.tipoTraccion FROM vehiculos v LEFT JOIN aviones a ON v.idVehiculo = a.idVehiculo LEFT JOIN colectivos c ON v.idVehiculo = c.idVehiculo LEFT JOIN trenes t ON v.idVehiculo = t.idVehiculo ";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }



    private Vehiculo mapear(ResultSet rs) throws SQLException {

        int id = rs.getInt("idVehiculo");
        String patente = rs.getString("patente");
        int capacidadPasajeros = rs.getInt("capacidadPasajeros");
        double costoPorKm = rs.getDouble("costoPorKm");
        boolean activo = rs.getBoolean("activo");

        if (rs.getObject("idAvion") != null) {
            return new Avion(
                    id, patente, capacidadPasajeros, costoPorKm, activo,
                    rs.getDouble("autonomiaKM"),
                    rs.getInt("cantTurbinas")
            );

        } else if (rs.getObject("idColectivo") != null) {
            return new Colectivo(
                    id, patente, capacidadPasajeros, costoPorKm, activo,
                    rs.getInt("cantidadPisos"),
                    rs.getBoolean("tieneBaño")
            );

        } else if (rs.getObject("idTren") != null) {
            return new Tren(
                    id, patente, capacidadPasajeros, costoPorKm, activo,
                    rs.getInt("cantVagones"),
                    rs.getString("tipoTraccion")
            );
        }

        throw new SQLException("Tipo de vehículo desconocido.");
    }


}
