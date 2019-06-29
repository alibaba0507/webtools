/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.run;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.json.JSONArray;
import org.json.simple.parser.JSONParser;

 

/**
 *
 * @author alibaba0507
 */
public class WebToolMainFrame extends JFrame{
    public static WebToolMainFrame instance;
    private final JDesktopPane desktop;
    private final JToolBar toolBar;
    private final JMenuBar menuBar;
    private JPopupMenu popup;
    private JPopupMenu popupProj;
    public WebToolMainFrame(){
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
         
        try (FileReader reader = new FileReader("employees.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray jsonMenu = (JSONArray) obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }
}
