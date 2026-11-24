package Viajes.GestorViajes;

import DAO.ViajeDAO;
import Viajes.Viaje;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestoraViajes {

    private List<Viaje> listaViajes;
    private ViajeDAO viajeDAO;

    public GestoraViajes(List<Viaje> listaViajes) {
        this.viajeDAO = new ViajeDAO();
        this.listaViajes = listaViajes != null ? new ArrayList<>(listaViajes) : new ArrayList<>();
    }

    public GestoraViajes() throws SQLException {
        this.viajeDAO = new ViajeDAO();
        this.listaViajes = new ArrayList<>(viajeDAO.listar());
    }

    // ALTA
    public void altaViaje(Viaje viaje) throws SQLException {
        if (viaje == null) {
            throw new IllegalArgumentException("El viaje no puede ser nulo.");
        }

        //Primero se guarda en la DB.
        viajeDAO.alta(viaje);

        //Después se agrega a la lista.
        listaViajes.add(viaje);

        System.out.println("Viaje registrado correctamente.");
    }

    // BAJA LÓGICA
    public void bajaLogicaViaje(int id) throws SQLException {

        Viaje viaje = buscarViajePorId(id);

        if (viaje == null || !viaje.isHabilitadoViaje()) {
            throw new SQLException("El viaje no existe o ya se encuentra deshabilitado.");
        }

        // Primero se actualiza en la DB.
        viajeDAO.baja(id);

        // Después se actualiza en memoria
        viaje.setHabilitadoViaje(false);

        System.out.println("Baja lógica realizada correctamente. ID: " + id);
    }

    // HABILITAR
    public void habilitarViaje(int id) throws SQLException {

        Viaje viaje = buscarViajePorId(id);

        if (viaje == null || viaje.isHabilitadoViaje()) {
            throw new SQLException("El viaje no existe o ya se encuentra habilitado."
            );
        }

        if (viaje.getRecorrido() == null || !viaje.getRecorrido().isHabilitadoRecorrido()) {

            throw new SQLException("No se puede habilitar el viaje porque su recorrido está deshabilitado.");
        }

        if (viaje.getVehiculoAsignado() == null || !viaje.getVehiculoAsignado().isActivo()) {

            throw new SQLException("No se puede habilitar el viaje porque su vehículo está inactivo.");
        }

        // Primero se actualiza en MySQL.
        viajeDAO.habilitar(id);

        // Después se actualiza en memoria.
        viaje.setHabilitadoViaje(true);

        System.out.println("Viaje habilitado correctamente. ID: " + id);
    }

    // MODIFICAR
    public void modificarViaje(Viaje viajeModificado) throws SQLException {

        if (viajeModificado == null) {
            throw new IllegalArgumentException("El viaje no puede ser nulo.");
        }

        if (!viajeModificado.isHabilitadoViaje()) {
            throw new SQLException("No se puede modificar un viaje deshabilitado.");
        }

        for (int i = 0; i < listaViajes.size(); i++) {

            if (listaViajes.get(i).getIdViaje() == viajeModificado.getIdViaje()) {

                // Primero se actualiza en la DB.
                viajeDAO.modificar(viajeModificado);

                // Después se actualiza en memoria.
                listaViajes.set(i, viajeModificado);

                System.out.println("Viaje modificado correctamente.");

                return;
            }
        }

        throw new SQLException("No se encontró el viaje en la lista para modificar.");
    }

    // LISTAR
    public List<Viaje> listarViajes() {
        return new ArrayList<>(listaViajes);
    }

    // BUSCAR POR ID
    private Viaje buscarViajePorId(int id) {
        for (Viaje viaje : listaViajes) {

            if (viaje.getIdViaje() == id) {
                return viaje;
            }
        }

        return null;
    }
}
