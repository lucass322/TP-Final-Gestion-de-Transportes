package Viajes;

public class Recorrido {
    private int idRecorrido;
    private String origen;
    private String destino;
    private double distanciaKM;
    private boolean habilitadoRecorrido = true;

    public Recorrido(int idRecorrido, String origen, String destino, double distanciaKM) {
        this.idRecorrido = idRecorrido;
        this.origen = origen;
        this.destino = destino;
        this.distanciaKM = distanciaKM;
    }

    public Recorrido() {
    }

    public int getIdRecorrido() {
        return idRecorrido;
    }

    public void setIdRecorrido(int idRecorrido) {
        this.idRecorrido = idRecorrido;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getDistanciaKM() {
        return distanciaKM;
    }

    public void setDistanciaKM(double distanciaKM) {
        this.distanciaKM = distanciaKM;
    }

    public boolean isHabilitadoRecorrido() {
        return habilitadoRecorrido;
    }

    public void setHabilitadoRecorrido(boolean habilitadoRecorrido) {
        this.habilitadoRecorrido = habilitadoRecorrido;
    }

    @Override
    public String toString() {
        return origen + " → " + destino + " (" + distanciaKM + " km)";
    }
}
