/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import webtools.gui.dialogs.SearchQueryDialog;
import webtools.gui.run.Main;
import webtools.gui.run.WebToolMainFrame;
import webtools.utils.ConstInterface;
import webtools.utils.SearchParams;
import za.co.utils.AWTUtils;

/**
 *
 * @author alibaba0507
 */
public class SearchForm extends TextForm {

    private JComboBox cSearch;

    public SearchForm(String[] labels, char[] mnemonics, int[] widths, String[] tips) {
        super(labels, mnemonics, widths, tips);
    }

    @Override
    void constractExtraPanels(JPanel c) {
        //super.constractExtraPanels(c); //To change body of generated methods, choose Tools | Templates.
        cSearch = new JComboBox();
        cSearch.addItem(new SearchParams("", "", ""));
        cSearch.addItem(ConstInterface.GOOGLE_SEARCH);
        cSearch.addItem(ConstInterface.GOOGLE_SEARCH_UK);
        cSearch.addItem(ConstInterface.GOOGLE_SEARCH_AU);
        cSearch.addItem(ConstInterface.GOOGLE_SEARCH_NZ);
        cSearch.addItem(ConstInterface.GOOGLE_SEARCH_DE);
        cSearch.addItem(ConstInterface.GOOGLE_SEARCH_SA);
        cSearch.addItem(ConstInterface.BING_SEARCH);
        cSearch.addItem(ConstInterface.DUCKDUCK_GO_SEARCH);
        cSearch.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    SearchParams s = (SearchParams) cSearch.getSelectedItem();
                    if (s.getSearchEngineName() != "") {
                        fields[2].setText(s.getSearchEngineName());
                        fields[3].setText(s.getSearchURL());
                        fields[4].setText(s.getLinkReqex());
                    }
                }

            }
        });

        JLabel lab = new JLabel("Default Search Engines", JLabel.RIGHT);
        lab.setLabelFor(cSearch);
        JPanel pnlSearchEngines = new JPanel(/*new GridLayout(2,2)*/);
        pnlSearchEngines.add(lab);
        pnlSearchEngines.add(cSearch);

        //c.add(buttonQueryPanel,BorderLayout.EAST);
        JPanel pnlSearchQuery = new JPanel(/*new GridLayout(2,2)*/);
        JLabel btnLab = new JLabel("Constract Search Query:", JLabel.RIGHT);
        JButton btnQuery = new JButton(new ImageIcon(AWTUtils.getIcon(this, Main.prop.getProperty("project.searchQuery.button.image"))));
        btnQuery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //  SearchQueryDialog dlg = new SearchQueryDialog(WebToolMainFrame.instance, true, fields[1]);
                // dlg.pack();
                // dlg.show();
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        final SearchQueryDialog dialog = new SearchQueryDialog(WebToolMainFrame.instance, true, fields[1],fields[5]);
                        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosing(java.awt.event.WindowEvent e) {
                                //  System.exit(0);
                                dialog.dispose();
                            }
                        });
                        ImageIcon icon = new ImageIcon(AWTUtils.getIcon(SearchForm.this,
                                Main.prop.getProperty("project.item.image")));
                        ((JFrame) dialog.getOwner()).setIconImage(AWTUtils.getIcon(SearchForm.this,
                                Main.prop.getProperty("project.item.image")));
                        dialog.setVisible(true);
                    }
                });

            }
        });
        pnlSearchQuery.add(btnLab);
        pnlSearchQuery.add(btnQuery);
        JPanel pnl = new JPanel(new GridLayout(2, 1));
        pnl.add(pnlSearchEngines);
        pnl.add(pnlSearchQuery);
        c.add(pnl, BorderLayout.WEST);

    }

}
