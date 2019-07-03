/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.run;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import webtools.gui.actions.NewProjectAction;
import webtools.gui.actions.ProjectsUI;
import za.co.utils.AWTUtils;
import za.co.utils.SQLite;

/**
 *
 * @author alibaba0507
 */
public class WebToolMainFrame extends JFrame {
    
    public static WebToolMainFrame instance;
    public static SQLite sqlite;
    public static Hashtable defaultProjectProperties;
    
    private static JDesktopPane desktop;
    private JToolBar toolBar;
    private JMenuBar menuBar;
    private DefaultListModel consolesListModel, projectListModel;
    private JList projectList;
    
    public static JDesktopPane getDesckTopInstance() {
        if (desktop == null) {
            new WebToolMainFrame();
        }
        
        return desktop;
    }
    
    public WebToolMainFrame() {
        super("WebTools");
        desktop = new JDesktopPane();
        toolBar = new JToolBar();
        menuBar = new JMenuBar();
        createMenuBar();
        setJMenuBar(menuBar);
        
        projectList = new JList();
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectListModel = new DefaultListModel();
        projectList.setModel(projectListModel);
        projectList.setCellRenderer(new ListEntryCellRenderer());
        projectList.addMouseListener(new MouseAdapter() {
             @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                     SwingUtilities.invokeLater(new Runnable() {
                          @Override
                        public void run() {
                            Cursor cursor = projectList.getParent().getCursor();
                            projectList.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                              //System.out.println(">>>> Selected Item [" + projectList.getSelectedValue().toString() + "] >>>>>");
                              new NewProjectAction().openProjectFile(null, projectList.getSelectedValue().toString());
                             projectList.getParent().setCursor(cursor);
                             
                        }
                     });
                }
            }
            
        });
        // JScrollPane commandScrollPane = new JScrollPane(consolesList);
        JScrollPane projectsScrollPane = new JScrollPane(projectList);
        
        JPanel projectPanel = new JPanel(new BorderLayout());
        JLabel projectLabel = new JLabel("Projects:");
        projectLabel.setIcon(new ImageIcon(AWTUtils.getIcon(desktop, Main.prop.getProperty("project.list.image"))));
        
        projectLabel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        projectPanel.add(projectLabel, BorderLayout.NORTH);
        projectPanel.add(projectsScrollPane, BorderLayout.CENTER);
        
        MnemonicTabbedPane tabbedPane = new MnemonicTabbedPane();
        String[] tabs = {"Projects", "Console"};
        char[] ms = {'P', 'C',};
        int[] keys = {KeyEvent.VK_0, KeyEvent.VK_1};
        tabbedPane.addTab(tabs[0], projectPanel);
        tabbedPane.setMnemonicAt(0, ms[0]);
        
        JSplitPane listSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane/*commandPanel*/, null/*linePanel*/);
        listSplit.setDividerLocation(140);
        
        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listSplit, desktop);
        hSplit.setOneTouchExpandable(true);
        hSplit.setDividerLocation(100);
        hSplit.setMinimumSize(new Dimension(0, 0));
        
        JTextArea console = new JTextArea();
        
        JScrollPane jsp = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JViewport viewport = jsp.getViewport();
        
        JPanel consolePanel = new JPanel(new BorderLayout());
        consolePanel.add(jsp, BorderLayout.CENTER);
        consolePanel.setMinimumSize(new Dimension(0, 0));
        
        JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, hSplit, consolePanel);
        vSplit.setOneTouchExpandable(true);
        JPanel northPanel = new JPanel();
        
        northPanel.setLayout(new BorderLayout());
        northPanel.add(toolBar, BorderLayout.NORTH);
        Container contentPane = getContentPane();
        contentPane.add(northPanel, BorderLayout.NORTH);
        contentPane.add(vSplit, BorderLayout.CENTER);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height - 50);
        loadProjectPropFromFile();
        updateProjectTree();
        WebToolMainFrame.sqlite = new SQLite();
        
        //This is last istantiation
        if (WebToolMainFrame.instance == null) {
            WebToolMainFrame.instance = this;
        }
    }
    
    public void updateProjectTree() {
        projectListModel.clear();
        WebToolMainFrame.defaultProjectProperties.keySet().forEach(e -> {
            
            projectListModel.addElement(new ListEntry((String) e,
                    new ImageIcon(AWTUtils.getIcon(desktop,
                            Main.prop.getProperty("project.item.image")))));
        });
        projectList.setSelectedIndex(projectListModel.size() - 1);
        
    }
    
    public static void loadProjectPropFromFile() {
        String propDir = Main.prop.getProperty("project.properties.dir");
        File file = new File(propDir + "defaultProperties");
        if (!file.exists()) {
            WebToolMainFrame.defaultProjectProperties = new Hashtable();
            try {
                file.createNewFile();
                saveProjectPropToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileInputStream fis = new FileInputStream(propDir + "defaultProperties");
                ObjectInputStream ois = new ObjectInputStream(fis);
                WebToolMainFrame.defaultProjectProperties = (Hashtable) ois.readObject();
                ois.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void saveProjectPropToFile() {
        try {
            String propDir = Main.prop.getProperty("project.properties.dir");
            FileOutputStream fos = new FileOutputStream(propDir + "defaultProperties");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(WebToolMainFrame.defaultProjectProperties);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMenuBar() {
        
        JSONParser jsonParser = new JSONParser();
        String menuFile = Main.prop.getProperty("menu.file");
        try (FileReader reader = new FileReader(menuFile)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject jsonMenu = (JSONObject) obj;
            // for (int i = 0; i < jsonMenu.size(); i++) {
            //JSONObject jsnMenu = (JSONObject) ((JSONObject) jsonMenu.get(i)).get("menu");
            JSONArray jsnMenu = (JSONArray) jsonMenu.get("menu");
            for (int i = 0; i < jsnMenu.size(); i++) {
                JSONObject menuJSON = (JSONObject) jsnMenu.get(i);
                String menuName = (String) menuJSON.get("name");
                if (menuName != null) {
                    JMenu m = new JMenu(menuName);
                    String key = (String) menuJSON.get("key");
                    if (key != null) {
                        m.setMnemonic(key.charAt(0));
                    }
                    menuBar.add(m);
                    JSONArray jsonActions = (JSONArray) menuJSON.get("actions");
                    for (int j = 0; j < jsonActions.size(); j++) {
                        JSONObject jsnAction = ((JSONObject) jsonActions.get(j));
                        String className = (String) jsnAction.get("class");
                        Class<?> clazz = Class.forName(className);
                        AbstractAction a = (AbstractAction) clazz.newInstance();
                        JMenuItem item = m.add(a);
                        String icn = (String) jsnAction.get("icon");
                        if (icn != null) {
                            
                            a.putValue(Action.SMALL_ICON, new ImageIcon(AWTUtils.getIcon(null, icn)));
                        }
                        String actionName = (String) jsnAction.get("name");
                        if (actionName != null) {
                            a.putValue(Action.NAME, actionName);
                        }
                        if (jsnAction.get("button") != null) {
                            JButton button = new JButton(a);
                            button.setText(null);
                            button.setToolTipText((String) a.getValue(Action.NAME));
                            button.setRequestFocusEnabled(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            toolBar.add(button);
                        }
                        key = (String) jsnAction.get("key");
                        if (key != null) {
                            item.setMnemonic(key.charAt(0));
                        }
                    }
                }
            }// end for
        } catch (IllegalAccessException ex1) {
            ex1.printStackTrace();
        } catch (InstantiationException insEx) {
            insEx.printStackTrace();
        } catch (ClassNotFoundException classEx) {
            classEx.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException jsEx) {
            jsEx.printStackTrace();
        }
    }
    
    class ListEntry {
        
        private String value;
        private ImageIcon icon;
        
        public ListEntry(String value, ImageIcon icon) {
            this.value = value;
            this.icon = icon;
        }
        
        public String getValue() {
            return value;
        }
        
        public ImageIcon getIcon() {
            return icon;
        }
        
        public String toString() {
            return value;
        }
    }

    class ListEntryCellRenderer
            extends JLabel implements ListCellRenderer<Object> {

        private JLabel label;
        
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected,
                boolean cellHasFocus) {
            ListEntry entry = (ListEntry) value;
            
            setText(value.toString());
            setIcon(entry.getIcon());
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            
            return this;
        }
    }

    class MnemonicTabbedPane extends JTabbedPane {
        
        Hashtable mnemonics = null;
        
        int condition;
        
        public MnemonicTabbedPane() {
            setUI(new MnemonicTabbedPaneUI());
            mnemonics = new Hashtable();

            // I don't know which one is more suitable for mnemonic action.
            //setMnemonicCondition(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            setMnemonicCondition(WHEN_IN_FOCUSED_WINDOW);
        }
        
        public void setMnemonicAt(int index, char c) {
            int key = (int) c;
            if ('a' <= key && key <= 'z') {
                key -= ('a' - 'A');
            }
            setMnemonicAt(index, key);
        }
        
        public void setMnemonicAt(int index, int keyCode) {
            ActionListener action = new MnemonicAction(index);
            KeyStroke stroke = KeyStroke
                    .getKeyStroke(keyCode, ActionEvent.ALT_MASK);
            registerKeyboardAction(action, stroke, condition);
            mnemonics.put(new Integer(index), new Integer(keyCode));
        }
        
        public int getMnemonicAt(int index) {
            int keyCode = 0;
            Integer m = (Integer) mnemonics.get(new Integer(index));
            if (m != null) {
                keyCode = m.intValue();
            }
            return keyCode;
        }
        
        public void setMnemonicCondition(int condition) {
            this.condition = condition;
        }
        
        public int getMnemonicCondition() {
            return condition;
        }
        
        class MnemonicAction implements ActionListener {
            
            int index;
            
            public MnemonicAction(int index) {
                this.index = index;
            }
            
            public void actionPerformed(ActionEvent e) {
                MnemonicTabbedPane tabbedPane = (MnemonicTabbedPane) e.getSource();
                tabbedPane.setSelectedIndex(index);
                tabbedPane.requestFocus();
            }
        }
        
        class MnemonicTabbedPaneUI extends MetalTabbedPaneUI {
            
            protected void paintText(Graphics g, int tabPlacement, Font font,
                    FontMetrics metrics, int tabIndex, String title,
                    Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                MnemonicTabbedPane mtabPane = (MnemonicTabbedPane) tabPane;
                if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                    g.setColor(tabPane.getForegroundAt(tabIndex));
                    BasicGraphicsUtils.drawString(g, title, mtabPane
                            .getMnemonicAt(tabIndex), textRect.x, textRect.y
                            + metrics.getAscent());
                } else {
                    g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
                    BasicGraphicsUtils.drawString(g, title, mtabPane
                            .getMnemonicAt(tabIndex), textRect.x, textRect.y
                            + metrics.getAscent());
                    g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
                    BasicGraphicsUtils.drawString(g, title, mtabPane
                            .getMnemonicAt(tabIndex), textRect.x - 1, textRect.y
                            + metrics.getAscent() - 1);
                }
            }
        }
    }
}
