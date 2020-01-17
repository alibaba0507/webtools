/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.net;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import webtools.gui.GUIController;
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
    private String regexParserString;
    private int queryId;
    private String searchQuery;
    private WebConnector webC;
    private long maxDelayInSec = 30;
    private long sleepTimeInSec = 30;
    private ConnectorCallback callback;

    public WebCrawler(String title, ConnectorCallback callback) {
        this.name = title;
        if (callback == null) {
            this.callback = this;
        } else {
            this.callback = callback;
        }
        // WebToolMainFrame.instance.getConsole().setText("");
        WebToolMainFrame.instance.getConsole().append(">>>>> INIT WebCrawler [" + title + "]..... >>>>>\n");
        sql = SQLite.getInstance();
        String[] list = (String[]) WebToolMainFrame.defaultProjectProperties.get(title);
        /*
        String[] labels = {"Project Name", "Search Query", "Search Engine",
             "Search URL",
             "Link Regex"}
         */
        this.searchQuery = list[1];
        this.searcURL = list[3];
        this.regexStr = list[4];
        if (list.length > 5) {
            this.regexParserString = list[5];
        }
        queryId = sql.saveQuery(list[1], list[3], 0);

    }

    @Override
    public String toString() {
        return this.name;// return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        if (!WebCrawler.threadCrawlers.contains(WebCrawler.this.name)) {
            WebCrawler.threadCrawlers.add(WebCrawler.this.name);
        }
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                //      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                Font f = WebToolMainFrame.instance.getConsole().getFont();
                WebToolMainFrame.instance.getConsole().setFont(f.deriveFont(Font.BOLD));
                WebToolMainFrame.instance.getConsole().append(">>>>>>>>>>>>>>> START CRAWLING [" + WebCrawler.this.name + "] >>>>>\n");

                while (WebCrawler.threadCrawlers.contains(WebCrawler.this.name)) {
                    webC = new WebConnector();
                    long tm = System.currentTimeMillis();
                    System.err.println("Before time out ....");
                    startCrawling();
                    long crawlingTime = (System.currentTimeMillis() - tm) / 1000;
                    if (LastHTTPError.errorCode == -1) { // Empty doc 
                        JOptionPane.showMessageDialog(WebToolMainFrame.getDesckTopInstance(),
                                 "Request was processed but return Empty Result. Service will be stoped.",
                                 "Empty Result", JOptionPane.INFORMATION_MESSAGE);

                        WebCrawler.this.stop();
                        break;
                    }
                    if (LastHTTPError.errorCode == 429) {
                        // inscreace time in for Too many Request
                        //sleepTimeInSec += maxDelayInSec;
                        // if (sleepTimeInSec / maxDelayInSec > 2)
                        // {
                        JOptionPane.showMessageDialog(WebToolMainFrame.getDesckTopInstance(),
                                 "Too many request.Could be and of search pages or Search engine wait period 5 - 15 min.If you have a proxy you can useit or replace it. Service will be Stoped",
                                 "Service Too many Request", JOptionPane.INFORMATION_MESSAGE);

                        WebCrawler.this.stop();
                        break;
                        // }
                    } else if (LastHTTPError.errorCode == 403) {
                        JOptionPane.showMessageDialog(WebToolMainFrame.getDesckTopInstance(),
                                 "403 Forbidden. The request was rejected by server.Service will be stoped",
                                 "403 Request - Forbidden", JOptionPane.INFORMATION_MESSAGE);

                        WebCrawler.this.stop();
                        break;
                    }
                    sleepTimeInSec = maxDelayInSec;

                    if (crawlingTime < sleepTimeInSec) {
                        timeOut(sleepTimeInSec - crawlingTime); // to prevent crawler from blocking Err = 429
                    }
                    System.err.println("After Time out ... [" + ((System.currentTimeMillis() - tm) / 1000) + "] Sec");
                }
                System.out.println(">>>>>>>>>>>>>>> STOP CRAWLING [" + WebCrawler.this.name + "] >>>>>");
                f = WebToolMainFrame.instance.getConsole().getFont();
                WebToolMainFrame.instance.getConsole().setFont(f.deriveFont(Font.BOLD));
                WebToolMainFrame.instance.getConsole().append(">>>>>>>>>>>>>>> STOP CRAWLING [" + WebCrawler.this.name + "] >>>>>\n");

                return webC;
            }
        };
        worker.execute();

//startCrawling();
//   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void timeOut(long delay) {
        //synchronized (this) {
        long tm = System.currentTimeMillis();
        // if (lastTimeAccess != 0 && tm - lastTimeAccess < (delayInSec * 1000)) {
        if (delay == 0) {
            delay = maxDelayInSec;
        }
        double randomDouble = Math.random();
        randomDouble = randomDouble * delay + 5;
        int randomInt = (int) randomDouble;
        try {
            while (System.currentTimeMillis() - tm < (randomInt * 1000)) {
                Thread.sleep(100);
                Thread.yield();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        lastTimeAccess = System.currentTimeMillis();
        //}
    }

    public synchronized void stop() {
        WebCrawler.threadCrawlers.remove(this.name);

    }

    private void startCrawling() {

        int page = sql.getSearchQueryPage(queryId);
        //int startFrom = 1;
        //if (page > 0) {
        //startFrom = page * 10;
        // }
        if (page == 0) {
            page = 1;
        }
        String ipAddr = this.searcURL + WebRequest.encodeValue(this.searchQuery) + "&start=" + (page * 10);
        try {
            Document doc = webC.get(new WebRequest(ipAddr, null));// new WebTools().search(urlEncode, "", true);
            if (doc != null) {
                this.callback.callback(name, searchQuery, regexStr, regexParserString, Integer.toString(page), doc);
            } else if (doc == null && LastHTTPError.errorCode == 0) {
                LastHTTPError.errorCode = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //}
    }

    @Override
    public void callback(String title, String searchQuery, String regexStr, String regexParserStr, String page, Document doc) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (doc != null) {
            Elements links = doc.select(regexStr);
            if (links.size() > 3) {
                sql.updatePage(queryId, Integer.parseInt(page) + 1);
                // SQLite tmpSql = new SQLite();
                // tmpSql.updatePage(queryId, Integer.parseInt(page)+1);
                try {
                    boolean hasUpdate = false;
                    for (org.jsoup.nodes.Element link : links) {
                        final String linkTitle = link.text();
                        //  final String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                        final String result = link.attr("href");
                        sql.saveSearch(queryId, result, 0, 0, Integer.parseInt(page));
                        //final String url = URLDecoder.decode(result, "UTF-8");
                        hasUpdate = true;
                    }// end for
                    // strip HTML , this is plain text
                    String txt = doc.text();
                    boolean foundRegex = false;
                    if (regexParserString != null && !regexParserString.trim().equals("")) {

                        Pattern p = Pattern.compile(regexParserString.trim());
                        Matcher matcher = p.matcher(txt);

                        while (matcher.find()) {
                            String res = matcher.group();
                            // System.out.println(res);
                            foundRegex = true;
                            sql.saveReqex(queryId, res);
                        }//  end while
                    }

                    // String[] words = txt.split("\\s+");
                    List<String> list = Arrays.asList(txt.split("\\s+"));
                    Set<String> uniqueWords = new HashSet<String>(list);
                    Vector<Vector> w = new Vector<Vector>();
                    String excludeWords = " Search Cache Cached Menu link links home ";
                    for (String word : uniqueWords) {
                        if (word.length() > 4 && !excludeWords.toLowerCase().contains(" " + word.toLowerCase() + " ")) {
                            Vector cols = new Vector();
                            cols.add(word);
                            cols.add(Integer.valueOf(Collections.frequency(list, word)));
                            w.add(cols);
                            //System.out.println(word + ": " + Collections.frequency(list, word));
                        }
                    }// end for
                    Collections.sort(w,
                            new MyComparator(false, 1));
                    int cnt = (w.size() < 30) ? w.size() : 30;
                    for (int i = 0; i < cnt; i++) {
                        Vector v = w.get(i);
                        sql.saveKeyWords(queryId, (String) v.get(0), ((Integer) v.get(1)).intValue());

                    }// end for
                    if (hasUpdate) {
                        GUIController.getProjectPanel(this.name).updateSearchTableModel();
                    }
                    if (foundRegex) {
                        GUIController.getProjectPanel(this.name).updateRegexTableModel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    Font f = WebToolMainFrame.instance.getConsole().getFont();
                    Color oldColor = WebToolMainFrame.instance.getConsole().getForeground();
                    Font errFont = f.deriveFont(15);
                    errFont = errFont.deriveFont(Font.BOLD);
                    WebToolMainFrame.instance.getConsole().setFont(errFont);
                    WebToolMainFrame.instance.getConsole().append("Error Crawling ["
                            + title + "][" + searchQuery + "][" + e.toString() + "] >>>>>\n");
                    WebToolMainFrame.instance.getConsole().setFont(f);
                }
            } else if (Integer.parseInt(page) > 1) { // and there is no more links so we have to stop this
                //if (doc == null && LastHTTPError.errorCode == 0)
                LastHTTPError.errorCode = -1;
            }
        }
    }
}

class MyComparator implements Comparator {

    protected boolean isSortAsc;
    private int sortIndx = 1;

    public MyComparator(boolean sortAsc, int sortIndx) {
        isSortAsc = sortAsc;
        this.sortIndx = sortIndx;
    }

    public MyComparator(boolean sortAsc) {
        isSortAsc = sortAsc;
    }

    public int compare(Object o1, Object o2) {
        //if (!(o1 instanceof Integer) || !(o2 instanceof Integer)) {
        //   return 0;
        // }
        Integer s1 = Integer.valueOf(((Vector) o1).elementAt(sortIndx).toString());
        Integer s2 = Integer.valueOf(((Vector) o2).elementAt(sortIndx).toString());
        int result = 0;
        result = s1.compareTo(s2);
        if (!isSortAsc) {
            result = -result;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MyComparator) {
            MyComparator compObj = (MyComparator) obj;
            return compObj.isSortAsc == isSortAsc;
        }
        return false;
    }
}

