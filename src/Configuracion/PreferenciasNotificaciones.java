package Configuracion;

public class PreferenciasNotificaciones {

    private int idPreferencia;
    private int idUsuario;
    private boolean notificarReservas;
    private boolean notificarCancelaciones;
    private boolean notificarCambiosViajes;

    public PreferenciasNotificaciones() {
    }

    public PreferenciasNotificaciones(int idUsuario,
                                      boolean notificarReservas,
                                      boolean notificarCancelaciones,
                                      boolean notificarCambiosViajes) {
        this.idUsuario = idUsuario;
        this.notificarReservas = notificarReservas;
        this.notificarCancelaciones = notificarCancelaciones;
        this.notificarCambiosViajes = notificarCambiosViajes;
    }

    public int getIdPreferencia() {
        return idPreferencia;
    }

    public void setIdPreferencia(int idPreferencia) {
        this.idPreferencia = idPreferencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isNotificarReservas() {
        return notificarReservas;
    }

    public void setNotificarReservas(boolean notificarReservas) {
        this.notificarReservas = notificarReservas;
    }

    public boolean isNotificarCancelaciones() {
        return notificarCancelaciones;
    }

    public void setNotificarCancelaciones(boolean notificarCancelaciones) {
        this.notificarCancelaciones = notificarCancelaciones;
    }

    public boolean isNotificarCambiosViajes() {
        return notificarCambiosViajes;
    }

    public void setNotificarCambiosViajes(boolean notificarCambiosViajes) {
        this.notificarCambiosViajes = notificarCambiosViajes;
    }
}
