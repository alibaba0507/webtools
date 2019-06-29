/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.utils;

/**
 *
 * @author alibaba0507
 */
public interface ConstInterface {
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String JAVA_HOME = System.getProperty("java.home");
    public static final String CURRENT_DIR = System.getProperty("user.dir");
    public static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // to make pattern find list of object remove front ^ and $ back
    // to add multiple white space \\s+
    public static final String ALL_EMAILS_PATTERN = "[a-zA-Z0-9_.+-]\\s++@\\s+[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
}
