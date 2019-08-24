/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import webtools.gui.dialogs.NetworkSetingDialog;
import webtools.gui.run.WebToolMainFrame;

/**
 *
 * @author alibaba0507
 */
public class NettworkSettingsAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NetworkSetingDialog dialog = new NetworkSetingDialog(WebToolMainFrame.instance, true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        //System.exit(0);
                        dialog.setVisible(true);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

}
