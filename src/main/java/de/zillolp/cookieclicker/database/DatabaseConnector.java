package de.zillolp.cookieclicker.database;

import de.zillolp.cookieclicker.CookieClicker;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    private final String url;
    private final String driver;
    private final Properties properties;
    private Connection connection;

    public DatabaseConnector(CookieClicker plugin, boolean useMySQL, String filename, String address, String port, String databaseName, String username, String password) {
        this.properties = new Properties();
        this.properties.put("user", username);
        this.properties.put("password", password);
        this.properties.put("autoReconnect", true);
        if (useMySQL) {
            this.driver = ("com.mysql.jdbc.Driver");
            this.url = "jdbc:mysql://" + address + ":" + port + "/" + databaseName;
            return;
        }
        File databaseFile = new File(plugin.getDataFolder(), filename + ".db");
        if (!(databaseFile.exists())) {
            try {
                databaseFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        this.driver = ("org.sqlite.JDBC");
        this.url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
    }

    public void open() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, properties);
            connection.setAutoCommit(true);
        } catch (SQLException exception) {
            System.out.println("[CookieClicker] Could not connect to Database!");
        } catch (ClassNotFoundException exception) {
            System.out.println("[CookieClicker] JDBC Driver not found!");
            System.out.println("[CookieClicker] Could not connect to Database! Error: ");
            System.out.println(exception.getMessage());
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public boolean checkConnection() {
        if (connection == null) {
            return false;
        }
        try {
            if (connection.isValid(0)) {
                return true;
            }
            close();
            open();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return connection != null;
    }

    public void updateStatement(PreparedStatement preparedStatement) {
        if (!(checkConnection())) {
            return;
        }
        try {
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public PreparedStatement prepareStatement(String sql) {
        if (!(checkConnection())) {
            return null;
        }
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
