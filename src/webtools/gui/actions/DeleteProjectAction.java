/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import webtools.gui.run.WebToolMainFrame;

/**
 *
 * @author alibaba0507
 */
public class DeleteProjectAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        int id = JOptionPane.showConfirmDialog(new JPanel()," Delete Selected project .");
        if (id == JOptionPane.YES_OPTION)
        { // do something
            JList projList = (JList)((JPopupMenu)((JMenuItem)e.getSource()).getParent()).getInvoker();
            Object obj = projList.getSelectedValue();
            WebToolMainFrame.defaultProjectProperties.remove(obj.toString());
            WebToolMainFrame.saveProjectPropToFile();
            WebToolMainFrame.instance.updateProjectTree();
            WebToolMainFrame.isDelete = true;
            JInternalFrame[] frames = WebToolMainFrame.getDesckTopInstance().getAllFrames();
            for (int i = 0;i < frames.length;i++)
            {
                if (frames[i].getTitle().equals(obj.toString()))
                {
                    frames[i].dispose();
                }
            }
            WebToolMainFrame.isDelete = false;
            System.out.println(" Deliton Project [" + ((JPopupMenu)((JMenuItem)e.getSource()).getParent()).getInvoker()+ "]");
        }
    }
    
}
