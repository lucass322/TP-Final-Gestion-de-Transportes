package DAO;

import java.sql.SQLException;
import java.util.List;

public interface iDAO <T>{
    void alta(T t) throws SQLException;
    void baja(int id) throws SQLException;
    void modificar(T t) throws SQLException;
    T consultar(int id) throws SQLException;
    List<T> listar() throws SQLException;
}
