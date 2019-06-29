/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author alibaba0507
 */
public class WebTools {

    private final static String GOOGLE_SEARCH_URL = "http://www.google.com/search?q=";
    private final static String GOOGLE_REGEX_SEARCH = "div[class='r']>a";
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";

    /**
     * @param args the command line arguments
     */
    public static void mainRun(String[] args) {
        // TODO code application logic here
        try {
            int page = 1;
            String query = "yahoo";
            String q =  query + "&start=" + page * 10;
            String charset = "UTF-8";
            String urlEncode = GOOGLE_SEARCH_URL  + q;//URLEncoder.encode(q, charset);
            Document doc = new WebTools().search(urlEncode, "", true);
            if (doc != null) {
                
                Elements links = doc.select("div[class='r']>a");//google only
                // Elements links = doc.getElementById("links").getElementsByClass("results_links");
                if (links.size() < 2) { // we have a problem something went wrong 
                    System.err.println(" Something went wrong request >>> " + urlEncode + " >>>>>");
                } else {
                    ArrayList duplicateList = new ArrayList();
                    for (org.jsoup.nodes.Element link : links) {
                        final String title = link.text();
                        //  final String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                        final String result = link.attr("href");
                        
                        final String url = URLDecoder.decode(result, "UTF-8");
                        
                        if (!url.startsWith("http")) {
                            continue; // Ads/news/etc.
                        } else if (title == null || title.equals("") || title.equalsIgnoreCase("ad")) {
                            continue;
                        } else if (duplicateList.contains(url)) {
                            continue;
                        }
                        duplicateList.add(url);
                        //JSONObject o = new JSONObject();
                        //o.put("title", title);
                        //o.put("URL", url);
                        //arrJSON.add(o);
                        // LinksObject lnk = new LinksObject();
                        // lnk.setKeyWord(keyWord);
                        // lnk.setLink(url);
                        // lnk.setTitle(title);
                        //tbl.add(lnk);
                        //ProjectsUI.console.append(" Link -> Title[" + title + "] \r\n");
                        //ProjectsUI.console.append(" Link -> URL[" + url + "] \r\n");
                        System.err.println(" Link -> Title[" + title + "] ");
                        System.err.println(" Link -> URL[" + url + "] ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Document search(String url, String cookies, boolean isFollowRedirect) {
        Connection.Response response;
        try {
            response = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .header("Accept-Language", "en-US")
                    .header("Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Cookie", cookies)
                    .ignoreContentType(true) // This is used because Jsoup "approved" content-types parsing is enabled by default by Jsoup
                    .followRedirects(false)
                    .execute();
            if (response.hasHeader("location") && isFollowRedirect) {
                String redirectUrl = "";
                redirectUrl = response.header("location");
                Map<String, String> cookiesMap = response.cookies();
                cookies = "";
                Iterator it = cookiesMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = (String) cookiesMap.get(key);
                    if (cookies != "") {
                        cookies += ";";
                    }
                    cookies += key + "=" + value;
                }
                //ProjectsUI.console.append(" Reirect [" + redirectUrl + "]\r\n");
                //ProjectsUI.console.append(" Coocies [" + cookies + "]\r\n");
                return search(redirectUrl, cookies, isFollowRedirect);
            }
            String body = response.body();
            //System.out.print(body);
            return Jsoup.parse(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
