/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import webtools.gui.run.WebToolMainFrame;
import za.co.utils.AWTUtils;

/**
 *
 * @author alibaba0507
 */
public class ProjectPanel extends JPanel {

    private JPanel searchForm;
    private JTabbedPane tabs;
    private String title;
    private JButton submit;
    private JTable tblSearchResult;
    private JTable tblPagesResult;

    //private JInternalFrame jif;
    public ProjectPanel(String title, JInternalFrame jif) {
        super();
        this.title = title;
        setLayout(new FlowLayout());
        initTabs();
        jif.addPropertyChangeListener(JInternalFrame.IS_CLOSED_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                if (evt.getNewValue() == Boolean.TRUE) {
                    // System.out.println(">>>>> JINTERNAL FRAME CLOSING >>>> " + evt.getOldValue() + " ] NEW [" + evt.getNewValue() + "] >>>>>>>>>>");
                    if (!WebToolMainFrame.isDelete) {
                        submit.doClick();
                    }
                }
            }
        });
    }
    public DefaultTableModel getSearchDomainTableModel()
    {
        return (DefaultTableModel)tblSearchResult.getModel();
    }
    
     public DefaultTableModel getSearchPageTableModel()
    {
        return (DefaultTableModel)tblPagesResult.getModel();
    }
    
    private void initTabs() {
        initSeachForm();
        tabs = new JTabbedPane();
        tabs.addTab("Settings", searchForm);

        JPanel pnlSearchResult = new JPanel();
        tabs.addTab("Crawl Results", pnlSearchResult);
        pnlSearchResult.setLayout(new BorderLayout());
        JScrollPane searchTableScrool = new JScrollPane();
        JScrollPane searchPagesTableScrool = new JScrollPane();
        JSplitPane searchQuerySplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchTableScrool, searchPagesTableScrool);
        searchQuerySplit.setOneTouchExpandable(true);
        searchQuerySplit.setDividerLocation(150);
        pnlSearchResult.add(searchQuerySplit, BorderLayout.CENTER);
        tblSearchResult = new JTable();
        tblSearchResult.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Domain", "Pages"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblSearchResult.getColumnModel().getColumn(0).setMinWidth(350);
        tblSearchResult.getColumnModel().getColumn(1).setMaxWidth(150);
        searchTableScrool.setViewportView(tblSearchResult);

        tblPagesResult = new JTable();
        tblPagesResult.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "URL", "Google Page Number"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblPagesResult.getColumnModel().getColumn(0).setMinWidth(350);
        tblPagesResult.getColumnModel().getColumn(1).setMaxWidth(250);
        searchPagesTableScrool.setViewportView(tblPagesResult);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);

    }

    private void initCrawlTab() {
        String[] labels = {"Project Name", "Search Query", "Search Engine",
            "Search URL",
            "Link Regex"};
        char[] mnemonics = {'P', 'Q', 'E', 'U', 'L'};
        int[] widths = {15, 55, 15, 55, 55};
        String[] descs = {"Project Name", "Search Engine query with | (or) inurl e.t.c",
            "Select Search Engine", "Search Engine URL",
            "Regex for parsing links"};
        final TextForm crawlForm = new SearchForm(labels, mnemonics, widths, descs);

    }

    private void initSeachForm() {
        //  String[] labels = {"Name: ", "Search Query: ", "Depth: ", "Site Search: "
        //            ,"File Filter:","File Size > :","File Size  < :"};
        /*   String[] labels = { "Project Name", "Search Query", "Link Depth", "Link has keyword"
                        ,"Link File Ext Only(jpg e.t.c)"
                         ,"File Bigger(kb)","File Smaller(kb)"};
    char[] mnemonics = { 'P', 'Q', 'L', 'K','E','B','S' };
    int[] widths = { 15, 15, 3, 15,5,3,3 };
    String[] descs = { "Project Name", "Search Engine query with | (or) inurl e.t.c"
                 , "Search Depth of extracted Links"
                , "(Optional) Search for keyword inside Links"
                ,"(Optional) will extract only this file Extention"
                ,"(Optional) Bigest File","(Optional)Smalest File" };
   final TextForm form = new TextForm(labels, mnemonics, widths, descs);
         */
        String[] labels = {"Project Name", "Search Query", "Search Engine",
            "Search URL",
            "Link Regex"};
        char[] mnemonics = {'P', 'Q', 'E', 'U', 'L'};
        int[] widths = {15, 55, 15, 55, 55};
        String[] descs = {"Project Name", "Search Engine query with | (or) inurl e.t.c",
            "Select Search Engine", "Search Engine URL",
            "Regex for parsing links"};
        final TextForm crawlForm = new SearchForm(labels, mnemonics, widths, descs);
        submit = new JButton("Save Project", new ImageIcon(AWTUtils.getIcon(this, ".\\images\\Save24.gif")));
        Object list = WebToolMainFrame.defaultProjectProperties.get(this.title);
        if (list != null) {
            crawlForm.setFormValues((String[]) list);
        }
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (crawlForm.getText(0) == null || crawlForm.getText(0) == "") {
                    JOptionPane.showMessageDialog(crawlForm, "Saved Fial.Please fill Project Name field.");
                    return;
                }
                System.out.println(crawlForm.getText(0) + " " + crawlForm.getText(1) + ". " + crawlForm.getText(2)
                        + ", age " + crawlForm.getText(3));
                java.awt.Container c = ProjectPanel.this.getParent();
                while (true) {
                    if (c == null) {
                        break;
                    }
                    if (c instanceof JInternalFrame) {
                        ((JInternalFrame) c).setTitle(crawlForm.getText(0));
                        break;
                    }
                    c = c.getParent();
                }
                //((JInternalFrame)ProjectPanel.this.getParent().getParent()).setTitle(form.getText(0));
                WebToolMainFrame.defaultProjectProperties.put(crawlForm.getText(0), crawlForm.getFormValues());
                WebToolMainFrame.saveProjectPropToFile();
                // repload the tree
                WebToolMainFrame.instance.updateProjectTree();
            }
        });

        searchForm = new JPanel(new BorderLayout());
        searchForm.add(crawlForm, BorderLayout.NORTH);
        JPanel p = new JPanel();
        p.add(submit);
        searchForm.add(p, BorderLayout.SOUTH);
    }

}
