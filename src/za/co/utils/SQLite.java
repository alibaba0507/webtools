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
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Executor;
import org.sqlite.SQLiteConnection;

/**
 *
 * @author alibaba0507
 */
public class SQLite {

    private Connection con;
    private static String QUERY_TBL_NAME = "queries";
    private static String SRCH_TBL_NAME = "search";
    private static SQLite instance;

    public SQLite() {
        //createDb();
        createTables();
    }

    public static SQLite getInstance() {
        if (instance == null) {
            instance = new SQLite();
        }
        return instance;
    }

    public void deleteSearch(int id) {
        try {
            createDb();
            String sql = "DELETE FROM " + SRCH_TBL_NAME + " WHERE id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            // statement.close();
            close(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int findSearch(int qId, String url) {
        try {
            createDb();
            URL u = new URL(url);
            String protocol = u.getProtocol();
            String host = u.getHost();
            int port = u.getPort();
            String path = u.getPath();
            if (port > 0) {
                host += ":" + port;
            }
            Statement state = con.createStatement();
            String sql = "SELECT id FROM " + SRCH_TBL_NAME + " WHERE "
                    + "q_id=? AND dom=? AND url=? ";
            PreparedStatement stm = con.prepareStatement(sql);
            //ResultSet res = state.executeQuery("SELECT id FROM search WHERE "
            //       + "q_id=? AND dom=? AND url=? ");
            stm.setInt(1, qId);
            stm.setString(2, host);
            stm.setString(3, path);
            ResultSet rs = stm.executeQuery();
            int id = -1;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            close(stm);
            //  rs.close();
            //  state.close();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Find domain and count (how many times domain is repeated)
     *
     * @param qId - queryId
     * @return List([domain name],[count])
     */
    public ArrayList<Vector> selectCoutDomains(int qId, int limit) {
        //String sql = "SELECT id FROM search WHERE "
        //         + "q_id=? AND dom=? AND url=? ";
        String sql = "SELECT dom,count(dom) FROM " + SRCH_TBL_NAME + " WHERE q_id=? AND level=0"
                + " GROUP BY dom ORDER BY count(dom) DESC";
        if (limit > 0) {
            sql += " LIMIT " + limit + ";";
        } else {
            sql += ";";
        }
        ArrayList<Vector> list = new ArrayList<Vector>();
        try {
            createDb();
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, qId);
            synchronized (con) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    //String[] row = new String[2];
                    //row[0] = rs.getString(1);
                    //row[1] = Integer.toString( rs.getInt(2));
                    Vector v = new Vector();
                    v.addElement(rs.getString(1));
                    v.addElement(Integer.toString(rs.getInt(2)));
                    list.add(v);
                }
                close(stm);
                con.notifyAll();
            }
//   stm.close();
            //   rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
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
            createDb();
            PreparedStatement prep = con.prepareStatement("insert into " + SRCH_TBL_NAME
                    + " (protocol,dom,url,level,parent_id,page,q_id) values(?,?,?,?,?,?,?);");
            URL u = new URL(url);
            String protocol = u.getProtocol();
            String host = u.getHost();
            int port = u.getPort();
            String path = u.getPath();
            if (port > 0) {
                host += ":" + port;
            }
            prep.setString(1, protocol);
            prep.setString(2, host);
            prep.setString(3, path);
            prep.setInt(4, level);
            prep.setInt(5, topId);
            prep.setInt(6, page);
            prep.setInt(7, qId);
            //prep.setInt(6, topId);
            synchronized (con) {
                prep.execute();
                ResultSet rs = prep.getGeneratedKeys();

                if (rs.next()) {
                    generatedKey = rs.getInt(1);
                }
                close(prep);
                con.notifyAll();
            }
//    prep.close();
            //    rs.close();
            //return generatedKey;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    public int findQueryId(String query, String searchEngine) {
        try {
            createDb();
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT id FROM " + QUERY_TBL_NAME + " WHERE name='" + query
                    + "' AND search_engine='" + searchEngine
                    + "'");
            if (res.next()) {
                return res.getInt("id");
            }
            close(state);
            //state.close();
            // res.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///notifyAll();
        return -1;
    }

    public void deleteQuery(int id) {
        try {
            String sql = "DELETE FROM " + QUERY_TBL_NAME + " WHERE id=?";
            createDb();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            // statement.close();
            close(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePage(int id, int page) {
        String sql = "UPDATE " + QUERY_TBL_NAME + " SET lastPage = ? "
                + "WHERE id = ?";
        PreparedStatement stm = null;
        try {
            createDb();
            boolean r = con.isReadOnly();
            stm = con.prepareStatement(sql);
            stm.setInt(1, page);
            stm.setInt(2, id);
            synchronized (con) {
                stm.execute();
                close(stm);
                con.notifyAll();
            }

            // stm.close();
            System.out.println("UPDADE TRANSACTION PAGE[" + page + "]");
        } catch (Exception e) {
            if (e.getMessage().indexOf("[SQLITE_BUSY]") > -1) {
                e.printStackTrace();
                // waitForTransaction();
                // updatePage(id, page);
            }
        }
    }

    public int getSearchQueryPage(int id) {
        try {
            // Statement state = con.createStatement();
            String sql = "SELECT lastPage FROM " + QUERY_TBL_NAME + " WHERE id=?";
            createDb();
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, id);
            int lastPage = -1;
            synchronized (con) {
                ResultSet res = stm.executeQuery();
                //ResultSet res = state.("SELECT lastPage FROM queries WHERE id=?");

                if (res.next()) {
                    lastPage = res.getInt("lastPage");
                }
                close(stm);
                con.notifyAll();
            }
            // stm.close();
            //   res.close();
            return lastPage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //notifyAll();
        return -1;
    }

    public int saveQuery(String query, String searchEngine, int currentSearchPage) {
        int generatedKey = 0;
        try {

            generatedKey = findQueryId(query, searchEngine);
            if (generatedKey == -1) {
                createDb();
                PreparedStatement prep = con.prepareStatement("insert into " + QUERY_TBL_NAME + " (name,search_engine,lastPage) values(?,?,?);");
                prep.setString(1, query);
                prep.setString(2, searchEngine);
                prep.setInt(3, currentSearchPage);
                synchronized (con) {
                    prep.executeUpdate();
                    ResultSet rs = prep.getGeneratedKeys();

                    if (rs.next()) {
                        generatedKey = rs.getInt(1);
                    }
                    close(prep);
                    con.notifyAll();
                }
                // prep.close();
                // rs.close();
                //return generatedKey;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedKey;
    }

    public synchronized void createDb() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (con == null) {
                con = DriverManager.getConnection("jdbc:sqlite:webtools.db");
                //org.sqlite.SQLiteConnection c =  (SQLiteConnection)DriverManager.getConnection("jdbc:sqlite:webtools.db");
                //c.
                con.clearWarnings();

                System.out.println("Database Opened...\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void close(Statement stmt) throws Exception {
        stmt.close();
        // con.close();
        //  con = null;
    }

    public void createTables() {
        try {
            createDb();
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + QUERY_TBL_NAME + "'");
            if (!res.next()) {
                System.out.println("Building the User table with prepopulated values.");

                String sql = "CREATE TABLE " + QUERY_TBL_NAME
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " name TEXT NOT NULL, "
                        + " search_engine TEXT NOT NULL,"
                        + " lastPage INTEGER); ";

                stmt.executeUpdate(sql);
            }

            res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + SRCH_TBL_NAME + "'");

            if (!res.next()) {
                System.out.println("Building the User table with prepopulated values.");

                String sql = "CREATE TABLE " + SRCH_TBL_NAME
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
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
            close(stmt);
            //   stmt.close();
            //con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
