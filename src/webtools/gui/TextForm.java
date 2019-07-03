/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author alibaba0507
 */
public class TextForm extends JPanel {

    JTextField[] fields;
    JPanel controlPanel;
    String[] labels;
    char[] mnemonics;
    int[] widths;
    String[] tips;

    public TextForm() {

    }
    
    void  constractExtraPanels(JPanel c)
    {
        
    }
    private void constractPanels() {
        JPanel pane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
       // controlPanel.add(fieldPanel,BorderLayout.CENTER);
        
        add(labelPanel, BorderLayout.WEST);
        add(fieldPanel, BorderLayout.CENTER);
        fields = new JTextField[labels.length];
         
        for (int i = 0; i < labels.length; i += 1) {
            fields[i] = new JTextField();
            
            if (i < tips.length) {
                fields[i].setToolTipText(tips[i]);
            }
            if (i < widths.length) {
                fields[i].setColumns(widths[i]);
            }

            JLabel lab = new JLabel(labels[i], JLabel.RIGHT);
            lab.setLabelFor(fields[i]);
            if (i < mnemonics.length) {
                lab.setDisplayedMnemonic(mnemonics[i]);
            }

            labelPanel.add(lab);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.add(fields[i]);
            fieldPanel.add(p);
        }
        JPanel north = new JPanel(new BorderLayout());
        add(north,BorderLayout.NORTH);
        constractExtraPanels(north);
    }
    // Create a form with the specified labels, tooltips, and sizes.

    public TextForm(String[] labels, char[] mnemonics, int[] widths, String[] tips) {
        super(new BorderLayout());
        //controlPanel = new JPanel(new BorderLayout());
        this.labels = labels;
        this.mnemonics = mnemonics;
        this.widths = widths;
        this.tips = tips;
        
        constractPanels();
       

    }

    public void setFormValues(String[] s) {
        for (int i = 0; i < Math.min(fields.length, s.length); i++) {
            fields[i].setText(s[i]);
        }
    }

    public String[] getFormValues() {
        String[] s = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            s[i] = fields[i].getText();
        }
        return s;
    }

    public String getText(int i) {
        return (fields[i].getText());
    }
}
