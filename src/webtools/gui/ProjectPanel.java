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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
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
    public ProjectPanel(String title) {
        super();
        this.title = title;
        setLayout(new FlowLayout());
        initTabs();
    }
    private void initTabs(){
        initSeachForm();
        tabs = new JTabbedPane();
        tabs.addTab("Settings", searchForm);
        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);
    }
    private void initSeachForm() {
      //  String[] labels = {"Name: ", "Search Query: ", "Depth: ", "Site Search: "
       //            ,"File Filter:","File Size > :","File Size  < :"};
      String[] labels = { "Project Name", "Search Query", "Link Depth", "Link has keyword"
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
        JButton submit = new JButton("Save Project",new ImageIcon(AWTUtils.getIcon(this, ".\\images\\Save24.gif")));
    Object list = WebToolMainFrame.defaultProjectProperties.get(this.title);
    if (list != null)
    {
        form.setFormValues((String[])list);
    }
    submit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          if (form.getText(0) == null || form.getText(0) == "")
          {
              JOptionPane.showMessageDialog(form, "Saved Fial.Please fill Project Name field.");
              return;
          }
        System.out.println(form.getText(0) + " " + form.getText(1) + ". " + form.getText(2)
            + ", age " + form.getText(3));
          java.awt.Container c = ProjectPanel.this.getParent();
          while (true)
          {
              if (c == null)
                  break;
              if (c instanceof JInternalFrame)
              {
                  ((JInternalFrame)c).setTitle(form.getText(0));
                  break;
              }
              c = c.getParent();
          }
          //((JInternalFrame)ProjectPanel.this.getParent().getParent()).setTitle(form.getText(0));
          WebToolMainFrame.defaultProjectProperties.put(form.getText(0), form.getFormValues());
          WebToolMainFrame.saveProjectPropToFile();
          // repload the tree
          WebToolMainFrame.instance.updateProjectTree();
      }
    });
   
    searchForm = new JPanel(new BorderLayout());
    searchForm.add(form, BorderLayout.NORTH);
    JPanel p = new JPanel();
    p.add(submit);
    searchForm.add(p, BorderLayout.SOUTH);
    }

}
