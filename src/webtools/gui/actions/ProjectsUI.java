/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JViewport;

/**
 *
 * @author alibaba0507
 */
public class ProjectsUI {
    public static DefaultListModel consolesListModel, projectListModel;
    public static JTextArea console;
     public static void updateStopAction(JList consolesList, ArrayList outputList, JViewport viewport) {
        int i = consolesList.getSelectedIndex();
        if (i != -1) {
            ArrayList list = (ArrayList) outputList.get(i);
            JTextArea console = (JTextArea) list.get(1);
            viewport.setView(console);
            /*if (list.get(2) == null) {
                stopAction.setEnabled(false);
            } else {
                stopAction.setEnabled(true);
            }
           */
        }
    }
}
