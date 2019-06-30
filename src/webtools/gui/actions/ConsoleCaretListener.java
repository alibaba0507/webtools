/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author alibaba0507
 */
public class ConsoleCaretListener implements CaretListener{
    public javax.swing.Action  cutAction, copyAction, pasteAction;
    JTextArea textArea;

        public ConsoleCaretListener(JTextArea textArea) {
            this.textArea = textArea;
            //  ProjectsUI.console = this.textArea;
        }

        public void caretUpdate(CaretEvent e) {
            cutAndCopy(textArea);
        }
        
        
        public void cutAndCopy(JTextArea textArea) {
        try {
            String s = textArea.getSelectedText();
            if (cutAction == null) {
                return;
            }
            if (s == null) {
                cutAction.setEnabled(false);
                copyAction.setEnabled(false);
            } else {
                cutAction.setEnabled(true);
                copyAction.setEnabled(true);
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
    }
}
