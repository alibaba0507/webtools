/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import editor.Notepad;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import webtools.gui.run.Main;
import za.co.utils.AWTUtils;

/**
 *
 * @author Sh4D0W
 */
public class MyFileEditorInternalFrame extends JInternalFrame{
    public MyFileEditorInternalFrame()
    {
        
    }
     /**
     * @return the project
     */
    public Notepad getNotepad() {
        return notepad;
    }

    /**
     * @param project the project to set
     */
    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }
    private Notepad notepad;
    public MyFileEditorInternalFrame( String title, boolean a, boolean b, boolean c, boolean d) {
          super(title, a, b, c, d);
          ImageIcon icon =  new ImageIcon(AWTUtils.getIcon(this,
                            Main.prop.getProperty("file.editor.item.image")));
          this.setFrameIcon(icon);
    }
}




