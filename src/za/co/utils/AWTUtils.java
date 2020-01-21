// jEdit settings:
// :tabSize=4:indentSize=4:noTabs=true:folding=explicit:collapseFolds=1:
package za.co.utils;

import static editor.Notepad.imageSuffix;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import webtools.gui.run.WebToolMainFrame;

/**
 * Misc helper methods for AWT that don't require Thinlet.
 *
 * @author Dirk Moebius
 */
public class AWTUtils {

    //{{{ logging
    private static final Logger log = Logger.getLogger("thinletcommons");

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private static final boolean debug() {
        return log.isLoggable(Level.FINE);
    }
    //}}}

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    public static String exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = AWTUtils.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            File f = new File(AWTUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            if (f.getParentFile() != null) {
                f = f.getParentFile();
            }
            jarFolder = f.getPath().replace('\\', '/');
            resourceName = resourceName.replace('\\', '/');
            resourceName = resourceName.substring(resourceName.lastIndexOf('/'));
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }

    private static HashMap keyDescriptions = new HashMap();

    static {
        // initialize HashMap keyDescriptions
        Field[] fields = KeyEvent.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            int modifiers = field.getModifiers();
            if (((modifiers & (Modifier.STATIC | Modifier.PUBLIC)) != 0)
                    && field.getName().startsWith("VK_")) {
                try {
                    int keyCode = field.getInt(null);
                    String keyDescription = field.getName().substring(3);
                    keyDescriptions.put(new Integer(keyCode), keyDescription);
                } catch (IllegalAccessException e) {
                    log.log(Level.SEVERE, "can't get value of field " + field, e);
                }
            }
        }
    }

    public static String getColorString(Color c) {
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();
        StringBuffer s = new StringBuffer("#");
        if (red < 10) {
            s.append("0");
        }
        s.append(Integer.toHexString(red));
        if (green < 10) {
            s.append("0");
        }
        s.append(Integer.toHexString(green));
        if (blue < 10) {
            s.append("0");
        }
        s.append(Integer.toHexString(blue));
        return s.toString();
    }

    /**
     * Returns a thinlet font description for the given font.
     *
     * @param font the font
     * @return a thinlet font description such as "Serif 10 bold italic"
     */
    public static String getFontString(Font font) {
        StringBuffer s = new StringBuffer();
        s.append(font.getFamily());
        s.append(" ");
        s.append(font.getSize());
        if (font.isItalic()) {
            s.append(" italic");
        }
        if (font.isBold()) {
            s.append(" bold");
        }
        return s.toString();
    }

    /**
     * Returns a thinlet font description for the given font, containing only
     * the font properties that <i>differ</i> from the given default font.
     *
     * @param font the font
     * @param defaultFont the default font
     * @return a thinlet font description such as "serif 10 bold italic"
     */
    public static String getFontString(Font font, Font defaultFont) {
        StringBuffer s = new StringBuffer();
        if (!font.getFamily().equals(defaultFont.getFamily())) {
            s.append(font.getFamily());
        }
        if (font.getSize() != defaultFont.getSize()) {
            if (s.length() > 0) {
                s.append(' ');
            }
            s.append(font.getSize());
        }
        if (font.isItalic() != defaultFont.isItalic()) {
            if (s.length() > 0) {
                s.append(' ');
            }
            s.append("italic");
        }
        if (font.isBold() != defaultFont.isBold()) {
            if (s.length() > 0) {
                s.append(' ');
            }
            s.append("bold");
        }
        return s.toString();
    }

    /**
     * Get the frame that the specified component resides in, or null if the
     * component has no ancestor of type <code>Frame</code>.
     */
    public static Frame getFrame(Component comp) {
        while (comp != null && !(Frame.class.isInstance(comp))) {
            comp = comp.getParent();
        }
        return (Frame) comp;
    }

    /**
     * Returns the dimension of the specified text string, if it would be
     * rendered with the font and the rendering context of the specified
     * component.
     */
    public static Dimension getBounds(String text, Component component) {
        Graphics2D graphics = (Graphics2D) component.getGraphics();
        StringTokenizer st = new StringTokenizer(text, "\n", true);
        Dimension dim = new Dimension(0, 0);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals("\n")) {
                token = "W";
            }
            TextLayout textLayout = new TextLayout(token, component.getFont(),
                    (graphics != null) ? graphics.getFontRenderContext()
                            : new FontRenderContext(null, true, false));
            Rectangle2D rect = textLayout.getBounds();
            dim.height += (int) rect.getHeight();
            dim.width = Math.max(dim.width, (int) rect.getWidth());
        }
        return dim;
    }

    /**
     * Load an icon. Use a MediaTracker synchronizing on the specified component
     * to wait for the icon to load.
     *
     * @param component the component that the MediaTracker uses to control the
     * loading of the icon.
     * @param path the path relative to the package path of the class of the
     * component, e.g. "icons/icon.gif".
     */
    public static Image getIcon(Component component, String path) {
        /* URL url = null;
        if (component == null) {
            url = WebToolMainFrame.class.getResource(path);
        } else {
            url = component.getClass().getResource(path);
        }*/

        //Image img = new ImageIcon(/*url*/path).getImage();
        Image img = AWTUtils.getResourceAsIcon(path).getImage();
        return img;//getIcon(component, url);
    }

    /**
     * Load an icon. Use a MediaTracker synchronizing on the specified component
     * to wait for the icon to load.
     *
     * @param component the component that the MediaTracker uses to control the
     * loading of the icon.
     * @param url the URL of the icon.
     */
    public static Image getIcon(Component component, URL url) {
        if (debug()) {
            log.fine("loading icon url=" + url + "...");
        }

        Image icon = WebToolMainFrame.instance.getToolkit().getImage(url);

        MediaTracker mediatracker = new MediaTracker(component);
        mediatracker.addImage(icon, 1);
        try {
            mediatracker.waitForID(1);
        } catch (InterruptedException e) {
            log.warning("loading of icon " + url + " has been interrupted!");
        }

        return icon;
    }

    /**
     * Given an AWTKeyStroke, returns a keystroke description that can be parsed
     * by <code>AWTKeyStroke.getAWTKeyStroke(String s)</code>.
     * <p>
     * Examples: "ctrl O", "alt shift F4", "altGraph Q"
     */
    public static String getAWTKeyStrokeDescription(AWTKeyStroke k) {
        StringBuffer buf = new StringBuffer();

        int mod = k.getModifiers();
        if ((mod & InputEvent.ALT_DOWN_MASK) != 0 || (mod & InputEvent.ALT_MASK) != 0) {
            buf.append("alt ");
        }
        if ((mod & InputEvent.ALT_GRAPH_DOWN_MASK) != 0 || (mod & InputEvent.ALT_GRAPH_MASK) != 0) {
            buf.append("altGraph ");
        }
        if ((mod & InputEvent.META_DOWN_MASK) != 0 || (mod & InputEvent.META_MASK) != 0) {
            buf.append("meta ");
        }
        if ((mod & InputEvent.CTRL_DOWN_MASK) != 0 || (mod & InputEvent.CTRL_MASK) != 0) {
            buf.append("ctrl ");
        }
        if ((mod & InputEvent.SHIFT_DOWN_MASK) != 0 || (mod & InputEvent.SHIFT_MASK) != 0) {
            buf.append("shift ");
        }

        buf.append(getKeyText(k.getKeyCode()));

        return buf.toString();
    }

    /**
     * Return the key description for a key code, according to the key names in
     * <code>java.awt.event.KeyEvent</code>, omitting the prefix "VK_". For
     * example the strings returned may be "A" - "Z", "F1" - "F24" "RIGHT",
     * "BACK_SPACE", "DEAD_TILDE" and so on.
     */
    public static String getKeyText(int keyCode) {
        String desc = (String) keyDescriptions.get(new Integer(keyCode));
        if (desc == null) {
            log.warning("KeyEvent field for keyCode " + keyCode + " not found!"
                    + " Returning default description.");
            return KeyEvent.getKeyText(keyCode);
        } else {
            return desc;
        }
    }

    public static String getJarDirectory() {
        //ClassLoader.getSystemClassLoader().getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        try {
          return new File(AWTUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();//.getPath();
        } catch (URISyntaxException ex) {
           return null;
        }
    }

    /**
     *
     * @param key this is relative path inside jar file basically it is a
     * package path
     * @return
     */
    public static InputStream getResourceAsStream(String key) {
        key = key.replace('\\', '/');
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        return ClassLoader.getSystemClassLoader().getResourceAsStream(key);
    }

    public static BufferedImage getResourceAsImage(String key) {
        try {
            InputStream is = AWTUtils.getResourceAsStream(key);
            if (is != null) {
                return ImageIO.read(is);
            }
            return null;
        } catch (IOException io) {
            return null;
        }
    }

    /**
     * Construct Icon to be used By JButtons , Action , JLabels and so on ...
     *
     * @param key this is relative path inside jar file basically it is a
     * package path
     * @return Icon Object
     */
    public static ImageIcon getResourceAsIcon(String key) {
        BufferedImage bimg = AWTUtils.getResourceAsImage(key);
        if (bimg != null) {
            return new ImageIcon(bimg);
        }
        return null;
    }
}



