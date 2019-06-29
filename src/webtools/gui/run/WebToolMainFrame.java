/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.run;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import webtools.gui.actions.ProjectsUI;
import za.co.utils.AWTUtils;

/**
 *
 * @author alibaba0507
 */
public class WebToolMainFrame extends JFrame {

    public static WebToolMainFrame instance;
    private final JDesktopPane desktop;
    private final JToolBar toolBar;
    private final JMenuBar menuBar;
    private JPopupMenu popup;
    private JPopupMenu popupProj;
    private DefaultListModel consolesListModel, projectListModel;

    public WebToolMainFrame() {
        super("WebTools");
        // do some extra work and then
        desktop = new JDesktopPane();
        toolBar = new JToolBar();
        menuBar = new JMenuBar();
        createMenuBar();
        setJMenuBar(menuBar);
        // Make dragging faster:
        desktop.putClientProperty("JDesktopPane.dragMode", "outline");

        JList consolesList = new JList();
        consolesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        consolesListModel = new DefaultListModel();

        JList projectList = new JList();
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectListModel = new DefaultListModel();

        try {
            consolesList.setModel(consolesListModel);
            projectList.setModel(projectListModel);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }

        ProjectsUI.consolesListModel = consolesListModel;
        ProjectsUI.projectListModel = projectListModel;
        
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
        
        Dimension dim = new Dimension(0, 0);
        
        
         JSplitPane listSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane/*commandPanel*/, null/*linePanel*/);
        listSplit.setDividerLocation(140);

        JSplitPane hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listSplit, desktop);
        hSplit.setOneTouchExpandable(true);
        hSplit.setDividerLocation(100);
        hSplit.setMinimumSize(dim);
        
         // Initialize default console window for displaying program output
        JTextArea console = new JTextArea();
        ProjectsUI.console = console;
        
        JPanel northPanel = new JPanel();

        northPanel.setLayout(new BorderLayout());
        northPanel.add(toolBar, BorderLayout.NORTH);
        Container contentPane = getContentPane();
        contentPane.add(northPanel, BorderLayout.NORTH);

        //This is last istantiation
        if (WebToolMainFrame.instance == null) {
            WebToolMainFrame.instance = this;
        }
    }

    private void createMenuBar() {

        JSONParser jsonParser = new JSONParser();
        String menuFile = Main.prop.getProperty("menu.file");
        try (FileReader reader = new FileReader(menuFile)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray jsonMenu = (JSONArray) obj;
            for (int i = 0; i < jsonMenu.size(); i++) {
                JSONObject jsnMenu = (JSONObject) ((JSONObject) jsonMenu.get(i)).get("menu");
                String menuName = (String) jsnMenu.get("name");
                if (menuName != null) {
                    JMenu m = new JMenu(menuName);
                    String key = (String) jsnMenu.get("key");
                    if (key != null) {
                        m.setMnemonic(key.charAt(0));
                    }
                    menuBar.add(m);
                    JSONArray jsonActions = (JSONArray) jsnMenu.get("actions");
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
            }
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
