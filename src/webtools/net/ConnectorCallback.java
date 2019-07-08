/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.net;

import org.jsoup.nodes.Document;

/**
 *
 * @author alibaba0507
 */
public interface ConnectorCallback {
    public void callback(String title , String searchQuery,String regexStr,String page,Document doc);
}
