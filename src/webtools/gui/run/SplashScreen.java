package webtools.gui.run;

// Author: Samuel Huang, 14/01/2002

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import javax.swing.border.LineBorder;
import za.co.utils.AWTUtils;

class SplashScreen extends JWindow
{ 
    public SplashScreen(String filename)
    {
       // InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
       // URL u =  this.getClass().getResource(filename);
       // JLabel l = new JLabel(new ImageIcon(filename));
        JLabel l = new JLabel(AWTUtils.getResourceAsIcon(filename));
        Color gold = new Color( 232, 232, 77 );
        l.setBorder( new LineBorder( gold, 5 ) );
        getContentPane().add( l, BorderLayout.CENTER );
        pack();
        Dimension screenSize =
           Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
    }
    
    public void close() {
       setVisible( false );
    	 dispose();
    }
    
}


