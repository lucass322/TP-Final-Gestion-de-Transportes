package Vehiculos.TiposVehiculos;

import Vehiculos.Vehiculo;

public class Colectivo extends Vehiculo {
    private int cantidadPisos;
    private boolean tieneBaño;

    public Colectivo(int id, String patente, int capacidadPasajeros, double costoPorKm, boolean activo, int cantidadPisos, boolean tieneBaño) {
        super(id, patente, capacidadPasajeros, costoPorKm, activo);
        this.cantidadPisos = cantidadPisos;
        this.tieneBaño = tieneBaño;
    }

    public Colectivo() {
    }

    public int getCantidadPisos() {
        return cantidadPisos;
    }

    public void setCantidadPisos(int cantidadPisos) {
        this.cantidadPisos = cantidadPisos;
    }

    public boolean isTieneBaño() {
        return tieneBaño;
    }

    public void setTieneBaño(boolean tieneBaño) {
        this.tieneBaño = tieneBaño;
    }

    @Override
    public double calcularCostoViaje(double distancia) {
        return distancia * getCostoPorKm();
    }

    @Override
    public void mostrarDetalles() {
        System.out.println("--- DATOS DEL COLECTIVO ---");
        System.out.println("Patente: " + getPatente());
        System.out.println("Capacidad: " + getCapacidadPasajeros());
        System.out.println("Capacidad De Pisos: " + cantidadPisos);
        System.out.println("¿Tiene Baño?: " + tieneBaño);
        System.out.println("Costo por km: $" + getCostoPorKm());
        System.out.println("Estado activo: " + isActivo());
    }
}
