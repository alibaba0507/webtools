/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
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
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import webtools.gui.run.WebToolMainFrame;
import za.co.utils.AWTUtils;
import za.co.utils.SQLite;

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
    private TextForm crawlForm;
    private int limitDoaminRecord = 100; // defauult
    private int lastSearchSort = 0;

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
        updateSearchTableModel();
    }

    public DefaultTableModel getSearchDomainTableModel() {
        return (DefaultTableModel) tblSearchResult.getModel();
    }

    public DefaultTableModel getSearchPageTableModel() {
        return (DefaultTableModel) tblPagesResult.getModel();
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
        JTableHeader header = tblSearchResult.getTableHeader();
        header.setUpdateTableInRealTime(true);
        //header.addMouseListener(tblSearchResult.new ColumnListener(table));
        header.setReorderingAllowed(true);
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                TableColumnModel colModel = tblSearchResult.getColumnModel();
                int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
                if (columnModelIndex < 0 || columnModelIndex >= colModel.getColumnCount())
                    return;
                int modelIndex = colModel.getColumn(columnModelIndex)
                        .getModelIndex();

                if (modelIndex < 0) {
                    return;
                }
                Vector v = ((DefaultTableModel) tblSearchResult.getModel()).getDataVector();
                Collections.sort(v,
                        new MyComparator(lastSearchSort != 0));
                lastSearchSort = (lastSearchSort == 0) ? 1 : 0;
                //Collections c = new PolicyUtils.Collections();
                String[] s = new String[]{
                    "Domain", "Pages"};
                Vector col = new Vector();
                col.insertElementAt(s[0], 0);
                col.insertElementAt(s[1], 1);
                ((DefaultTableModel) tblSearchResult.getModel()).setDataVector(v, col);
                //table.tableChanged(new TableModelEvent(MyTableModel.this));
                tblSearchResult.repaint();
            }
        });

        tblSearchResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //     super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
                try {
                    if (e.getClickCount() == 2) {
                        // System.out.println("double clicked");
                        int row = tblSearchResult.getSelectedRow();
                        String domain = (String) ((DefaultTableModel) tblSearchResult.getModel()).getValueAt(row, 0);
                        SQLite db = SQLite.getInstance();
                        String[] s = crawlForm.getFormValues();
                        int id = db.findQueryId(s[1], s[3]);
                        if (id > 0) {
                            ArrayList<Vector> list = db.selectURLByDomain(id, domain);
                            DefaultTableModel m = (DefaultTableModel) tblPagesResult.getModel();
                            if (list.size() > 0) {
                                while (m.getRowCount() > 0) {
                                    m.removeRow(0);
                                }
                                for (int i = 0; i < list.size(); i++) {
                                    m.addRow(list.get(i));
                                }// end for
                            }// end if 
                        }// end if
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        tblPagesResult = new JTable();

        tblPagesResult.setModel(
                new javax.swing.table.DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                            "URL",
                            "Google Page Number",
                            "Crawl level",
                            "Parent ID"
                        }
                ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        }
        );
        tblPagesResult.getColumnModel().getColumn(0).setMinWidth(350);
        tblPagesResult.getColumnModel().getColumn(1).setMaxWidth(250);
        tblPagesResult.getColumnModel().getColumn(2).setMaxWidth(250);
        tblPagesResult.getColumnModel().getColumn(3).setMaxWidth(250);
        searchPagesTableScrool.setViewportView(tblPagesResult);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);
        //  updateSearchTableModel();
    }

    public void updateSearchTableModel() {

        SQLite db = SQLite.getInstance();

        String[] s = crawlForm.getFormValues();
        int id = db.findQueryId(s[1], s[3]);
        if (id > 0) {
            ArrayList<Vector> list = db.selectCoutDomains(id, limitDoaminRecord);
            DefaultTableModel m = (DefaultTableModel) tblSearchResult.getModel();
            if (list.size() != m.getRowCount()) {
                while (m.getRowCount() > 0) {
                    m.removeRow(0);
                }
                for (int i = 0; i < list.size(); i++) {
                    m.addRow(list.get(i));
                }
            } else {
                for (int i = 0; i < m.getRowCount(); i++) {
                    if (!m.getValueAt(i, 0).equals(list.get(i).elementAt(0))
                            || m.getValueAt(i, 1).equals(list.get(i).elementAt(1))) {
                        m.setValueAt(list.get(i).elementAt(0), i, 0);
                        m.setValueAt(list.get(i).elementAt(1), i, 1);
                    }
                }// end for
            }
        }
    }

    private void initCrawlTab() {
        String[] labels = {"Project Name", "Search Query", "Search Engine",
            "Search URL",
            "Link Regex",
            "Page Parser"};
        char[] mnemonics = {'P', 'Q', 'E', 'U', 'L','X'};
        int[] widths = {15, 55, 15, 55, 55,55};
        String[] descs = {"Project Name", "Search Engine query with | (or) inurl e.t.c",
            "Select Search Engine", "Search Engine URL",
            "Regex for parsing links","Regex For Parsing Page"};
        crawlForm = new SearchForm(labels, mnemonics, widths, descs);

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
            "Link Regex",
        "Page Parser"};
        char[] mnemonics = {'P', 'Q', 'E', 'U', 'L','R'};
        int[] widths = {15, 55, 15, 55, 55,55};
        String[] descs = {"Project Name", "Search Engine query with | (or) inurl e.t.c",
            "Select Search Engine", "Search Engine URL",
            "Regex for parsing links",
          "Regex For Parsing Page"};
        crawlForm = new SearchForm(labels, mnemonics, widths, descs);
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
        searchForm.add(p, BorderLayout.CENTER);

    }

}

class MyComparator implements Comparator {

    protected boolean isSortAsc;

    public MyComparator(boolean sortAsc) {
        isSortAsc = sortAsc;
    }

    public int compare(Object o1, Object o2) {
        //if (!(o1 instanceof Integer) || !(o2 instanceof Integer)) {
        //   return 0;
        // }
        Integer s1 = Integer.valueOf(((Vector) o1).elementAt(1).toString());
        Integer s2 = Integer.valueOf(((Vector) o2).elementAt(1).toString());
        int result = 0;
        result = s1.compareTo(s2);
        if (!isSortAsc) {
            result = -result;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MyComparator) {
            MyComparator compObj = (MyComparator) obj;
            return compObj.isSortAsc == isSortAsc;
        }
        return false;
    }
}
