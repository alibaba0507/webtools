/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.net;

import java.net.URLDecoder;
import java.util.HashSet;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webtools.gui.run.WebToolMainFrame;
import za.co.utils.SQLite;
import za.co.utils.WebConnector;

/**
 *
 * @author alibaba0507
 */
public class WebCrawler implements Runnable, ConnectorCallback {

    public static HashSet threadCrawlers = new HashSet<Object>();
    public static long lastTimeAccess;
    private String name;
    private SQLite sql;
    private String searcURL;
    private String regexStr;
    private int queryId;
    private String searchQuery;
    private WebConnector webC;
    private long delayInSec = 10;
    private ConnectorCallback callback;

    public WebCrawler(String title, ConnectorCallback callback) {
        this.name = title;
        if (callback == null) {
            this.callback = this;
        } else {
            this.callback = callback;
        }
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
            long tm = System.currentTimeMillis();
            if (lastTimeAccess != 0 && tm - lastTimeAccess < (delayInSec & 1000)) {
                double randomDouble = Math.random();
                randomDouble = randomDouble * 25 + 15;
                int randomInt = (int) randomDouble;
                try {
                    while (System.currentTimeMillis() - tm < (randomInt * 1000)) {
                        Thread.sleep(100);
                        Thread.yield();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            lastTimeAccess = System.currentTimeMillis();
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
            if (page == 0) {
                page = 1;
            }
            String ipAddr = this.searcURL + this.searchQuery + "&start=" + (page * 10);
            try {
                Document doc = webC.get(ipAddr);// new WebTools().search(urlEncode, "", true);
                this.callback.callback(name, searchQuery, regexStr, Integer.toString(page), doc);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }// end while 
    }

    @Override
    public void callback(String title, String searchQuery, String regexStr, String page, Document doc) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (doc != null) {
            Elements links = doc.select(regexStr);
            if (links.size() > 2) {
                sql.updatePage(queryId, Integer.parseInt(page + 1));
                try {
                    for (org.jsoup.nodes.Element link : links) {
                        final String linkTitle = link.text();
                        //  final String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                        final String result = link.attr("href");
                        sql.saveSearch(queryId, result,0,0,Integer.parseInt(page) );
                        
                        final String url = URLDecoder.decode(result, "UTF-8");
                        
                    }// end for
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
