/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.run;

import java.awt.Insets;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

    public WebToolMainFrame() {
        super("WebTools");
        // do some extra work and then
        desktop = new JDesktopPane();
        toolBar = new JToolBar();
        menuBar = new JMenuBar();
        createMenuBar();
        setJMenuBar(menuBar);

        if (WebToolMainFrame.instance == null) {
            WebToolMainFrame.instance = this;
        }
    }

    private void createMenuBar() {

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("../res/menu.json")) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray jsonMenu = (JSONArray) obj;
            for (int i = 0; i < jsonMenu.size(); i++) {
                JSONObject jsnMenu = ((JSONObject) jsonMenu.get(i));
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
                        String icn = (String)jsnAction.get("icon");
                        if (icn != null)
                        {
                             
                            a.putValue(Action.SMALL_ICON, new ImageIcon(AWTUtils.getIcon(null, icn)));
                        }
                        if (jsnAction.get("button") != null) {
                           JButton button = new JButton(a);
                            button.setText(null);
                            button.setToolTipText((String)a.getValue(Action.NAME));
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

        } catch (InstantiationException insEx) {

        } catch (ClassNotFoundException classEx) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException jsEx) {

        }
    }
}
