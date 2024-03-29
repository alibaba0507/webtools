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
import webtools.gui.run.WebToolMainFrame;

/**
 *
 * @author alibaba0507
 */
public class SQLite {

    private Connection con;
    private static String QUERY_TBL_NAME = "queries";
    private static String SRCH_TBL_NAME = "search";
    private static String REGEX_TBL_NAME = "regexSearch";
    // this table will save resul of most used words among this sites
    private static String USED_KEYWORDS_TBL_NAME = "usedKeywords";

    private static SQLite instance;
    private String dbFile = "webtools1.db";
    private String bkpFile = "webtools.db";
    private String currentDbFile = "";
    public SQLite() {
        //createDb();
        if (currentDbFile == "")
        {
            currentDbFile = dbFile;
        }
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
            createDb(false);
            String sql = "DELETE FROM " + SRCH_TBL_NAME + " WHERE id=?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            // statement.close();
            close(statement);
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
    }

    public int[] findKeyWord(int qId, String word) {
        int ret[] = new int[0];
        try {
            createDb(false);
            String sql = "SELECT id,count FROM " + USED_KEYWORDS_TBL_NAME + " WHERE "
                    + "q_id=? AND word=?";
            PreparedStatement stm = con.prepareStatement(sql);
            //ResultSet res = state.executeQuery("SELECT id FROM search WHERE "
            //       + "q_id=? AND dom=? AND url=? ");
            stm.setInt(1, qId);
            stm.setString(2, word);
            ResultSet rs = stm.executeQuery();
            int id = -1;
            if (rs.next()) {
                ret = new int[2];
                ret[0] = rs.getInt("id");
                ret[1] = rs.getInt("count");
            }
            close(stm);
            //  rs.close();
            //  state.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
        return ret;
    }

    public int findRegexSearch(int qId, String txt) {
        try {
            createDb(false);
            String sql = "SELECT id FROM " + REGEX_TBL_NAME + " WHERE "
                    + "q_id=? AND txt=?";
            PreparedStatement stm = con.prepareStatement(sql);
            //ResultSet res = state.executeQuery("SELECT id FROM search WHERE "
            //       + "q_id=? AND dom=? AND url=? ");
            stm.setInt(1, qId);
            stm.setString(2, txt);
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
            createDb(true);
        }
        return 0;
    }

    public int findSearch(int qId, String url) {
        try {
            createDb(false);
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
            createDb(true);
        }
        return 0;
    }

    /**
     * Find all pages (URL) associated with selected domain
     *
     * @param qId - query ID
     * @param domain - selected domain
     * @return
     */
    public ArrayList<Vector> selectURLByDomain(int qId, String domain) {
        String sql = "SELECT  protocol,dom,url,level,parent_id,page FROM " + SRCH_TBL_NAME + " WHERE q_id=? AND dom=?"
                + " ORDER BY page DESC";
        //protocol,dom,url,level,parent_id,page,q_id
        ArrayList<Vector> list = new ArrayList<Vector>();
        try {
            createDb(false);
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, qId);
            stm.setString(2, domain);
            synchronized (con) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    /*  "URL",
                             "Google Page Number",
                             "Crawl level",
                             "Parent ID"
                     */
                    Vector v = new Vector();
                    String url = rs.getString(1) + "://"
                            + rs.getString(2)
                            + rs.getString(3);
                    v.addElement(url);
                    v.addElement(Integer.toString(rs.getInt(6)));
                    v.addElement(Integer.toString(rs.getInt(4)));
                    v.addElement(Integer.toString(rs.getInt(5)));
                    list.add(v);
                }
                close(stm);
                con.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
        return list;
    }
    
      /**
     * Find all pages (URL) associated with selected domain
     *
     * @param qId - query ID
     * @param domain - selected domain
     * @return
     */
    public ArrayList<Vector> selectURLById(int id) {
        String sql = "SELECT  protocol,dom,url,level,parent_id,page FROM " + SRCH_TBL_NAME + " WHERE id=?";
        //protocol,dom,url,level,parent_id,page,q_id
        ArrayList<Vector> list = new ArrayList<Vector>();
        try {
            createDb(false);
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, id);
            //stm.setString(2, domain);
            synchronized (con) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    /*  "URL",
                             "Google Page Number",
                             "Crawl level",
                             "Parent ID"
                     */
                    Vector v = new Vector();
                    String url = rs.getString(1) + "://"
                            + rs.getString(2)
                            + rs.getString(3);
                    v.addElement(url);
                    v.addElement(Integer.toString(rs.getInt(6)));
                    v.addElement(Integer.toString(rs.getInt(4)));
                    v.addElement(Integer.toString(rs.getInt(5)));
                    list.add(v);
                }
                close(stm);
                con.notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
        return list;
    }

    public Vector<Vector> selectKeywords(int qId) {
        String sql = "SELECT word,count FROM " + USED_KEYWORDS_TBL_NAME + " WHERE q_id=? "
                + " GROUP BY word ORDER BY count DESC";
        Vector<Vector> list = new Vector<Vector>();
        try {
            createDb(false);
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, qId);

            synchronized (con) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    Vector v = new Vector();
                    v.addElement((rs.getString(1)));
                    v.addElement(Integer.valueOf(rs.getInt(2)));
                    list.add(v);
                }
                close(stm);
                con.notifyAll();
            }
//   stm.close();
            //   rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }

        return list;

    }
    public ArrayList<Vector> selectAllRegex()
    {
        String sql = "SELECT id,txt FROM " + REGEX_TBL_NAME + ""
                + " GROUP BY txt ORDER BY id ASC";
         ArrayList<Vector> list = new ArrayList<Vector>();
        try {
            createDb(false);
            PreparedStatement stm = con.prepareStatement(sql);
           // stm.setInt(1, qId);
            //stm.setInt(2, startIndx);
            synchronized (con) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    Vector v = new Vector();
                    v.addElement(Integer.valueOf(rs.getInt(1)));
                    v.addElement((rs.getString(2)));
                    list.add(v);
                }
                close(stm);
                con.notifyAll();
            }
//   stm.close();
            //   rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }

        return list;
        
    }
    public ArrayList<Vector> selectRegex(int qId, int startIndx) {
        String sql = "SELECT id,txt FROM " + REGEX_TBL_NAME + " WHERE q_id=? AND id>? "
                + " GROUP BY txt ORDER BY id ASC";

        ArrayList<Vector> list = new ArrayList<Vector>();
        try {
            createDb(false);
            PreparedStatement stm = con.prepareStatement(sql);
            stm.setInt(1, qId);
            stm.setInt(2, startIndx);
            synchronized (con) {
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    Vector v = new Vector();
                    v.addElement(Integer.valueOf(rs.getInt(1)));
                    v.addElement((rs.getString(2)));
                    list.add(v);
                }
                close(stm);
                con.notifyAll();
            }
//   stm.close();
            //   rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }

        return list;
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
            createDb(false);
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
            createDb(true);
        }

        return list;
    }

    public int saveKeyWords(int qId, String word, int cnt) {
        int generatedKey = 0;
        try {

            int ret[] = findKeyWord(qId, word);
            if (ret.length > 0) {
                // update
                updateKeyWord(ret[0], ret[1] + cnt);
                return ret[0];
            }
            // int id = findQueryId(projecName, query, searchEngine);
            // if (id == -1) {
            createDb(false);
            PreparedStatement prep = con.prepareStatement("insert into " + USED_KEYWORDS_TBL_NAME
                    + " (word,count,q_id) values(?,?,?);");

            prep.setString(1, word);
            prep.setInt(2, cnt);
            prep.setInt(3, qId); // coujnt of the words
            //prep.setInt(6, topId);
            synchronized (con) {
                prep.executeUpdate();
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
            createDb(true);
        }

        return generatedKey;
    }

    public int saveReqex(int qId, String reqexStr) {
        int generatedKey = 0;
        try {

            generatedKey = findRegexSearch(qId, reqexStr);
            if (generatedKey > 0) {
                return generatedKey;
            }
            // int id = findQueryId(projecName, query, searchEngine);
            // if (id == -1) {
            createDb(false);
            PreparedStatement prep = con.prepareStatement("insert into " + REGEX_TBL_NAME
                    + " (txt,q_id) values(?,?);");

            prep.setString(1, reqexStr);

            prep.setInt(2, qId);
            //prep.setInt(6, topId);
            synchronized (con) {
                prep.executeUpdate();
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
            createDb(true);
        }
        return generatedKey;
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
            createDb(false);
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
                prep.executeUpdate();
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
            createDb(true);
        }
        return generatedKey;
    }

    public int findQueryId(String query, String searchEngine) {
        try {
            createDb(false);
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
            createDb(true);
        }
        ///notifyAll();
        return -1;
    }

    public void deleteSearchByQueryId(int id) {
        try {
            String sql = "DELETE FROM " + SRCH_TBL_NAME + " WHERE q_id=?";
            createDb(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            synchronized (con) {
                int rowsDeleted = statement.executeUpdate();
                // statement.close();
                close(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
    }

    public void deleteKeywordsQuery(int id) {
        try {
            String sql = "DELETE FROM " + USED_KEYWORDS_TBL_NAME + " WHERE q_id=?";
            createDb(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            // statement.close();
            close(statement);

        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
    }

    public void deleteRegexQuery(int id) {
        try {
            String sql = "DELETE FROM " + REGEX_TBL_NAME + " WHERE q_id=?";
            createDb(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            synchronized (con) {
                int rowsDeleted = statement.executeUpdate();
                // statement.close();
                close(statement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
    }

    public void deleteQuery(int id) {
        try {
            if (id <= 0) {
                return;
            }
            String sql = "DELETE FROM " + QUERY_TBL_NAME + " WHERE id=?";
            createDb(false);
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            synchronized (con) {
                int rowsDeleted = statement.executeUpdate();
                // statement.close();
                close(statement);
            }
            deleteRegexQuery(id); // delete all assocated queries
            deleteSearchByQueryId(id);
        } catch (Exception e) {
            e.printStackTrace();
            createDb(true);
        }
    }

    public void updateKeyWord(int id, int count) {
        String sql = "UPDATE " + USED_KEYWORDS_TBL_NAME + " SET count = ? "
                + "WHERE id = ?";
        PreparedStatement stm = null;
        try {
            createDb(false);
            boolean r = con.isReadOnly();
            stm = con.prepareStatement(sql);
            stm.setInt(1, count);
            stm.setInt(2, id);
            synchronized (con) {
                stm.executeUpdate();
                close(stm);
                con.notifyAll();
            }

            // stm.close();
            System.out.println("UPDADE TRANSACTION WORD CNT[" + count + "]");
            WebToolMainFrame.instance.getConsole().append("UPDADE TRANSACTION WORD CNT[" + count + "]\n");
        } catch (Exception e) {
            if (e.getMessage().indexOf("[SQLITE_BUSY]") > -1) {
                e.printStackTrace();
                // waitForTransaction();
                // updatePage(id, page);
            }
            createDb(true);
        }
    }

    public void updatePage(int id, int page) {
        String sql = "UPDATE " + QUERY_TBL_NAME + " SET lastPage = ? "
                + "WHERE id = ?";
        PreparedStatement stm = null;
        try {
            createDb(false);
            boolean r = con.isReadOnly();
            stm = con.prepareStatement(sql);
            stm.setInt(1, page);
            stm.setInt(2, id);
            synchronized (con) {
                stm.executeUpdate();
                close(stm);
                con.notifyAll();
            }

            // stm.close();
            System.out.println("UPDADE TRANSACTION PAGE[" + page + "]");
            WebToolMainFrame.instance.getConsole().append("UPDADE TRANSACTION PAGE[" + page + "]\n");
        } catch (Exception e) {
            if (e.getMessage().indexOf("[SQLITE_BUSY]") > -1) {
                e.printStackTrace();
                // waitForTransaction();
                // updatePage(id, page);
            }
            createDb(true);
        }
    }

    public int getSearchQueryPage(int id) {
        try {
            // Statement state = con.createStatement();
            String sql = "SELECT lastPage FROM " + QUERY_TBL_NAME + " WHERE id=?";
            createDb(false);
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
            createDb(true);
        }
        //notifyAll();
        return -1;
    }

    public int saveQuery(String query, String searchEngine, int currentSearchPage) {
        int generatedKey = 0;
        try {

            generatedKey = findQueryId(query, searchEngine);
            if (generatedKey == -1) {
                createDb(false);
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
            createDb(true);
        }
        return generatedKey;
    }

    public synchronized void createDb(boolean  clearLock) {
        try {
            Class.forName("org.sqlite.JDBC");
            if (con == null || clearLock) {
                if (clearLock)
                {
                    con.commit();
                    con.close();
                    con = null;
                    //String delFile = "";
                    if (currentDbFile.equals(dbFile))
                    {
                        //delFile = dbFile;
                        currentDbFile = bkpFile;
                    }else if (currentDbFile.equals(bkpFile))
                    {
                      currentDbFile = dbFile;  
                    }
                    createTables();
                }
                con = DriverManager.getConnection("jdbc:sqlite:" + currentDbFile);
                //org.sqlite.SQLiteConnection c =  (SQLiteConnection)DriverManager.getConnection("jdbc:sqlite:webtools.db");
                //c.
                con.clearWarnings();
                if (!currentDbFile.equals(dbFile) && new File(dbFile).exists())
                {
                    new File(dbFile).delete();
                }
                if (!currentDbFile.equals(bkpFile) && new File(bkpFile).exists())
                {
                    new File(bkpFile).delete();
                }
                System.out.println("Database Opened...\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void close(Statement stmt) throws Exception {
        stmt.close();
         //con.close();
        //  con = null;
    }

    public void createTables() {
        try {
            createDb(false);
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

            res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + REGEX_TBL_NAME + "'");
            if (!res.next()) {
                System.out.println("Building the User [" + REGEX_TBL_NAME + "] table with prepopulated values.");

                String sql = "CREATE TABLE " + REGEX_TBL_NAME
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " txt TEXT NOT NULL,"
                        + " q_id INTEGER NOT NULL); ";

                stmt.executeUpdate(sql);
            }

            res = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + USED_KEYWORDS_TBL_NAME + "'");
            if (!res.next()) {
                System.out.println("Building the User [" + USED_KEYWORDS_TBL_NAME + "] table with prepopulated values.");

                String sql = "CREATE TABLE " + USED_KEYWORDS_TBL_NAME
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " word TEXT NOT NULL,"
                        + " count INTEGER,"
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




