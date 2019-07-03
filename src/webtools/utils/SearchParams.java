/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.utils;

public class SearchParams {
        private String searchEngineName;
        private String searchURL;
        private String linkReqex;
        
        public SearchParams(String name,String url,String regex)
        {
            this.searchEngineName = name;
            this.searchURL = url;
            this.linkReqex = regex;
        }
        public String[] getParams()
        {
            String[] s = new String[3];
            s[0] = (searchEngineName == null)?"":searchEngineName;
            s[1] = (searchURL == null)?"":searchURL;
            s[3] = (linkReqex == null)?"":linkReqex;
            return s;
        }
        public void setParams(String[] s)
        {
            if (s.length == 3)
            {
                this.searchEngineName = s[0];
                this.searchURL = s[1];
                this.linkReqex = s[2];
            }
        }
        @Override
        public String toString() {
            return this.getSearchEngineName(); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * @return the searchEngineName
         */
        public String getSearchEngineName() {
            return searchEngineName;
        }

        /**
         * @param searchEngineName the searchEngineName to set
         */
        public void setSearchEngineName(String searchEngineName) {
            this.searchEngineName = searchEngineName;
        }

        /**
         * @return the searchURL
         */
        public String getSearchURL() {
            return searchURL;
        }

        /**
         * @param searchURL the searchURL to set
         */
        public void setSearchURL(String searchURL) {
            this.searchURL = searchURL;
        }

        /**
         * @return the linkReqex
         */
        public String getLinkReqex() {
            return linkReqex;
        }

        /**
         * @param linkReqex the linkReqex to set
         */
        public void setLinkReqex(String linkReqex) {
            this.linkReqex = linkReqex;
        }
        
        
    }
