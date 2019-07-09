/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import webtools.gui.run.WebToolMainFrame;

/**
 * This will be the link between WebToolMainFrame 
 * components like ProjectPanel and its internal components 
 * like tables and so on.
 * @author alibaba0507
 */
public class GUIController {
    
    /**
     * Search WebToolMainFrame for JInternalFrame with 
     * given title , and find TableModel of search Domain table,
     * the other table is All link associated with this domain
     * @param windowsTitle - title of the search frame
     * @return DefaultTableModel is any or null with columns 
     * String[]{"Domain", "Pages"}
     */
    public static DefaultTableModel getDomainSearchTableModel(String windowsTitle) {
        JInternalFrame frame = null;
        try {
            JInternalFrame[] frames = WebToolMainFrame.getDesckTopInstance().getAllFrames();
            for (int i = 0; i < frames.length; i++) {
                if (windowsTitle != null && frames[i].getTitle().equals(windowsTitle)) {
                    frame = frames[i];
                    break;
                }
            }
            if (frame != null)
            {
                MyInternalFrame jif = (MyInternalFrame)frame;
                return jif.getProject().getSearchDomainTableModel();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
