package Vehiculos.GestorVehiculos;
import Excepciones.VehiculoNoDisponibleExcepcion;
import Vehiculos.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class GestoraVehiculos {
    private List<Vehiculo> listaVehiculos;

    public GestoraVehiculos(List<Vehiculo> listaVehiculos) {
        this.listaVehiculos = new ArrayList<>();
    }

    public GestoraVehiculos() {
    }



    public void altaVehiculo(Vehiculo v) {
        if (v != null && !listaVehiculos.contains(v)) {
            listaVehiculos.add(v);
            System.out.println("Vehículo dado de alta exitosamente.");
        } else {
            System.out.println("El vehículo ya existe en el sistema o es inválido.");
        }
    }


    public void bajaLogicaVehiculo(int id) throws VehiculoNoDisponibleExcepcion {
        Vehiculo v = buscarVehiculoPorId(id);

        if (v == null || !v.isActivo()) {
            throw new VehiculoNoDisponibleExcepcion("El vehículo con ID " + id + " no está disponible o ya se encuentra inactivo.");
        }

        v.setActivo(false);
        System.out.println("Baja lógica realizada con éxito para el vehículo ID: " + id);
    }


    public void modificarVehiculo(Vehiculo vehiculoModificado) throws VehiculoNoDisponibleExcepcion {
        if (vehiculoModificado == null || !vehiculoModificado.isActivo()) {
            throw new VehiculoNoDisponibleExcepcion("No se puede modificar el vehículo porque no está activo o disponible.");
        }

        for (int i = 0; i < listaVehiculos.size(); i++) {
            if (listaVehiculos.get(i).getId() == vehiculoModificado.getId()) {
                listaVehiculos.set(i, vehiculoModificado);
                System.out.println("Vehículo modificado correctamente.");
                return;
            }
        }

        System.out.println("No se encontró el vehículo en la lista para modificar.");
    }


    public List<Vehiculo> listarVehiculos() {
        return listaVehiculos;
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
