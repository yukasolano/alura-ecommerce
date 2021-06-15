package br.com.alura;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDatabase {
    private final LocalDatabase localDatabase;

    public OrderDatabase() throws SQLException {
        this.localDatabase = new LocalDatabase("orders_database");
        this.localDatabase.createTableIfNotExist("create table Orders (" +
                "uuid varchar(200) primary key)");
    }

    public boolean saveNew(Order order) throws SQLException {
        if (wasProcessed(order)) {
            return false;
        }
        localDatabase.update("insert into Orders (uuid) values (?)", order.getOrderId());
        return true;
    }

    private boolean wasProcessed(Order order) throws SQLException {
        ResultSet results = localDatabase.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }
}
