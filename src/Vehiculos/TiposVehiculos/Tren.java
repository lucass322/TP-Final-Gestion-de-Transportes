package Vehiculos.TiposVehiculos;

import Vehiculos.Vehiculo;

public class Tren extends Vehiculo {
    private int cantVagones;
    private String tipoTraccion;

    public Tren(int id, String patente, int capacidadPasajeros, double costoPorKm, boolean activo, int cantVagones, String tipoTraccion) {
        super(id, patente, capacidadPasajeros, costoPorKm, activo);
        this.cantVagones = cantVagones;
        this.tipoTraccion = tipoTraccion;
    }

    public Tren() {
    }

    public int getCantVagones() {
        return cantVagones;
    }

    public void setCantVagones(int cantVagones) {
        this.cantVagones = cantVagones;
    }

    public String getTipoTraccion() {
        return tipoTraccion;
    }

    public void setTipoTraccion(String tipoTraccion) {
        tipoTraccion = tipoTraccion;
    }

    @Override
    public void mostrarDetalles() {
        System.out.println("--- DATOS DEL TREN ---");
        System.out.println("Patente: " + getPatente());
        System.out.println("Capacidad de pasajeros: " + getCapacidadPasajeros());
        System.out.println("Cantidad de vagones: " + cantVagones);
        System.out.println("Tipo de tracción: " + tipoTraccion);
        System.out.println("Costo por km: $" + getCostoPorKm());
        System.out.println("Estado activo: " + isActivo());
    }

    @Override
    public double calcularCostoViaje(double distancia) {
        return distancia * getCostoPorKm();
    }
}
