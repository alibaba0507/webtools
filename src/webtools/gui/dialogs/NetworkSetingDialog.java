/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.dialogs;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import webtools.gui.run.Main;
import webtools.gui.run.WebToolMainFrame;
import webtools.net.TorSocket;
import za.co.utils.AWTUtils;

/**
 *
 * @author alibaba0507
 */
public class NetworkSetingDialog extends javax.swing.JDialog {

    private JTextField txt;

    /**
     * Creates new form SearchQueryDialog
     */
    public NetworkSetingDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        initComponents();
//        boolean radioButtonSelected = false;
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtPort.getText().trim().equals("") && 
                            !AWTUtils.isInteger(txtPort.getText()))
                {
                    JOptionPane.showMessageDialog(NetworkSetingDialog.this, "Please enter valid port number between 1 and 99999, Or leave it blank");
                    return;
                }
                  if (!txtTorPort.getText().trim().equals("") && 
                            !AWTUtils.isInteger(txtPort.getText()))
                {
                    JOptionPane.showMessageDialog(NetworkSetingDialog.this, "Please enter valid \bTOR port number between 1 and 99999, Or leave it blank");
                    return;
                }
                if (WebToolMainFrame.defaultProjectProperties != null) {
                    if (rbUseProxy.isSelected()) {
                        WebToolMainFrame.defaultProjectProperties.put("proxy.type", "HTTPS");
                    }
                    if (rbUseTor.isSelected()) {
                        WebToolMainFrame.defaultProjectProperties.put("proxy.type", "TOR");
                    }
                    WebToolMainFrame.defaultProjectProperties.put("proxy.host", txtHost.getText());
                    WebToolMainFrame.defaultProjectProperties.put("proxy.port", txtPort.getText());
                    WebToolMainFrame.defaultProjectProperties.put("proxy.user", txtUser.getText());
                    WebToolMainFrame.defaultProjectProperties.put("proxy.pasw", new String(txtPassw.getPassword()));

                    WebToolMainFrame.defaultProjectProperties.put("proxy.tor.host", txtTorHost.getText());
                    WebToolMainFrame.defaultProjectProperties.put("proxy.tor.port", txtTorPort.getText());
                    WebToolMainFrame.saveProjectPropToFile();
                }
                NetworkSetingDialog.this.dispose();
            }
        }
        );
        Enumeration<AbstractButton> e = buttonGroup1.getElements();
        while (e.hasMoreElements()) {
            AbstractButton b = e.nextElement();
            if (b.getText().equals(rbUseProxy.getText())) {

                b.setSelected(true);
                updateProxy(true);
                break;
                //  buttonGroup1.setSelected(m, modal);
            }
        }// end while
        if (WebToolMainFrame.defaultProjectProperties != null
                && WebToolMainFrame.defaultProjectProperties.get("proxy.type") != null) {
            String proxyType = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.type");
            if (proxyType.equals("HTTPS")) {
                String proxyHost = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.host");
                String proxyPort = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.port");
                String proxyUser = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.user");
                String proxyPsw = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.pasw");
                if (proxyHost != null) {
                    txtHost.setText(proxyHost);
                }
                if (proxyPort != null) {
                    txtPort.setText(proxyPort);
                }
                if (proxyUser != null) {
                    txtUser.setText(proxyUser);
                }
                if (proxyPsw != null) {
                    txtPassw.setText(proxyPsw);
                }
            }
            if (proxyType.equals("TOR")) {
                String proxyHost = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.tor.host");
                String proxyPort = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.tor.port");
                if (proxyHost != null) {
                    txtTorHost.setText(proxyHost);
                }
                if (proxyPort != null) {
                    txtTorPort.setText(proxyPort);
                }
                e = buttonGroup1.getElements();
                while (e.hasMoreElements()) {
                    AbstractButton b = e.nextElement();
                    if (b.getText().equals(rbUseTor.getText())) {
                        //radioButtonSelected = true;
                        b.setSelected(true);
                        updateProxy(false);
                        break;
                        //  buttonGroup1.setSelected(m, modal);
                    }
                }// end while
            }// end if

        }

        rbUseProxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProxy(true);

            }
        });
        rbUseTor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProxy(false);

            }
        });
        htmlPane.setEditable(false);
        htmlPane.setOpaque(false);
        htmlPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    // System.out.println(hle.getURL());
                    openWebpage(hle.getURL());
                }
            }
        });

        btnTestTor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                try {
                    if (txtTorHost.getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(NetworkSetingDialog.this, "Please Enter Host Name");
                        return;
                    }
                    if (!AWTUtils.isInteger(txtTorPort.getText())) {
                        JOptionPane.showMessageDialog(NetworkSetingDialog.this, "Please Enter Port Number");
                        return;
                    } else {
                        int i = Integer.parseInt(txtTorPort.getText());
                        if (i < 1 || i > 99999) {
                            JOptionPane.showMessageDialog(NetworkSetingDialog.this,
                                    "Invalid Port Number . Valid between 1 and 99999");
                            return;
                        }
                    }

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                TorSocket tor = new TorSocket(txtTorHost.getText(), Integer.parseInt(txtTorPort.getText()));
                                URL u = new URL("https://www.google.com/search?q=my+ip");

                                final String html;

                                html = tor.connect(u, null);

                                final TorTestPage dialog = new TorTestPage(new javax.swing.JFrame(), true, html);
                                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                                    @Override
                                    public void windowClosing(java.awt.event.WindowEvent e) {
                                      //  System.exit(0);
                                      dialog.dispose();
                                    }
                                });
                                dialog.setVisible(true);
                                dialog.repaint();
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(NetworkSetingDialog.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(NetworkSetingDialog.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(NetworkSetingDialog.this,
                            "Error [" + ex.getMessage() + "]");
                }

            }
        });

    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            if (url == null) {
                return false;
            }
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateProxy(boolean isEnabled) {
        txtHost.setEnabled(isEnabled);
        txtPassw.setEnabled(isEnabled);
        txtPort.setEnabled(isEnabled);
        txtUser.setEnabled(isEnabled);

        txtTorHost.setEnabled(!isEnabled);
        txtTorPort.setEnabled(!isEnabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        rbUseProxy = new javax.swing.JRadioButton();
        txtPassw = new javax.swing.JPasswordField();
        rbUseTor = new javax.swing.JRadioButton();
        btnOk = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtTorHost = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtTorPort = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        htmlPane = new javax.swing.JEditorPane();
        btnTestTor = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel3.setText("Host");

        jLabel6.setText("Port");

        txtPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPortActionPerformed(evt);
            }
        });

        jLabel7.setText("User Name");

        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        jLabel8.setText("Password");

        buttonGroup1.add(rbUseProxy);
        rbUseProxy.setText("Use Proxy");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbUseProxy)
                            .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassw)
                            .addComponent(txtUser))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(rbUseProxy)
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtPassw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        buttonGroup1.add(rbUseTor);
        rbUseTor.setText("Use Tor Proxy");

        btnOk.setText("Ok");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        jLabel4.setText("Host");

        jLabel9.setText("Port");

        txtTorPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTorPortActionPerformed(evt);
            }
        });

        htmlPane.setContentType("text/html"); // NOI18N
        htmlPane.setText("<html><b>To use Tor Network is very easy</b>.You must install only Tor Browser can be downloaded from <a href=\"https://www.torproject.org/download/\">here</a>\n<br>Once Tor Browser is downloaded Lunch the Browser. Usually Tor runs on port 9050 , but to make sure open windows comand  promt and type netstat , \n<br> you must find ports like 9050 or/and 9051 or/amd 9053 , 9153 , you have to test it when input host ussully \"127.0.0.1\" and port No and clik <b>Test Tor</b> Button\n</html>.");
        jScrollPane1.setViewportView(htmlPane);

        btnTestTor.setText("Test Tor");
        btnTestTor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTestTorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(rbUseTor)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnOk)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTorPort, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnTestTor))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTorHost, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(rbUseTor)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTorHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTorPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTestTor))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOk)
                .addGap(19, 19, 19))
        );

        jTabbedPane1.addTab("Search Query Constractor", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPortActionPerformed

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtTorPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTorPortActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTorPortActionPerformed

    private void btnTestTorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTestTorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTestTorActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SearchQueryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SearchQueryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SearchQueryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SearchQueryDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NetworkSetingDialog dialog = new NetworkSetingDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnTestTor;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JEditorPane htmlPane;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton rbUseProxy;
    private javax.swing.JRadioButton rbUseTor;
    private javax.swing.JTextField txtHost;
    private javax.swing.JPasswordField txtPassw;
    private javax.swing.JTextField txtPort;
    private javax.swing.JTextField txtTorHost;
    private javax.swing.JTextField txtTorPort;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables

}
