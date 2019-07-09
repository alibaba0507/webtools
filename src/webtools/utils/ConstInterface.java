/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.utils;

import webtools.gui.CrawlPanel;

/**
 *
 * @author alibaba0507
 */
public interface ConstInterface {
    public static final SearchParams GOOGLE_SEARCH = new SearchParams("Google"
                        , "http://www.google.com/search?q=", "div[class='r']>a");
    
    public static final SearchParams GOOGLE_SEARCH_UK = new SearchParams("Google UK"
                        , "http://www.google.co.uk/search?q=", "div[class='r']>a");
    
     public static final SearchParams GOOGLE_SEARCH_NZ = new SearchParams("Google NZ"
                        , "http://www.google.co.nz/search?q=", "div[class='r']>a");
     
     public static final SearchParams GOOGLE_SEARCH_AU = new SearchParams("Google AU"
                        , "http://www.google.com.au/search?q=", "div[class='r']>a");
     
     public static final SearchParams GOOGLE_SEARCH_DE = new SearchParams("Google DE"
                        , "http://www.google.de/search?q=", "div[class='r']>a");
     
     public static final SearchParams GOOGLE_SEARCH_SA = new SearchParams("Google SA"
                        , "http://www.google.co.za/search?q=", "div[class='r']>a");
     
    public static final SearchParams BING_SEARCH = new SearchParams("Bing"
                        , "https://www.bing.com/search?q=", "#b_results .b_algo h2 a");
    
    public static final SearchParams DUCKDUCK_GO_SEARCH = new SearchParams("DuckDuckGo"
                        , "https://duckduckgo.com/html/?q=", "#links .results_links .links_main a");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String CURRENT_DIR = System.getProperty("user.dir");
    public static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // to make pattern find list of object remove front ^ and $ back
    // to add multiple white space \\s+
    public static final String ALL_EMAILS_PATTERN = "[a-zA-Z0-9_.+-]\\s++@\\s+[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
}
