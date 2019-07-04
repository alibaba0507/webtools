package webtools.gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sun.javafx.application.PlatformImpl;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * SwingFXWebView
 */
public class SwingFXWebView extends JPanel {

    private WebView webView;
    private WebEngine webEngine;

    public SwingFXWebView() {
        initComponents();
    }

    public static void main(String... args) {
        // Run this later:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("BEFORE UPDATE");
                final JFrame frame = new JFrame();
                SwingFXWebView v = new SwingFXWebView();
                frame.getContentPane().add(v);

                frame.setMinimumSize(new Dimension(640, 480));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                System.out.println("BEFORE LOAD ....");
                v.loadHTMLContent("<div><b>Some content to load</b></div>");
            }
        });
    }

    public void loadHTMLContent(final String html) {

        System.out.println("BEFORE LOAD HTML CONTENT  ...." );

        Platform.runLater(() -> {
            // initFX(fxPanel);
            System.out.print("BEFORE WEBVIEW NEW TEXT");
            while (webView == null) {
                // Object nextElement = en.nextElement();
                try {
                    Thread.sleep(200);
                    Thread.yield();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
          //  webView.setVisible(false);
            webView.getEngine().loadContent(html);
           // webView.setVisible(true);
            System.out.print("AFTER WEBVIEW NEW TEXT");
        });

    }

    private void initComponents() {
        setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel() {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 480);
            }
        };
        add(fxPanel, BorderLayout.CENTER);
        Platform.runLater(() -> {
            initFX(fxPanel);
        });

    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    /**
     * createScene
     *
     * Note: Key is that Scene needs to be created and run on "FX user thread"
     * NOT on the AWT-EventQueue Thread
     *
     */
    private Scene createScene() {
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        webView = new WebView();
        // hide webview scrollbars whenever they appear.
        webView.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> change) {
                Set<Node> deadSeaScrolls = webView.lookupAll(".scroll-bar");
                for (Node scroll : deadSeaScrolls) {
                    scroll.setVisible(false);

                }
            }
        });
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("noborder-scroll-pane");
        scrollPane.setStyle("-fx-background-color: white");
        scrollPane.setContent(webView);
        scrollPane.setFitToWidth(true);
        //scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setPrefWidth(620);
        webEngine = webView.getEngine();
        //webEngine.load("http://www.google.com");
        webEngine.loadContent("<div><b>This is test</b></div>");
        root.getChildren().add(scrollPane);
        return scene;
    }
}
