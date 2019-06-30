/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author alibaba0507
 */
public class ConsoleListener implements ListSelectionListener{
      private JList consolesList;
        private ArrayList outputList;
        private JViewport viewport;

        public ConsoleListener(JList consolesList, ArrayList outputList, JViewport viewport) {
            super();
            this.consolesList = consolesList;
            this.outputList = outputList;
            this.viewport = viewport;
        }

        public void valueChanged(ListSelectionEvent e) {
            int i = consolesList.getSelectedIndex();
            if (i == -1) // Returns if list empty
            {
                return;
            }
            ArrayList al = (ArrayList) outputList.get(i);
            JTextArea console = (JTextArea) al.get(1);
            console.setCaretPosition(console.getText().length());
            ProjectsUI.updateStopAction(this.consolesList, outputList, viewport);

            // Update buttons of font style if radioButton is selected.
            /* if (radioButton.isSelected()) {

                Font font = console.getFont();
                String fontName = font.getName();
                int fontSize = font.getSize();
                if (font.isBold()) {
                    boldButton.setSelected(true);
                } else {
                    boldButton.setSelected(false);
                }
                if (font.isItalic()) {
                    italicButton.setSelected(true);
                } else {
                    italicButton.setSelected(false);
                }
                cbFonts.setSelectedItem(fontName);
                cbSizes.setSelectedItem(Integer.toString(fontSize));

            }  // if
             */
        }  // valueChanged
}
