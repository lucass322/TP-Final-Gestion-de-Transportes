package Vehiculos;

public abstract class Vehiculo {
    private int id;
    private String patente;
    private int capacidadPasajeros;
    private double costoPorKm;
    private boolean activo;

    public Vehiculo(int id, String patente, int capacidadPasajeros, double costoPorKm, boolean activo) {
        this.id = id;
        this.patente = patente;
        this.capacidadPasajeros = capacidadPasajeros;
        this.costoPorKm = costoPorKm;
        this.activo = true;
    }

    public Vehiculo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public int getCapacidadPasajeros() {
        return capacidadPasajeros;
    }

    public void setCapacidadPasajeros(int capacidadPasajeros) {
        this.capacidadPasajeros = capacidadPasajeros;
    }

    public double getCostoPorKm() {
        return costoPorKm;
    }

    public void setCostoPorKm(double costoPorKm) {
        this.costoPorKm = costoPorKm;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }


    public abstract void mostrarDetalles();


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return id == vehiculo.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public abstract double calcularCostoViaje (double distancia);

    public void darBajaLogica (){
        activo = false;
    }

    public void actualizarDatos(String nuevaPatente, int nuevaCapacidadPasajeros) {
        patente = nuevaPatente;
        capacidadPasajeros = nuevaCapacidadPasajeros;
    }


}
