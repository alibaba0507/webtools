/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import webtools.gui.MyInternalFrame;
import webtools.gui.ProjectPanel;
import webtools.gui.run.WebToolMainFrame;

/**
 *
 * @author alibaba0507
 */
public class NewProjectAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        openProjectFile(null, "New Project");
    }

    public void openProjectFile(final File file, String title) {
        JInternalFrame frame = null;
        try {
            JInternalFrame[] frames = WebToolMainFrame.getDesckTopInstance().getAllFrames();

            for (int i = 0; i < frames.length; i++) {
                if (title != null && frames[i].getTitle().equals(title)) {
                    frame = frames[i];
                    frame.moveToFront();
                    frame.setMaximum(true);
                    frame.setSelected(true);
                    frame.moveToFront();
                    frame.repaint();
                    // System.out.print(">>>>> DISPLAY FRAME [" + title + "] >>>>>");
                    return;
                }
            }
            MyInternalFrame jif = new MyInternalFrame(title, true, true, true, true);

            ProjectPanel proj = new ProjectPanel(title, jif);
            jif.setProject(proj);
            JScrollPane scroller = new JScrollPane(proj, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            jif.setContentPane(scroller);
            jif.setSize(400, 250);	// A necessary statement	
            jif.setVisible(true);
            // jif.addVetoableChangeListener(new ActionsUI().new CloseListener(jif, proj));
            jif.moveToFront();

            jif.setSelected(true);
            // jif.setMaximum(true);     
            WebToolMainFrame.getDesckTopInstance().add(jif);
            jif.setMaximum(true);
            jif.repaint();

            //return jif;
        } catch (Exception pve) {
            pve.printStackTrace();
            Font fnt = WebToolMainFrame.instance.getConsole().getFont();
            
            WebToolMainFrame.instance.getConsole().setFont(fnt.deriveFont(Font.BOLD));
            WebToolMainFrame.instance.getConsole().setForeground(Color.RED);
            WebToolMainFrame.instance.getConsole().append(">>>> ERROR OPEN PROJECT " 
                        + pve.getMessage() + " >>>>>\n");
            WebToolMainFrame.instance.getConsole().setForeground(Color.BLACK);
        }

    }
}

