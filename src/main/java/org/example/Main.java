package org.example;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Main {

    private static final String SQL_INSERT = "INSERT INTO ramusagelogs(ram_usage, pc_name) VALUES (?, ?)";

    public static void main(String[] args) {
        Properties properties = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        String dbUrl = properties.getProperty("db.url");
        String dbUser = properties.getProperty("db.user");
        String dbPassword = properties.getProperty("db.password");

        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        String computerName = System.getenv("COMPUTERNAME");


        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            //infinite loop checking ram usage every 30 seconds
            while (true) {
                long totalMemory = memory.getTotal();
                long availableMemory = memory.getAvailable();
                long usedMemory = (totalMemory - availableMemory) / (1024 * 1024);

                sendDataToDatabase(connection, usedMemory, computerName);

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendDataToDatabase(Connection connection, long ramUsage, String pcName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setLong(1, ramUsage);
            preparedStatement.setString(2, pcName);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}