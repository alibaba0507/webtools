/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author alibaba0507
 */
public class ExitAction extends AbstractAction{

    @Override
    public void actionPerformed(ActionEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        JOptionPane.showMessageDialog((JComponent) e.getSource(), "TODO: Before Exit check if need TO SAVE DATA ...");
       System.exit(0);
    }
    
}
