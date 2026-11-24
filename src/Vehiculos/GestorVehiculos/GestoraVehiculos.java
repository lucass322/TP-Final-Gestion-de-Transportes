package Vehiculos.GestorVehiculos;

import Excepciones.VehiculoNoDisponibleExcepcion;
import Vehiculos.Vehiculo;
import DAO.VehiculoDAO;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;


public class GestoraVehiculos {
    private List<Vehiculo> listaVehiculos;
    private VehiculoDAO vehiculoDAO;

    public GestoraVehiculos(List<Vehiculo> listaVehiculos) {
        this.vehiculoDAO = new VehiculoDAO();
        this.listaVehiculos = listaVehiculos != null ? new ArrayList<>(listaVehiculos) : new ArrayList<>();
    }

    public GestoraVehiculos() throws SQLException{
        this.vehiculoDAO = new VehiculoDAO();
        this.listaVehiculos = new ArrayList<>();
    }

    public void altaVehiculo(Vehiculo v) throws SQLException {
        if (v == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo.");
        }
        vehiculoDAO.alta(v);
        listaVehiculos.add(v);

        System.out.println("Vehículo dado de alta exitosamente.");
    }


    public void bajaLogicaVehiculo(int id) throws VehiculoNoDisponibleExcepcion, SQLException {
        Vehiculo v = buscarVehiculoPorId(id);

        if (v == null || !v.isActivo()) {
            throw new VehiculoNoDisponibleExcepcion("El vehículo con ID " + id + " no está disponible o ya se encuentra inactivo.");
        }
        vehiculoDAO.baja(id);
        v.setActivo(false);

        System.out.println("Baja lógica realizada con éxito para el vehículo ID: " + id);
    }


    public void modificarVehiculo(Vehiculo vehiculoModificado) throws VehiculoNoDisponibleExcepcion, SQLException {
        if (vehiculoModificado == null || !vehiculoModificado.isActivo()) {
            throw new VehiculoNoDisponibleExcepcion("No se puede modificar el vehículo porque no está activo o disponible.");
        }

        for (int i = 0; i < listaVehiculos.size(); i++) {
            if (listaVehiculos.get(i).getId() == vehiculoModificado.getId()) {
                vehiculoDAO.modificar(vehiculoModificado);

                listaVehiculos.set(i, vehiculoModificado);

                System.out.println("Vehículo modificado correctamente.");

                return;
            }
        }

        throw new VehiculoNoDisponibleExcepcion("No se encontró el vehículo en la lista para modificar.");
    }


    public List<Vehiculo> listarVehiculos() {
        return new ArrayList<>(listaVehiculos);
    }


    private Vehiculo buscarVehiculoPorId(int id) {
        for (Vehiculo v : listaVehiculos) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }
}
