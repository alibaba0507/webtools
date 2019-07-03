/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 *
 * @author alibaba0507
 */
public class SQLiteTest {

    public static void main(String args[]) {

        Connection c = null;

        Statement stmt = null;

        try {

            Class.forName("org.sqlite.JDBC");

            c = DriverManager.getConnection("jdbc:sqlite:SqliteJavaDB.db");

            System.out.println("Database Opened...\n");

            stmt = c.createStatement();

           /*
            String sql = "CREATE TABLE Product "
                    + "(p_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " p_name TEXT NOT NULL, "
                    + " price REAL NOT NULL, "
                    + " quantity INTEGER) ";
            */
           
            String sql = "CREATE TABLE queries "
                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " name TEXT NOT NULL, "
                    + " search_engine TEXT NOT NULL); ";
           
            stmt.executeUpdate(sql);

            stmt.close();

            c.close();

        } catch (Exception e) {

            System.err.println(e.getClass().getName() + ": " + e.getMessage());

            System.exit(0);

        }

        System.out.println("Table Product Created Successfully!!!");

    }
}
