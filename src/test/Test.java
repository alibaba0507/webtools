/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.UserAgent;

/**
 *
 * @author Sh4D0W
 */
public class Test {

    public static void main(String[] args) {
        try {
            UserAgent userAgent = new UserAgent();         //create new userAgent (headless browser)
            userAgent.visit("http://bing.com");          //visit google
            userAgent.doc.apply("butterflies").submit();   //apply form input and submit

            Elements links = userAgent.doc.findEvery("<h3>").findEvery("<a>");  //find search result links
            for (Element link : links) {
                System.out.println(link.getAt("href"));   //print results
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


