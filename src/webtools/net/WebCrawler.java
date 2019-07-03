/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.net;

import java.util.HashSet;
import org.jsoup.nodes.Document;
import webtools.gui.run.WebToolMainFrame;
import za.co.utils.SQLite;
import za.co.utils.WebConnector;

/**
 *
 * @author alibaba0507
 */
public class WebCrawler implements Runnable {

    public static HashSet threadCrawlers = new HashSet<Object>();
    private String name;
    private SQLite sql;
    private String searcURL;
    private String regexStr;
    private int queryId;
    private String searchQuery;
    private WebConnector webC;

    public WebCrawler(String title) {
        this.name = title;
        sql = new SQLite();
        String[] list = (String[]) WebToolMainFrame.defaultProjectProperties.get(title);
        /*
        String[] labels = {"Project Name", "Search Query", "Search Engine",
             "Search URL",
             "Link Regex"}
         */
        this.searchQuery = list[1];
        this.searcURL = list[3];
        this.regexStr = list[4];
        queryId = sql.saveQuery(list[1], list[3], 0);

    }

    @Override
    public String toString() {
        return this.name;// return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        if (!WebCrawler.threadCrawlers.contains(this.name)) {
            WebCrawler.threadCrawlers.add(this.name);
            webC = new WebConnector();
            startCrawling();
        }
        //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public synchronized void stop() {
        WebCrawler.threadCrawlers.remove(this.name);

    }

    private void startCrawling() {
        // this will stop if is removed from hashset
        while (WebCrawler.threadCrawlers.contains(this.name)) {
            int page = sql.getSearchQueryPage(queryId);
            //int startFrom = 1;
            //if (page > 0) {
            //startFrom = page * 10;
            // }
            String ipAddr = this.searcURL + this.searchQuery + "&start=" + (page * 10);
            
            Document doc = webC.get(ipAddr, "", true, null, 8118, null, null);// new WebTools().search(urlEncode, "", true);

        }// end while 
    }
}
