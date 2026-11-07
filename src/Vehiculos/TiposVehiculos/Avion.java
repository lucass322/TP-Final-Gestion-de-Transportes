package Vehiculos.TiposVehiculos;

import Vehiculos.Vehiculo;

public class Avion extends Vehiculo {
    private double autonomiaKM;
    private int cantTurbinas;

    public Avion(int id, String patente, int capacidadPasajeros, double costoPorKm, boolean activo, double autonomiaKM, int cantTurbinas) {
        super(id, patente, capacidadPasajeros, costoPorKm, activo);
        this.autonomiaKM = autonomiaKM;
        this.cantTurbinas = cantTurbinas;
    }

    public Avion() {

    }

    public double getAutonomiaKM() {
        return autonomiaKM;
    }

    public void setAutonomiaKM(double autonomiaKM) {
        this.autonomiaKM = autonomiaKM;
    }

    public int getCantTurbinas() {
        return cantTurbinas;
    }

    public void setCantTurbinas(int cantTurbinas) {
        this.cantTurbinas = cantTurbinas;
    }

    @Override
    public double calcularCostoViaje(double distancia) {
        return (distancia * 50.0) + (cantTurbinas * 1000.0);
    }

    @Override
    public void mostrarDetalles() {
        System.out.println("--- DATOS DEL AVIÓN ---");
        System.out.println("Patente: " + getPatente());
        System.out.println("Capacidad: " + getCapacidadPasajeros());
        System.out.println("Autonomía: " + autonomiaKM + " km");
        System.out.println("Turbinas: " + cantTurbinas);
        System.out.println("Costo por KM: " + getCostoPorKm());
        System.out.println("Estado activo: " + isActivo());
    }
}
