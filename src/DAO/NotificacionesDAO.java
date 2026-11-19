package DAO;

import Configuracion.PreferenciasNotificaciones;
import Conexion.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificacionesDAO {

    //Busca las preferencias asociadas al idUsuario. Si todavía no existen, devuelve null.
    public PreferenciasNotificaciones consultarPreferencias(int idUsuario)
            throws SQLException {

        String sql = """
                SELECT idPreferencia, idUsuario,
                       notificarReservas,
                       notificarCancelaciones,
                       notificarCambiosViajes
                FROM preferencias_notificaciones
                WHERE idUsuario = ?
                """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    PreferenciasNotificaciones preferencias =
                            new PreferenciasNotificaciones();

                    preferencias.setIdPreferencia(rs.getInt("idPreferencia"));
                    preferencias.setIdUsuario(rs.getInt("idUsuario"));
                    preferencias.setNotificarReservas(rs.getBoolean("notificarReservas"));
                    preferencias.setNotificarCancelaciones(rs.getBoolean("notificarCancelaciones"));
                    preferencias.setNotificarCambiosViajes(rs.getBoolean("notificarCambiosViajes"));

                    return preferencias;
                }
            }
        }

        return null;
    }

    //Actualiza las tres preferencias del usuario indicado.
    public void guardarPreferencias(PreferenciasNotificaciones preferencias)
            throws SQLException {

        String sql = """
            UPDATE preferencias_notificaciones
            SET notificarReservas = ?,
                notificarCancelaciones = ?,
                notificarCambiosViajes = ?
            WHERE idUsuario = ?
            """;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, preferencias.isNotificarReservas());
            ps.setBoolean(2, preferencias.isNotificarCancelaciones());
            ps.setBoolean(3, preferencias.isNotificarCambiosViajes());
            ps.setInt(4, preferencias.getIdUsuario());

            ps.executeUpdate();
        }
    }
}