/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.dialogs;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import webtools.gui.run.Main;
import za.co.utils.AWTUtils;

/**
 *
 * @author alibaba0507
 */
public class SearchQueryDialog extends javax.swing.JDialog {

    private JTextField txt;

    /**
     * Creates new form SearchQueryDialog
     */
    public SearchQueryDialog(java.awt.Frame parent, boolean modal, JTextField txt, JTextField txtRegex) {
        super(parent, modal);
        //this.setUndecorated(true);
        // this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE) ;

        this.txt = txt;
        initComponents();
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                SearchQueryDialog.this.dispose();
            }
        });
        btnClose1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                SearchQueryDialog.this.dispose();
            }
        });
        txtDexcr.setEditable(false);
        //txtDexcr.setEnabled(false);
        txtDexcr.setLineWrap(true);

        txtDexcr1.setEditable(false);
        txtDexcr1.setLineWrap(true);
        //txtDexcr.setFont(f);
        txtExample.setEditable(false);
        //txtExample.setEnabled(false);
        txtExample.setLineWrap(true);
        if (txt != null) {
            txtSearchString.setText(txt.getText());
        }

        txtExample1.setEditable(false);
        //txtExample.setEnabled(false);
        txtExample1.setLineWrap(true);
        if (txtRegex != null) {
            txtSearchString1.setText(txtRegex.getText());
        }

        btnUse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(txtSearchString.getText());
                SearchQueryDialog.this.dispose();
            }
        });

        btnUse1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtRegex.setText(txtSearchString1.getText());
                SearchQueryDialog.this.dispose();
            }
        });

        loadSearchJson();
        loadRegexJson();

    }

    private void loadRegexJson() {
        JSONParser jsonParser = new JSONParser();
        String menuFile = Main.prop.getProperty("search.regex.keywords");
        //try (FileReader reader = new FileReader(menuFile)) {
        try (Reader reader = new InputStreamReader(AWTUtils.getResourceAsStream(menuFile))) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonKeywords = (JSONObject) obj;
            JSONArray jsnSearch = (JSONArray) jsonKeywords.get("search");
            for (int i = 0;

            i< jsnSearch.size ();
            i

            
                ++) {
                JSONObject menuJSON = (JSONObject) jsnSearch.get(i);
                CboItem itm = new CboItem((String) menuJSON.get("keyword"),
                        (String) menuJSON.get("descr"),
                        (String) menuJSON.get("example"));
                cboKeywords1.addItem(itm);
                cboKeywords1.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            CboItem itm = (CboItem) cboKeywords1.getSelectedItem();
                            //String s = txtSearchString1.getText();
                            //if (s.indexOf(" " + itm.toString() + " ") < 0) {
                            //   s += " " + itm.toString() + " ";
                            txtSearchString1.setText(itm.toString());
                            //}
                            txtDexcr1.setText(itm.getDescr());
                            txtExample1.setText(itm.getExamp());

                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    

    private void loadSearchJson() {
        JSONParser jsonParser = new JSONParser();
        String menuFile = Main.prop.getProperty("search.query.keywords");
        //try (FileReader reader = new FileReader(menuFile)) {
        try(Reader reader = new InputStreamReader(AWTUtils.getResourceAsStream(menuFile))){
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonKeywords = (JSONObject) obj;
            JSONArray jsnSearch = (JSONArray) jsonKeywords.get("search");
            for (int i = 0; i < jsnSearch.size(); i++) {
                JSONObject menuJSON = (JSONObject) jsnSearch.get(i);
                CboItem itm = new CboItem((String) menuJSON.get("keyword"),
                        (String) menuJSON.get("descr"),
                        (String) menuJSON.get("example"));
                cboKeywords.addItem(itm);
                cboKeywords.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            CboItem itm = (CboItem) cboKeywords.getSelectedItem();
                            String s = txtSearchString.getText();
                            if (s.indexOf(" " + itm.toString() + " ") < 0) {
                                s += " " + itm.toString() + " ";
                                txtSearchString.setText(s);
                            }
                            txtDexcr.setText(itm.getDescr());
                            txtExample.setText(itm.getExamp());

                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboKeywords = new javax.swing.JComboBox<>();
        txtSearchString = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDexcr = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        btnUse = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtExample = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cboKeywords1 = new javax.swing.JComboBox<>();
        txtSearchString1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDexcr1 = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        btnUse1 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtExample1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        btnClose1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setText("Select Query Keyword");

        cboKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKeywordsActionPerformed(evt);
            }
        });

        txtSearchString.setToolTipText("Search Query String");

        txtDexcr.setColumns(20);
        txtDexcr.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtDexcr.setRows(5);
        jScrollPane1.setViewportView(txtDexcr);

        jLabel2.setText("Example");

        btnUse.setText("Use Only");

        txtExample.setColumns(20);
        txtExample.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtExample.setRows(5);
        jScrollPane3.setViewportView(txtExample);

        jLabel4.setText("Description");

        btnClose.setText("Close");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchString)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cboKeywords, 0, 315, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose)
                        .addGap(41, 41, 41)
                        .addComponent(btnUse, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboKeywords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(txtSearchString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUse)
                    .addComponent(btnClose))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Search Query Constractor", jPanel1);

        jLabel3.setText("Select Regex Keyword");

        cboKeywords1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboKeywords1ActionPerformed(evt);
            }
        });

        txtSearchString1.setToolTipText("Search Query String");

        txtDexcr1.setColumns(20);
        txtDexcr1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtDexcr1.setRows(5);
        jScrollPane2.setViewportView(txtDexcr1);

        jLabel5.setText("Example");

        btnUse1.setText("Use Only");

        txtExample1.setColumns(20);
        txtExample1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        txtExample1.setRows(5);
        jScrollPane4.setViewportView(txtExample1);

        jLabel6.setText("Description");

        btnClose1.setText("Close");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchString1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboKeywords1, 0, 314, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnClose1)
                        .addGap(41, 41, 41)
                        .addComponent(btnUse1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cboKeywords1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(txtSearchString1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUse1)
                    .addComponent(btnClose1))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Search Query Constractor", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Search Regex Constractor");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKeywordsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKeywordsActionPerformed

    private void cboKeywords1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboKeywords1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboKeywords1ActionPerformed

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
                final SearchQueryDialog dialog = new SearchQueryDialog(new javax.swing.JFrame(), true, null, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        //System.exit(0);
                        dialog.dispose();
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    class CboItem {

        private String key, descr, examp;

        public CboItem(String key, String descr, String example) {
            this.key = key;
            this.descr = descr;
            this.examp = example;
        }

        @Override
        public String toString() {
            return this.getKey(); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key the key to set
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * @return the descr
         */
        public String getDescr() {
            return descr;
        }

        /**
         * @param descr the descr to set
         */
        public void setDescr(String descr) {
            this.descr = descr;
        }

        /**
         * @return the examp
         */
        public String getExamp() {
            return examp;
        }

        /**
         * @param examp the examp to set
         */
        public void setExamp(String examp) {
            this.examp = examp;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnClose1;
    private javax.swing.JButton btnUse;
    private javax.swing.JButton btnUse1;
    private javax.swing.JComboBox<CboItem> cboKeywords;
    private javax.swing.JComboBox<CboItem> cboKeywords1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea txtDexcr;
    private javax.swing.JTextArea txtDexcr1;
    private javax.swing.JTextArea txtExample;
    private javax.swing.JTextArea txtExample1;
    private javax.swing.JTextField txtSearchString;
    private javax.swing.JTextField txtSearchString1;
    // End of variables declaration//GEN-END:variables

}
