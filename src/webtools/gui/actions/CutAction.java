/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.actions;

import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import webtools.gui.run.Main;
import za.co.utils.AWTUtils;

/**
 *
 * @author alibaba0507
 */
public class CutAction extends AbstractAction  {
     public CutAction() {
           // super("Cut", new ImageIcon(AWTUtils.getIcon(null, "/images/Cut24.gif")));
           String menuFile = Main.prop.getProperty("menu.file");
           try (FileReader reader = new FileReader(menuFile)) {
            //Read JSON file
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
            JSONObject jsonMenu = (JSONObject) obj;
            JSONArray jsnMenu =  (JSONArray) jsonMenu.get("actions");
            for (int i = 0;i < jsnMenu.size();i++)
            {
                JSONObject jsonAction = (JSONObject)jsnMenu.get(i);
                if (jsonAction.get("name") == "cut")
                {
                    this.putValue(NAME, "Cut");
                    String icn = (String) jsonAction.get("icon");
                    if ( icn != null)
                    {
                        putValue(SMALL_ICON, new ImageIcon(AWTUtils.getIcon(null, icn)));
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException jsEx) {
            jsEx.printStackTrace();
        }
      }

        public void actionPerformed(ActionEvent e) {
            // openProjectFile(null, "New Project");
            JOptionPane.showConfirmDialog(null, "Exit Not Implemented yet ...");
            //openFile(null);

        }
}
