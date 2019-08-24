/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author alibaba0507
 */
public class WebRequest {
    private String originalURL;
    private String currentURL;
   
    public WebRequest(String originalURL,String currentURL )
    {
        this.originalURL = originalURL;
        if (currentURL == null)
            this.currentURL = originalURL;
        else
            this.currentURL = currentURL;
    }
    
    // Method to encode a string value using `UTF-8` encoding scheme
    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
    /**
     * @return the originalURL
     */
    public String getOriginalURL() {
        return (originalURL);
    }

    /**
     * @param originalURL the originalURL to set
     */
    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }

    /**
     * @return the currentURL
     */
    public String getCurrentURL() {
        return (currentURL) ;
    }

    /**
     * @param currentURL the currentURL to set
     */
    public void setCurrentURL(String currentURL) {
        this.currentURL = currentURL;
    }
}
