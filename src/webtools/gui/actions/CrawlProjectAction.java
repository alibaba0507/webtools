/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import webtools.net.WebCrawler;

/**
 *
 * @author alibaba0507
 */
public class CrawlProjectAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        JList projList = (JList) ((JPopupMenu) ((JMenuItem) e.getSource()).getParent()).getInvoker();
        Object obj = projList.getSelectedValue();
        if (!WebCrawler.threadCrawlers.contains(obj.toString()))
        {
            SwingUtilities.invokeLater(new WebCrawler((obj.toString()),null));
            
        }
    }

}
