package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnection {
    public static Connection getDBConnection() {
        Connection con = null;
        ResourceBundle resourceBundle = ResourceBundle.getBundle("properties.jdbc");

        try {
            Class.forName(resourceBundle.getString("jdbc.className"));
            String connectionUrl = resourceBundle.getString("jdbc.connectionUrl");
            String user = resourceBundle.getString("jdbc.login");
            String password = resourceBundle.getString("jdbc.password");
            con = DriverManager.getConnection(connectionUrl, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }
}
