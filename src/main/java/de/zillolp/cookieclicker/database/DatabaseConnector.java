package de.zillolp.cookieclicker.database;

import de.zillolp.cookieclicker.CookieClicker;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseConnector {
    public boolean disabled;
    private final boolean useMysql;
    private final String url;
    private final String username;
    private final String password;
    private final String driver;
    private Connection connection = null;

    public DatabaseConnector(boolean useMysql, String filename, String address, String port, String databaseName, String username, String password) {
        disabled = false;
        this.useMysql = useMysql;
        this.username = username;
        this.password = password;
        if (useMysql) {
            url = "jdbc:mysql://" + address + ":" + port + "/" + databaseName;
            driver = ("com.mysql.jdbc.Driver");
        } else {
            File databaseFile = new File(CookieClicker.cookieClicker.getDataFolder(), filename + ".db");
            if (!databaseFile.exists()) {
                try {
                    databaseFile.createNewFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            driver = ("org.sqlite.JDBC");
        }
    }

    public Connection open() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (SQLException exception) {
            System.out.println("[CookieClicker] Could not connect to MySQL/SQLite server! Error: " + exception.getMessage());
        } catch (ClassNotFoundException exception) {
            System.out.println("[CookieClicker] JDBC Driver not found!");
        }
        return connection;
    }

    public void update(String qre) {
        if (disabled) {
            updateStatement(qre);
            return;
        }
        CompletableFuture.runAsync(() -> {
            updateStatement(qre);
        });
    }

    private void updateStatement(String qre) {
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate(qre);
                statement.close();
            } catch (SQLException exception) {
                System.err.print(exception);
            }
        }
    }

    public ResultSet query(String qre) {
        if (connection != null) {
            ResultSet resultSet = null;
            try {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(qre);
            } catch (SQLException exception) {
                System.err.print(exception);
            }
            return resultSet;
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean checkConnection() {
        return connection != null;
    }
}
