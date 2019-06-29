/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.io.Serializable;

/**
 *
 * @author alibaba0507
 */
public class SearchObject implements Serializable{
   private String searchEngine,linksRegex;

    @Override
    public String toString() {
        return getSearchEngine();
    }

    /**
     * @return the searchEngine
     */
    public String getSearchEngine() {
        return searchEngine;
    }

    /**
     * @param searchEngine the searchEngine to set
     */
    public void setSearchEngine(String searchEngine) {
        this.searchEngine = searchEngine;
    }

    /**
     * @return the linksRegex
     */
    public String getLinksRegex() {
        return linksRegex;
    }

    /**
     * @param linksRegex the linksRegex to set
     */
    public void setLinksRegex(String linksRegex) {
        this.linksRegex = linksRegex;
    }
   
   
}
