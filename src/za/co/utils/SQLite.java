/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author alibaba0507
 */
public class SQLite {

    private Connection con;

    public SQLite() {
        createDb();
        createTables();
    }

    public void deleteSearch(int id) {
        try {
            String sql = "DELETE FROM search WHERE id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int findSearch(int qId, String url) {
        try {
            URL u = new URL(url);
            String protocol = u.getProtocol();
            String host = u.getHost();
            int port = u.getPort();
            String path = u.getPath();
            if (port > 0) {
                host += ":" + port;
            }
            Statement state = con.createStatement();
            String sql = "SELECT id FROM search WHERE "
                    + "q_id=? AND dom=? AND url=? ";
            PreparedStatement stm = con.prepareStatement(sql);
            //ResultSet res = state.executeQuery("SELECT id FROM search WHERE "
             //       + "q_id=? AND dom=? AND url=? ");
             stm.setInt(1, qId);
             stm.setString(2, host);
             stm.setString(3, path);
             ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            rs.close();
            state.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int saveSearch(int qId, String url, int level, int topId, int page) {
        int generatedKey = 0;
        try {

            generatedKey = findSearch(qId, url);
            if (generatedKey > 0) {
                return generatedKey;
            }
            // int id = findQueryId(projecName, query, searchEngine);
            // if (id == -1) {
            PreparedStatement prep = con.prepareStatement("insert into search values(?,?,?,?,?,?,?,?);");
            URL u = new URL(url);
            String protocol = u.getProtocol();
            String host = u.getHost();
            int port = u.getPort();
            String path = u.getPath();
            if (port > 0) {
                host += ":" + port;
            }
            prep.setString(2, protocol);
            prep.setString(3, host);
            prep.setString(4, path);
            prep.setInt(3, level);
            prep.setInt(4, topId);
            prep.setInt(5, page);
            prep.setInt(6, qId);
            //prep.setInt(6, topId);
            prep.execute();
            ResultSet rs = prep.getGeneratedKeys();

            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
            prep.close();
            rs.close();
            //return generatedKey;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    public int findQueryId(String query, String searchEngine) {
        try {
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT id FROM queries WHERE name='" + query
                    + "' AND search_engine='" + searchEngine
                    + "'");
            if (res.next()) {
                return res.getInt("id");
            }
            state.close();
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void deleteQuery(int id) {
        try {
            String sql = "DELETE FROM queries WHERE id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getSearchQueryPage(int id){
          try {
           // Statement state = con.createStatement();
            String sql = "SELECT lastPage FROM queries WHERE id=?";
            PreparedStatement stm =  con.prepareStatement(sql);
            stm.setInt(1, id);
            ResultSet res = stm.executeQuery();
            //ResultSet res = state.("SELECT lastPage FROM queries WHERE id=?");
            if (res.next()) {
                return res.getInt("lastPage");
            }
            stm.close();
            res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int saveQuery(String query, String searchEngine,int currentSearchPage) {
        int generatedKey = 0;
        try {

             generatedKey = findQueryId(query, searchEngine);
            if (generatedKey == -1) {
                PreparedStatement prep = con.prepareStatement("insert into query values(?,?,?,?);");
                prep.setString(2, query);
                prep.setString(3, searchEngine);
                prep.setInt(4, currentSearchPage);
                prep.execute();
                ResultSet rs = prep.getGeneratedKeys();

                if (rs.next()) {
                    generatedKey = rs.getInt(1);
                }
                prep.close();
                rs.close();
                //return generatedKey;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

   

    public void createDb() {
        try {
            Class.forName("org.sqlite.JDBC");

            con = DriverManager.getConnection("jdbc:sqlite:webtools.db");

            System.out.println("Database Opened...\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTables() {
        try {
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='queries'");
            if (!res.next()) {
                System.out.println("Building the User table with prepopulated values.");

                String sql = "CREATE TABLE queries "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " name TEXT NOT NULL, "
                        + " search_engine TEXT NOT NULL,"
                        + " lastPage INTEGER); ";

                stmt.executeUpdate(sql);
            }

            res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='search'");

            if (!res.next()) {
                System.out.println("Building the User table with prepopulated values.");

                String sql = "CREATE TABLE search "
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " protocol TEXT NOT NULL,"
                        + " dom TEXT NOT NULL,"
                        + " url TEXT NOT NULL, "
                        + " level INTEGER, "
                        + " parent_id INTEGER, "
                        + " page INTEGER, "
                        + " q_id INTEGER NOT NULL); ";

                stmt.executeUpdate(sql);
            }

            res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='filter'");
            if (!res.next()) {

                String sql = "CREATE TABLE  filter (id INTEGER PRIMARY KEY AUTOINCREMENT"
                        + ", parent_id integer,queryId integer,"
                        + "url TEXT NOT NULL )";
                stmt.executeUpdate(sql);
            }

            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
