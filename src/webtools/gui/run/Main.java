/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.gui.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import webtools.net.SSLCertificates;

/**
 *
 * @author alibaba0507
 */
public class Main {

    public static Properties prop = new Properties();

    public static void updateProperties() {
        try {
            File yourFile = new File("config.properties");
            yourFile.createNewFile(); // if file already exists will do nothing 

            prop.store(new FileOutputStream("config.properties"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (InputStream input = new FileInputStream("config.properties")) {

            Main.prop = new Properties();
            // load a properties file
            Main.prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        java.lang.System.setProperty(
                "https.protocols", "TLSv1");
        SSLCertificates.ignoreCertificates();

        System.out.println(System.getProperty("https.protocols"));
        String imageDir = Main.prop.getProperty("image.dir");
        String imageFile = imageDir + "article-spinning.jpg";
        final SplashScreen splash = new SplashScreen(imageFile);

        splash.setVisible(
                true);
        long start = System.currentTimeMillis();

        new Thread(
                new Runnable() {
            @Override
            public void run() {
                try {
                    while (WebToolMainFrame.instance == null) {
                        Thread.sleep(500);
                    }
                    while (System.currentTimeMillis() - start < 5000) {
                        Thread.sleep(500);
                    }
                    splash.close();
                    WebToolMainFrame.instance.setVisible(true);
                } catch (InterruptedException ex) {

                }

            }
        }
        ).start();

        new WebToolMainFrame();

    }
}
