package editor;

/*
 * @(#)Notepad.java	1.31 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of soJFileurce code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

 /*
 * @(#)Notepad.java	1.31 05/11/17
 */
import cue.lang.Counter;
import cue.lang.NGramIterator;
import cue.lang.SentenceIterator;
import cue.lang.WordIterator;
import cue.lang.stop.StopWords;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.*;
import webtools.gui.run.WebToolMainFrame;

/**
 * Sample application using the simple text editor component that supports only
 * one font.
 *
 * @author Timothy Prinzing
 * @version 1.31 11/17/05
 */
public class Notepad extends JPanel {

    private static ResourceBundle resources;
    private final static String EXIT_AFTER_PAINT = new String("-exit");
    private static boolean exitAfterFirstPaint;
    private String title;
    private JButton submit;
    Preferences prefs = Preferences.userRoot().node(getClass().getName());
    String LAST_USED_FOLDER = System.getProperty("user.home");

    static {
        try {
            resources = ResourceBundle.getBundle("resources.Notepad",
                    Locale.getDefault());
        } catch (MissingResourceException mre) {
            System.err.println("resources/Notepad.properties not found");
            System.exit(1);
        }
    }

    public void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (exitAfterFirstPaint) {
            System.exit(0);
        }
    }

    public Notepad(String title, JInternalFrame jif) {
        this();
        this.title = title;
        jif.addPropertyChangeListener(JInternalFrame.IS_CLOSED_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                if (evt.getNewValue() == Boolean.TRUE) {
                    // System.out.println(">>>>> JINTERNAL FRAME CLOSING >>>> " + evt.getOldValue() + " ] NEW [" + evt.getNewValue() + "] >>>>>>>>>>");
                    if (!WebToolMainFrame.isDelete) {
                        submit.doClick();
                    }
                }
            }
        });

    }

    Notepad() {
        super(true);

        // Force SwingSet to come up in the Cross Platform L&F
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            // If you want the System L&F instead, comment out the above line and
            // uncomment the following:
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Error loading L&F: " + exc);
        }

        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());

        // create the embedded JTextComponent
        editor = createEditor();
        // Add this as a listener for undoable edits.
        editor.getDocument().addUndoableEditListener(undoHandler);

        // install the command table
        commands = new Hashtable();
        Action[] actions = getActions();
        for (int i = 0; i < actions.length; i++) {
            Action a = actions[i];
            //commands.put(a.getText(Action.NAME), a);
            commands.put(a.getValue(Action.NAME), a);
        }

        JScrollPane scroller = new JScrollPane();
        JViewport port = scroller.getViewport();
        port.add(editor);

        JPanel pnlEditor = new JPanel();
        pnlEditor.setLayout(new BorderLayout());
        //nlEditor.add("Center", port);
        JScrollPane scrollerList = new JScrollPane();
        JViewport porList = scrollerList.getViewport();

        list = new JList();
        list.setModel(new DefaultListModel());
        porList.add(list);
        pnlEditor.add("North", scrollerList);
        pnlEditor.add("Center", scroller);

        try {
            String vpFlag = resources.getString("ViewportBackingStore");
            Boolean bs = Boolean.valueOf(vpFlag);
            port.setBackingStoreEnabled(bs.booleanValue());
        } catch (MissingResourceException mre) {
            // just use the viewport default
        }

        syntaxEditor = createEditor();
        JScrollPane scrollerSyntax = new JScrollPane();
        JViewport portSyntax = scrollerSyntax.getViewport();
        portSyntax.add(syntaxEditor);
       /* try {
            String vpFlag = resources.getString("ViewportBackingStore");
            Boolean bs = Boolean.valueOf(vpFlag);
            portSyntax.setBackingStoreEnabled(bs.booleanValue());
        } catch (MissingResourceException mre) {
            // just use the viewport default
        }
       */
        menuItems = new Hashtable();
        JPanel panelEditor = new JPanel();
        panelEditor.setLayout(new BorderLayout());
        panelEditor.add("North", createToolbar());
        //panelEditor.add("Center", scroller);
        panelEditor.add("Center", pnlEditor);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        splitPane.setTopComponent(panelEditor);

        JPanel panelSyntax = new JPanel();
        panelSyntax.setLayout(new BorderLayout());

        panelSyntax.add("Center", scrollerSyntax);

        splitPane.setBottomComponent(panelSyntax);
        JPanel panelSyntaxActions = new JPanel();
        panelSyntaxActions.setLayout(new BoxLayout(panelSyntaxActions, BoxLayout.X_AXIS));
        panelSyntax.add("North", panelSyntaxActions);

        panelSyntaxActions.add(new JButton(createActionSpinSentances()));
        // createActionSpinWords3
        panelSyntaxActions.add(new JButton(createActionSpinWords3()));
        panelSyntaxActions.add(new JButton(createActionSpinWords4()));
        panelSyntaxActions.add(new JButton(createActionSpinWords5()));
       // add("North", panelEditor);
        add("Center", splitPane);
        add("South", createStatusbar());
        splitPane.setResizeWeight(0.5);
        //splitPane.setDividerLocation(50);
    }

    Action createActionSpinSentances() {
        return new AbstractAction("Spin Sentences") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = editor.getText();
                if (s.trim().length() <= 0) {
                    JOptionPane.showMessageDialog(Notepad.this, "Nothing To Spin", "Empty Text", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                Map m = spinSencanses();
                Iterator it = m.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    String val = m.get(key).toString();
                    s = s.replaceAll(" " + key + " ", " " + val + " ");
                    s = s.replaceAll("\n" + key + " ", "\n" + val + " ");
                    s = s.replaceAll("\r" + key + " ", "\r" + val + " ");
                    s = s.replaceAll("," + key + " ", "," + val + " ");
                    s = s.replaceAll("." + key + " ", "." + val + " ");
                    s = s.replaceAll(" " + key + ",", " " + val + ",");
                    s = s.replaceAll(" " + key + ".", " " + val + ".");
                    syntaxEditor.setText(s);
                }
            }
        };
    }

    Map<String, String> spinSencanses() {
        Map m = new HashMap();
        //String syntax = getResourceString("syntax");// getResourceString("syntax");
        // InputStreamReader inr = getResourceAsStream("syntax");
        // BufferedReader br = getResourceAsBuffer("../" + syntax);
        String s = editor.getText();
        StopWords sw = StopWords.English;
        for (final String word : new SentenceIterator(s, Locale.ENGLISH)) {
            //System.out.println(word);

            for (final String w : new WordIterator(word)) {
                //System.out.println(word);
                boolean found = false;
                if (m.containsKey(w)) {
                    continue; // we have this one 
                }
                if (w.trim().length() >= 4 && !sw.isStopWord(w)) {
                    try {
                        // inr.mark(0);
                        //inr.reset();
                        BufferedReader br = new BufferedReader(getResourceAsStream("syntax"));
                        String readLine = "";
                        while ((readLine = br.readLine()) != null) {
                            if (readLine.indexOf("," + w + ",") > -1) { // we found 
                                m.put(w, "{" + readLine.replaceAll(",", "|") + "}");
                                found = true;
                                //br.close();
                                break;
                            }
                        }// end while
                        br.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }// end if
                if (found) {
                    break;
                }
            }// end for 
        }// end for
        return m;
    }

    Action createActionSpinWords3() {
        return new AbstractAction("Spin Words > 3 chars") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    Action createActionSpinWords4() {
        return new AbstractAction("Spin Words > 4 chars") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    Action createActionSpinWords5() {
        return new AbstractAction("Spin Words > 5 chars") {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    public static void main(String[] args) {
        try {
            String vers = System.getProperty("java.version");
            if (vers.compareTo("1.1.2") < 0) {
                System.out.println("!!!WARNING: Swing must be run with a "
                        + "1.1.2 or higher version VM!!!");
            }
            if (args.length > 0 && args[0].equals(EXIT_AFTER_PAINT)) {
                exitAfterFirstPaint = true;
            }
            JFrame frame = new JFrame();
            frame.setTitle(resources.getString("Title"));
            frame.setBackground(Color.lightGray);
            frame.getContentPane().setLayout(new BorderLayout());
            Notepad notepad = new Notepad();
            frame.getContentPane().add("Center", notepad);
            frame.setJMenuBar(notepad.createMenubar());
            frame.addWindowListener(new AppCloser());
            frame.pack();
            frame.setSize(500, 600);
            frame.show();
        } catch (Throwable t) {
            System.out.println("uncaught exception: " + t);
            t.printStackTrace();
        }
    }

    /**
     * Fetch the list of actions supported by this editor. It is implemented to
     * return the list of actions supported by the embedded JTextComponent
     * augmented with the actions defined locally.
     */
    public Action[] getActions() {
        return TextAction.augmentList(editor.getActions(), defaultActions);
    }

    /**
     * Create an editor to represent the given document.
     */
    protected JTextComponent createEditor() {
        JTextComponent c = new JTextArea();
        c.setDragEnabled(true);
        c.setFont(new Font("monospaced", Font.PLAIN, 12));
        return c;
    }

    /**
     * Fetch the editor contained in this panel
     */
    protected JTextComponent getEditor() {
        return editor;
    }

    /**
     * To shutdown when run as an application. This is a fairly lame
     * implementation. A more self-respecting implementation would at least
     * check to see if a save was needed.
     */
    protected static final class AppCloser extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    /**
     * Find the hosting frame, for the file-chooser dialog.
     */
    protected Frame getFrame() {
        for (Container p = getParent(); p != null; p = p.getParent()) {
            if (p instanceof Frame) {
                return (Frame) p;
            }
        }
        return null;
    }

    /**
     * This is the hook through which all menu items are created. It registers
     * the result with the menuitem hashtable so that it can be fetched with
     * getMenuItem().
     *
     * @see #getMenuItem
     */
    protected JMenuItem createMenuItem(String cmd) {
        JMenuItem mi = new JMenuItem(getResourceString(cmd + labelSuffix));
        URL url = getResource(cmd + imageSuffix);
        if (url != null) {
            mi.setHorizontalTextPosition(JButton.RIGHT);
            mi.setIcon(new ImageIcon(url));
        }
        String astr = getResourceString(cmd + actionSuffix);
        if (astr == null) {
            astr = cmd;
        }
        mi.setActionCommand(astr);
        Action a = getAction(astr);
        if (a != null) {
            mi.addActionListener(a);
            a.addPropertyChangeListener(createActionChangeListener(mi));
            mi.setEnabled(a.isEnabled());
        } else {
            mi.setEnabled(false);
        }
        menuItems.put(cmd, mi);
        return mi;
    }

    /**
     * Fetch the menu item that was created for the given command.
     *
     * @param cmd Name of the action.
     * @returns item created for the given command or null if one wasn't
     * created.
     */
    protected JMenuItem getMenuItem(String cmd) {
        return (JMenuItem) menuItems.get(cmd);
    }

    protected Action getAction(String cmd) {
        return (Action) commands.get(cmd);
    }

    protected String getResourceString(String nm) {
        String str;
        try {
            str = resources.getString(nm);
        } catch (MissingResourceException mre) {
            str = null;
        }
        return str;
    }

    protected InputStreamReader getResourceAsStream(String key) {
        String name = getResourceString(key);
        if (name != null) {
            return new InputStreamReader(
                    this.getClass().getResourceAsStream("../" + name));

            //BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        }
        return null;
    }

    protected BufferedReader getResourceAsBuffer(String key) {
        InputStreamReader in = getResourceAsStream(key);
        if (in != null) {
            return new BufferedReader(in);//new InputStreamReader(in));
        }
        return null;
    }

    protected URL getResource(String key) {
        String name = getResourceString(key);
        if (name != null) {
            URL url = this.getClass().getResource("../" + name);
            return url;
        }
        return null;
    }

    protected Container getToolbar() {
        return toolbar;
    }

    protected JMenuBar getMenubar() {
        return menubar;
    }

    /**
     * Create a status bar
     */
    protected Component createStatusbar() {
        // need to do something reasonable here
        status = new StatusBar();
        return status;
    }

    /**
     * Resets the undo manager.
     */
    protected void resetUndoManager() {
        undo.discardAllEdits();
        undoAction.update();
        redoAction.update();
    }

    /**
     * Create the toolbar. By default this reads the resource file for the
     * definition of the toolbar.
     */
    private Component createToolbar() {
        toolbar = new JToolBar();
        String[] toolKeys = tokenize(getResourceString("toolbar"));
        for (int i = 0; i < toolKeys.length; i++) {
            if (toolKeys[i].equals("-")) {
                toolbar.add(Box.createHorizontalStrut(5));
            } else {
                toolbar.add(createTool(toolKeys[i]));
            }
        }
        toolbar.add(Box.createHorizontalGlue());
        return toolbar;
    }

    /**
     * Hook through which every toolbar item is created.
     */
    protected Component createTool(String key) {
        return createToolbarButton(key);
    }

    /**
     * Create a button to go inside of the toolbar. By default this will load an
     * image resource. The image filename is relative to the classpath
     * (including the '.' directory if its a part of the classpath), and may
     * either be in a JAR file or a separate file.
     *
     * @param key The key in the resource file to serve as the basis of lookups.
     */
    protected JButton createToolbarButton(String key) {
        URL url = getResource(key + imageSuffix);
        JButton b = new JButton(new ImageIcon(url)) {
            public float getAlignmentY() {
                return 0.5f;
            }
        };
        b.setRequestFocusEnabled(false);
        b.setMargin(new Insets(1, 1, 1, 1));

        String astr = getResourceString(key + actionSuffix);
        if (astr == null) {
            astr = key;
        }
        Action a = getAction(astr);
        if (a != null) {
            b.setActionCommand(astr);
            b.addActionListener(a);
        } else {
            b.setEnabled(false);
        }

        String tip = getResourceString(key + tipSuffix);
        if (tip != null) {
            b.setToolTipText(tip);
        }

        return b;
    }

    /**
     * Take the given string and chop it up into a series of strings on
     * whitespace boundaries. This is useful for trying to get an array of
     * strings out of the resource file.
     */
    protected String[] tokenize(String input) {
        Vector v = new Vector();
        StringTokenizer t = new StringTokenizer(input);
        String cmd[];

        while (t.hasMoreTokens()) {
            v.addElement(t.nextToken());
        }
        cmd = new String[v.size()];
        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = (String) v.elementAt(i);
        }

        return cmd;
    }

    /**
     * Create the menubar for the app. By default this pulls the definition of
     * the menu from the associated resource file.
     */
    protected JMenuBar createMenubar() {
        JMenuItem mi;
        JMenuBar mb = new JMenuBar();

        String[] menuKeys = tokenize(getResourceString("menubar"));
        for (int i = 0; i < menuKeys.length; i++) {
            JMenu m = createMenu(menuKeys[i]);
            if (m != null) {
                mb.add(m);
            }
        }
        this.menubar = mb;
        return mb;
    }

    /**
     * Create a menu for the app. By default this pulls the definition of the
     * menu from the associated resource file.
     */
    protected JMenu createMenu(String key) {
        String[] itemKeys = tokenize(getResourceString(key));
        JMenu menu = new JMenu(getResourceString(key + "Label"));
        for (int i = 0; i < itemKeys.length; i++) {
            if (itemKeys[i].equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem mi = createMenuItem(itemKeys[i]);
                menu.add(mi);
            }
        }
        return menu;
    }

    // Yarked from JMenu, ideally this would be public.
    protected PropertyChangeListener createActionChangeListener(JMenuItem b) {
        return new ActionChangedListener(b);
    }

    // Yarked from JMenu, ideally this would be public.
    private class ActionChangedListener implements PropertyChangeListener {

        JMenuItem menuItem;

        ActionChangedListener(JMenuItem mi) {
            super();
            this.menuItem = mi;
        }

        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (e.getPropertyName().equals(Action.NAME)) {
                String text = (String) e.getNewValue();
                menuItem.setText(text);
            } else if (propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean) e.getNewValue();
                menuItem.setEnabled(enabledState.booleanValue());
            }
        }
    }

    private JTextComponent editor;
    // This is the editor where syntax template is made based on
    // Syntax words {w1|w2|w3 ...}
    private JTextComponent syntaxEditor;
    // This is where the spin text is made choose  random from {w1|w2|w3 ...}
    private JTextComponent spinEditor;
    private JList list;
    private Hashtable commands;
    private Hashtable menuItems;
    private JMenuBar menubar;
    private JToolBar toolbar;
    private JComponent status;
    private JFrame elementTreeFrame;
    protected ElementTreePanel elementTreePanel;

    protected FileDialog fileDialog;

    /**
     * Listener for the edits on the current document.
     */
    protected UndoableEditListener undoHandler = new UndoHandler();

    /**
     * UndoManager that we add edits to.
     */
    protected UndoManager undo = new UndoManager();

    /**
     * Suffix applied to the key used in resource file lookups for an image.
     */
    public static final String imageSuffix = "Image";

    /**
     * Suffix applied to the key used in resource file lookups for a label.
     */
    public static final String labelSuffix = "Label";

    /**
     * Suffix applied to the key used in resource file lookups for an action.
     */
    public static final String actionSuffix = "Action";

    /**
     * Suffix applied to the key used in resource file lookups for tooltip text.
     */
    public static final String tipSuffix = "Tooltip";

    public static final String openAction = "open";
    public static final String newAction = "new";
    public static final String saveAction = "save";
    public static final String exitAction = "exit";
    public static final String showElementTreeAction = "showElementTree";

    class UndoHandler implements UndoableEditListener {

        /**
         * Messaged when the Document has created an edit, the edit is added to
         * <code>undo</code>, an instance of UndoManager.
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    /**
     * FIXME - I'm not very useful yet
     */
    class StatusBar extends JComponent {

        public StatusBar() {
            super();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        }

        public void paint(Graphics g) {
            super.paint(g);
        }

    }

    // --- action implementations -----------------------------------
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();

    /**
     * Actions defined by the Notepad class
     */
    private Action[] defaultActions = {
        new NewAction(),
        new OpenAction(),
        new SaveAction(),
        new ExitAction(),
        new ShowElementTreeAction(),
        undoAction,
        redoAction
    };

    class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undo.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                undo.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undo.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undo.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    class OpenAction extends NewAction {

        OpenAction() {
            super(openAction);
        }

        public void actionPerformed(ActionEvent e) {
            Frame frame = getFrame();
            JFileChooser chooser = new JFileChooser(prefs.get(LAST_USED_FOLDER,
                    new File(".").getAbsolutePath()));
            chooser.setMultiSelectionEnabled(true);
            int ret = chooser.showOpenDialog(frame);

            if (ret != JFileChooser.APPROVE_OPTION) {
                return;
            }
            prefs.put(LAST_USED_FOLDER, chooser.getSelectedFile().getParent());
            // cause multiselection is on always will be array of files
            File[] fs = chooser.getSelectedFiles();//File();
            Document oldDoc = getEditor().getDocument();
            getEditor().setDocument(new PlainDocument());
            for (File f : fs) {
                if (f.isFile() && f.canRead()) {

                    if (oldDoc != null) {
                        oldDoc.removeUndoableEditListener(undoHandler);
                    }
                    if (elementTreePanel != null) {
                        elementTreePanel.setEditor(null);
                    }

                    frame.setTitle(f.getName());
                    synchronized (OpenAction.this) {
                        Thread loader = new FileLoader(f, editor.getDocument());
                        loader.start();
                        while (!((FileLoader) loader).hasFinish) {
                            try {
                                wait(100);
                            } catch (InterruptedException iex) {

                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(getFrame(),
                            "Could not open file: " + f,
                            "Error opening file",
                            JOptionPane.ERROR_MESSAGE);
                }
            } // end for 
            try {
                if (editor.getDocument().getLength() > 0) {
                    String s = editor.getDocument().getText(0, editor.getDocument().getLength());
                    ((DefaultListModel) list.getModel()).clear();
                    /*
                    System.out.println("---------------------------- WORDS ---------------------------");
                    for (final String word : new WordIterator(s)) {
                        System.out.println(word);
                    }
                     */
 /*
                    System.out.println("------------------------ SENTANCES --------------------------");
                    for (final String word : new SentenceIterator(s, Locale.ENGLISH)) {
                        System.out.println(word);
                    }
                    // all 3-grams
                    for (final String ngram : new NGramIterator(3, s, Locale.ENGLISH)) {
                        System.out.println(ngram);
                    }

                    // all 3-grams not containing stop words
                    for (final String ngram : new NGramIterator(3, s, Locale.ENGLISH, StopWords.English)) {
                        System.out.println(ngram);
                    }
                     */
                    System.out.println("-------------------  MOST COMMON WORDS -------------------");
                    // find the most common 3-grams of the Baskervilles 
                    final Counter<String> ngrams = new Counter<String>();
                    for (final String ngram : new NGramIterator(3, s, Locale.ENGLISH, StopWords.English)) {
                        ngrams.note(ngram.toLowerCase(Locale.ENGLISH));
                    }
                    for (final Map.Entry<String, Integer> en : ngrams.getAllByFrequency().subList(0, 10)) {
                        System.out.println(en.getKey() + ": " + en.getValue());
                        ((DefaultListModel) list.getModel()).addElement(en.getKey() + ": " + en.getValue());
                    }

                }// end if
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class SaveAction extends AbstractAction {

        Boolean openStart;

        SaveAction() {
            super(saveAction);
        }

        public void actionPerformed(ActionEvent e) {
            Frame frame = getFrame();
            JFileChooser chooser = new JFileChooser();
            int ret = chooser.showSaveDialog(frame);

            if (ret != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File f = chooser.getSelectedFile();
            frame.setTitle(f.getName());
            // synchronized (SaveAction.this) {
            Thread saver = new FileSaver(f, editor.getDocument());
            //saver.join();
            saver.start();

            //}
        }
    }

    class NewAction extends AbstractAction {

        NewAction() {
            super(newAction);
        }

        NewAction(String nm) {
            super(nm);
        }

        public void actionPerformed(ActionEvent e) {
            Document oldDoc = getEditor().getDocument();
            if (oldDoc != null) {
                oldDoc.removeUndoableEditListener(undoHandler);
            }
            getEditor().setDocument(new PlainDocument());
            getEditor().getDocument().addUndoableEditListener(undoHandler);
            resetUndoManager();
            getFrame().setTitle(resources.getString("Title"));
            revalidate();
        }
    }

    /**
     * Really lame implementation of an exit command
     */
    class ExitAction extends AbstractAction {

        ExitAction() {
            super(exitAction);
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Action that brings up a JFrame with a JTree showing the structure of the
     * document.
     */
    class ShowElementTreeAction extends AbstractAction {

        ShowElementTreeAction() {
            super(showElementTreeAction);
        }

        ShowElementTreeAction(String nm) {
            super(nm);
        }

        public void actionPerformed(ActionEvent e) {
            if (elementTreeFrame == null) {
                // Create a frame containing an instance of 
                // ElementTreePanel.
                try {
                    String title = resources.getString("ElementTreeFrameTitle");
                    elementTreeFrame = new JFrame(title);
                } catch (MissingResourceException mre) {
                    elementTreeFrame = new JFrame();
                }

                elementTreeFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent weeee) {
                        elementTreeFrame.setVisible(false);
                    }
                });
                Container fContentPane = elementTreeFrame.getContentPane();

                fContentPane.setLayout(new BorderLayout());
                elementTreePanel = new ElementTreePanel(getEditor());
                fContentPane.add(elementTreePanel);
                elementTreeFrame.pack();
            }
            elementTreeFrame.show();
        }
    }

    /**
     * Thread to load a file into the text storage model
     */
    class FileLoader extends Thread {

        public boolean hasFinish;

        FileLoader(File f, Document doc) {
            setPriority(4);
            this.f = f;
            this.doc = doc;
        }

        public void run() {
            try {
                // initialize the statusbar
                status.removeAll();
                JProgressBar progress = new JProgressBar();
                progress.setMinimum(0);
                progress.setMaximum((int) f.length());
                status.add(progress);
                status.revalidate();

                // try to start reading
                Reader in = new FileReader(f);
                char[] buff = new char[4096];
                int nch;
                while ((nch = in.read(buff, 0, buff.length)) != -1) {
                    doc.insertString(doc.getLength(), new String(buff, 0, nch), null);
                    progress.setValue(progress.getValue() + nch);
                }
            } catch (IOException e) {
                final String msg = e.getMessage();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(getFrame(),
                                "Could not open file: " + msg,
                                "Error opening file",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (BadLocationException e) {
                System.err.println(e.getMessage());
            } finally {
                hasFinish = true;
            }
            doc.addUndoableEditListener(undoHandler);
            // we are done... get rid of progressbar
            status.removeAll();
            status.revalidate();

            resetUndoManager();

            if (elementTreePanel != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        elementTreePanel.setEditor(getEditor());
                    }
                });
            }
        }

        Document doc;
        File f;
    }

    /**
     * Thread to save a document to file
     */
    class FileSaver extends Thread {

        Document doc;
        File f;
        public boolean hasFinish;

        FileSaver(File f, Document doc) {
            setPriority(4);
            this.f = f;
            this.doc = doc;
        }

        public void run() {
            try {
                // initialize the statusbar
                status.removeAll();
                JProgressBar progress = new JProgressBar();
                progress.setMinimum(0);
                progress.setMaximum((int) doc.getLength());
                status.add(progress);
                status.revalidate();

                // start writing
                Writer out = new FileWriter(f);
                Segment text = new Segment();
                text.setPartialReturn(true);
                int charsLeft = doc.getLength();
                int offset = 0;
                while (charsLeft > 0) {
                    doc.getText(offset, Math.min(4096, charsLeft), text);
                    out.write(text.array, text.offset, text.count);
                    charsLeft -= text.count;
                    offset += text.count;
                    progress.setValue(offset);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                final String msg = e.getMessage();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(getFrame(),
                                "Could not save file: " + msg,
                                "Error saving file",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (BadLocationException e) {
                System.err.println(e.getMessage());
            } finally {
                hasFinish = true;
            }
            // we are done... get rid of progressbar
            status.removeAll();
            status.revalidate();
        }
    }
}
