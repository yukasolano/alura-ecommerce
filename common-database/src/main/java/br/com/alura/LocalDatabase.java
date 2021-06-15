package br.com.alura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalDatabase {

    private Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        connection = DriverManager.getConnection(url);
    }

    public void createTableIfNotExist(String sql) {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet query(String sql,
                           String... params) throws SQLException {
        return prepare(sql, params).executeQuery();
    }

    private PreparedStatement prepare(String sql,
                                      String[] params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }

    public boolean update(String sql,
                          String... params) throws SQLException {
        return prepare(sql, params).execute();
    }
}
