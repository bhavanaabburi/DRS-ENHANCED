/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.drs.drs.initial.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author abbur
 */
public class DatabaseConnectionTest {
    
    public static void main(String[] args) {

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            System.out.println(
                "SUCCESS: Connected to MySQL!"
            );

            System.out.println(
                "Database: " + connection.getCatalog()
            );

        } catch (SQLException e) {

            System.err.println(
                "ERROR: Database connection failed."
            );

            System.err.println(e.getMessage());
        }
    }
    
}
