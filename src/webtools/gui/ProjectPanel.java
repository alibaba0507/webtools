/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import webtools.gui.run.Main;

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
    private JTable tblRegexResult;
    private JTable tblKeywordsResult;
    private JTable tblSearchResult;
    private JTable tblPagesResult;
    private TextForm crawlForm;
    private int limitDoaminRecord = 100; // defauult
    private int lastSearchSort = 0;
    private int lastregexSort = 0;
    private int lastKeywordSort = 0;

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
        updateRegexTableModel();
    }

    public DefaultTableModel getSearchDomainTableModel() {
        return (DefaultTableModel) tblSearchResult.getModel();
    }

    public DefaultTableModel getSearchPageTableModel() {
        return (DefaultTableModel) tblPagesResult.getModel();
    }

    private void initRegexTab(JPanel pnlRegexResults) {
        pnlRegexResults.setLayout(new BorderLayout());
        JScrollPane searchTableScrool = new JScrollPane();
        searchTableScrool.setMaximumSize(new Dimension(650, 50));
        searchTableScrool.setPreferredSize(new Dimension(0, 50));

        JScrollPane searchKeywordsTableScrool = new JScrollPane();
        searchKeywordsTableScrool.setMaximumSize(new Dimension(650, 50));
        searchKeywordsTableScrool.setPreferredSize(new Dimension(0, 50));

        JSplitPane searchQuerySplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchTableScrool, searchKeywordsTableScrool);
        searchQuerySplit.setOneTouchExpandable(true);
        searchQuerySplit.setDividerLocation(150);

        JPanel pnlButtons = new JPanel();
        pnlRegexResults.add(searchQuerySplit, BorderLayout.CENTER);
        pnlRegexResults.add(pnlButtons, BorderLayout.SOUTH);
        JButton btnSaveRegex = new JButton("Save Regex To File", new ImageIcon(AWTUtils.getIcon(this, ".\\images\\Save24.gif")));
        pnlButtons.setLayout(new FlowLayout());
        pnlButtons.add(btnSaveRegex);
        btnSaveRegex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                // chooser.setCurrentDirectory(new File("/home/me/Documents"));
                int retrival = chooser.showSaveDialog(null);
                if (retrival == JFileChooser.APPROVE_OPTION) {
                    try (FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".txt")) {
                        SQLite db = SQLite.getInstance();
                        String[] s = crawlForm.getFormValues();
                        int id = db.findQueryId(s[1], s[3]);
                        if (id > 0) {
                           ArrayList<Vector> list = db.selectRegex(id,0);
                           for (int i = 0;i < list.size();i++)
                           {
                               Vector v = list.get(i);
                                fw.write((String)v.get(1) + "\n");
                           }
                        }
                        
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });

        JButton btnRefreshKeywords = new JButton("Refresh keyword table", new ImageIcon(AWTUtils.getIcon(this, ".\\images\\Redo24.gif")));
        //pnlButtons.setLayout(new FlowLayout());

        pnlButtons.add(btnRefreshKeywords);

        btnRefreshKeywords.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e
            ) {
                // JOptionPane.showMessageDialog(ProjectPanel.this, "Comming Soon ...");
                updateKeyWordModel();
            }
        }
        );

        tblKeywordsResult = new JTable();

        tblKeywordsResult.setModel(
                new javax.swing.table.DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                            "Most Used words by Sites",
                             "Count"
                        }
                ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        searchKeywordsTableScrool.setViewportView(tblKeywordsResult);

        JTableHeader headerKeywords = tblKeywordsResult.getTableHeader();
        headerKeywords.setUpdateTableInRealTime(true);
        // Sort of column when click on header ASC DESC
        headerKeywords.setReorderingAllowed(true);
        headerKeywords.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                TableColumnModel colModel = tblKeywordsResult.getColumnModel();
                int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
                if (columnModelIndex < 0 || columnModelIndex >= colModel.getColumnCount()) {
                    return;
                }
                int modelIndex = colModel.getColumn(columnModelIndex)
                        .getModelIndex();

                if (modelIndex < 0) {
                    return;
                }
                Vector v = ((DefaultTableModel) tblKeywordsResult.getModel()).getDataVector();
                Collections.sort(v,
                        new MyComparator(lastKeywordSort != 0, 1));
                lastKeywordSort = (lastKeywordSort == 0) ? 1 : 0;
                //Collections c = new PolicyUtils.Collections();
                String[] s = new String[]{
                    "id", "Regex Keywords"};
                Vector col = new Vector();
                col.insertElementAt(s[0], 0);
                col.insertElementAt(s[1], 1);
                ((DefaultTableModel) tblKeywordsResult.getModel()).setDataVector(v, col);
                //table.tableChanged(new TableModelEvent(MyTableModel.this));
                tblKeywordsResult.repaint();
            }
        });

        tblRegexResult = new JTable();
        tblRegexResult.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "id", "Regex Keywords"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        searchTableScrool.setViewportView(tblRegexResult);
        JTableHeader header = tblRegexResult.getTableHeader();
        header.setUpdateTableInRealTime(true);
        // Sort of column when click on header ASC DESC
        header.setReorderingAllowed(true);
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                TableColumnModel colModel = tblRegexResult.getColumnModel();
                int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
                if (columnModelIndex < 0 || columnModelIndex >= colModel.getColumnCount()) {
                    return;
                }
                int modelIndex = colModel.getColumn(columnModelIndex)
                        .getModelIndex();

                if (modelIndex < 0) {
                    return;
                }
                Vector v = ((DefaultTableModel) tblRegexResult.getModel()).getDataVector();
                Collections.sort(v,
                        new MyComparator(lastregexSort != 0, 0));
                lastregexSort = (lastregexSort == 0) ? 1 : 0;
                //Collections c = new PolicyUtils.Collections();
                String[] s = new String[]{
                    "id", "Regex Keywords"};
                Vector col = new Vector();
                col.insertElementAt(s[0], 0);
                col.insertElementAt(s[1], 1);
                ((DefaultTableModel) tblRegexResult.getModel()).setDataVector(v, col);
                //table.tableChanged(new TableModelEvent(MyTableModel.this));
                tblRegexResult.repaint();
            }
        });

    }

    private void initSearchTab(JPanel pnlSearchResult) {

        pnlSearchResult.setLayout(new BorderLayout());
        JScrollPane searchTableScrool = new JScrollPane();
        JScrollPane searchPagesTableScrool = new JScrollPane();
        searchTableScrool.setMaximumSize(new Dimension(350, 50));
        searchTableScrool.setPreferredSize(new Dimension(0, 50));

        searchPagesTableScrool.setMaximumSize(new Dimension(350, 50));
        searchPagesTableScrool.setPreferredSize(new Dimension(0, 50));

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
                if (columnModelIndex < 0 || columnModelIndex >= colModel.getColumnCount()) {
                    return;
                }
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
    }

    private void initTabs() {
        initSeachForm();
        tabs = new JTabbedPane();
        tabs.addTab("Settings", searchForm);

        JPanel pnlSearchResult = new JPanel();
        tabs.addTab("Crawl Results", pnlSearchResult);
        initSearchTab(pnlSearchResult);
        JPanel pnlRegexResults = new JPanel();
        tabs.addTab("Regex Results", pnlRegexResults);
        initRegexTab(pnlRegexResults);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);
        //  updateSearchTableModel();
    }

    public void updateKeyWordModel() {
        SQLite db = SQLite.getInstance();
        String[] s = crawlForm.getFormValues();
        int id = db.findQueryId(s[1], s[3]);
        if (id > 0) {
            Vector<Vector> list = db.selectKeywords(id);

            String[] cols = new String[]{
                "Most Used words by Sites", "Count"
            };
            Vector col = new Vector();
            col.insertElementAt(cols[0], 0);
            col.insertElementAt(cols[1], 1);
            ((DefaultTableModel) tblKeywordsResult.getModel()).setRowCount(0);
            ((DefaultTableModel) tblKeywordsResult.getModel()).setDataVector(list, col);
        }
    }

    public void updateRegexTableModel() {
        SQLite db = SQLite.getInstance();
        String[] s = crawlForm.getFormValues();
        int id = db.findQueryId(s[1], s[3]);
        if (id > 0) {
            Vector v = ((DefaultTableModel) tblRegexResult.getModel()).getDataVector();
            Collections.sort(v,
                    new MyComparator(false, 0));
            int indx = -1;
            if (v.size() > 0) {
                Vector row = (Vector) v.get(0);
                indx = ((Integer) row.get(0)).intValue();
            }
            ArrayList<Vector> list = db.selectRegex(id, indx);
            if (list.size() > 0) {
                DefaultTableModel m = (DefaultTableModel) tblRegexResult.getModel();
                for (int i = 0; i < list.size(); i++) {
                    //Vector row = list.get(i);
                    m.addRow(list.get(i));
                }
            }
        }
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
        char[] mnemonics = {'P', 'Q', 'E', 'U', 'L', 'X'};
        int[] widths = {15, 55, 15, 55, 55, 55};
        String[] descs = {"Project Name", "Search Engine query with | (or) inurl e.t.c",
            "Select Search Engine", "Search Engine URL",
            "Regex for parsing links", "Regex For Parsing Page"};
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
        char[] mnemonics = {'P', 'Q', 'E', 'U', 'L', 'R'};
        int[] widths = {15, 55, 15, 55, 55, 55};
        String[] descs = {"Project Name", "Search Engine query with | (or) inurl e.t.c",
            "Select Search Engine", "Search Engine URL",
            "Regex for parsing links",
            "Regex For Parsing Page"};
        crawlForm = new SearchForm(labels, mnemonics, widths, descs);
        submit = new JButton("Save Project", new ImageIcon(AWTUtils.getIcon(this, ".\\images\\Save24.gif")));
        final Object list = WebToolMainFrame.defaultProjectProperties.get(this.title);
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
                // we must check to c if search query change
                String query = "";
                String searchEngine = "";
                String regex = "";
                if (list != null) {
                    query = ((String[]) list)[1];
                    searchEngine = ((String[]) list)[3];
                    regex = ((String[]) list)[5];
                }

                if ((!query.equals("") && !query.equals(crawlForm.getText(1)))
                        || (!searchEngine.equals("") && !searchEngine.equals(crawlForm.getText(3)))) {
                    String newQuery = crawlForm.getText(1);
                    int qId = SQLite.getInstance().findQueryId(query, searchEngine);
                    
                    SQLite.getInstance().deleteQuery(qId);
                } else
                if (!regex.equals("") && !regex.equals(crawlForm.getText(5))) {
                    int qId = SQLite.getInstance().findQueryId(query, searchEngine);
                    SQLite.getInstance().deleteRegexQuery(qId);
                    SQLite.getInstance().deleteKeywordsQuery(qId);
                }

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
    private int sortIndx = 1;

    public MyComparator(boolean sortAsc, int sortIndx) {
        isSortAsc = sortAsc;
        this.sortIndx = sortIndx;
    }

    public MyComparator(boolean sortAsc) {
        isSortAsc = sortAsc;
    }

    public int compare(Object o1, Object o2) {
        //if (!(o1 instanceof Integer) || !(o2 instanceof Integer)) {
        //   return 0;
        // }
        Integer s1 = Integer.valueOf(((Vector) o1).elementAt(sortIndx).toString());
        Integer s2 = Integer.valueOf(((Vector) o2).elementAt(sortIndx).toString());
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
