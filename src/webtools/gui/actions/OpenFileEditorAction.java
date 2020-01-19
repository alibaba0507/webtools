/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import editor.Notepad;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import webtools.gui.MyFileEditorInternalFrame;
import webtools.gui.MyInternalFrame;
import webtools.gui.run.WebToolMainFrame;

/**
 *
 * @author Sh4D0W
 */
public class OpenFileEditorAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        openFileEditor(null, "New File Editor");
    }

    public void openFileEditor(Object object, String title) {
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
            MyFileEditorInternalFrame jif = new MyFileEditorInternalFrame(title, true, true, true, true);
            Notepad np = new Notepad(title, jif);
            jif.setNotepad(np);
            
             JScrollPane scroller = new JScrollPane(np, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
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
        } catch (Exception ex) {
            ex.printStackTrace();
            Font fnt = WebToolMainFrame.instance.getConsole().getFont();

            WebToolMainFrame.instance.getConsole().setFont(fnt.deriveFont(Font.BOLD));
            WebToolMainFrame.instance.getConsole().setForeground(Color.RED);
            WebToolMainFrame.instance.getConsole().append(">>>> ERROR OPEN PROJECT "
                    + ex.getMessage() + " >>>>>\n");
            WebToolMainFrame.instance.getConsole().setForeground(Color.BLACK);
        }
    }

}




