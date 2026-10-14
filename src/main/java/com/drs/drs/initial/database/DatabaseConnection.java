
package com.drs.drs.initial.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/drs_enhanced"
            + "?createDatabaseIfNotExist=true";

    private static final String USER = "root";

    private static final String PASSWORD = "pass";

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL, USER, PASSWORD
        );
    }
}
