/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.run;

import java.util.Timer;
import java.util.TimerTask;
import webtools.net.SSLCertificates;

/**
 *
 * @author alibaba0507
 */
public class Main {

    public static void main(String[] args) {
        java.lang.System.setProperty("https.protocols", "TLSv1");
        SSLCertificates.ignoreCertificates();
        System.out.println(System.getProperty("https.protocols"));
        final SplashScreen splash = new SplashScreen("/images/article-spinning.jpg");
        splash.setVisible(true);
        long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (WebToolMainFrame.instance == null) {
                       Thread.sleep(500);
                    }
                    while (System.currentTimeMillis() - start < 5000)
                    {
                        Thread.sleep(500);
                    }
                    splash.close();
                    WebToolMainFrame.instance.setVisible(true);
                } catch (InterruptedException ex) {

                }

            }
        }).start();
        
        new WebToolMainFrame();
        
        
    }
}
