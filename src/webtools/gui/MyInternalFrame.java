/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import javax.swing.JInternalFrame;

/**
 *
 * @author alibaba0507
 */
public class MyInternalFrame extends JInternalFrame {

    /**
     * @return the project
     */
    public ProjectPanel getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectPanel project) {
        this.project = project;
    }
    private ProjectPanel project;
    public MyInternalFrame( String title, boolean a, boolean b, boolean c, boolean d) {
          super(title, a, b, c, d);
    }
}
