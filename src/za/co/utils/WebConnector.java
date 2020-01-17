/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import net.sourceforge.jsocks.socks.Socks5Proxy;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import webtools.gui.run.WebToolMainFrame;
import webtools.net.LastHTTPError;
import webtools.net.TorSocket;
import webtools.net.WebRequest;

/**
 *
 * @author alibaba0507
 */
public class WebConnector {

    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36";
    private String userAgent = USER_AGENT;
    private Proxy p;

    public static Socks5Proxy constractTorProxy() {
        try {
            //Proxy webProxy 
            //  = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9150));
            Socks5Proxy webProxy = new Socks5Proxy("127.0.0.1", 30002);
            return webProxy;
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return null;
    }

    public void setProxy(Proxy p) {
        this.p = p;
    }

    public void setUserAgent(String ua) {
        this.userAgent = ua;
    }

    public Document get(WebRequest req) throws Exception {
        Document doc = null;
        String proxyType = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.type");
        if (proxyType != null && !proxyType.trim().equals("")) {

            if (proxyType.equals("HTTPS")) {
                String host = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.host");
                String port = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.port");
                String user = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.user");
                String psw = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.pasw");
                if (host.trim().length() > 0 && port.trim().length()>0)
                    doc = getJsoup(req, "", true, host, Integer.parseInt(port), user, psw,"");
                else
                    doc = getJsoup(req, "", true, null, 0, null, null,"");
            } else if (proxyType.equals("TOR")) {
                String proxyHost = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.tor.host");
                String proxyPort = (String) WebToolMainFrame.defaultProjectProperties.get("proxy.tor.port");
                TorSocket tor = new TorSocket(proxyHost, Integer.parseInt(proxyPort));
                //URL u = new URL("https://www.google.com/search?q=my+ip");
                URL u = new URL(req.getCurrentURL());
                final String html;
                html = tor.connect(u, null);
                doc = Jsoup.parse(html);
            }
        } else {
            doc = getJsoup(req, "", true, null, 0, null, null,"");
        }

        return doc;
    }

    public Document getJsoup(WebRequest req, String cookies, boolean isFollowRedirect,
            String proxyHost, int proxyPort, String proxyUser, String proxyPassW,String referer) {
        Connection.Response response;
        URL u = null;
        LastHTTPError.errorCode = 0; // clear last error
        try {
            
            u = new URL(req.getOriginalURL());
            //set up handler for jsse
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Provider prov = new com.sun.net.ssl.internal.ssl.Provider();
            Security.addProvider(prov);

            Connection c = Jsoup.connect(req.getCurrentURL());
            if (proxyHost != null) {
                if (proxyUser != null) {
                    c.sslSocketFactory(new SSLTunnelSocketFactory(proxyHost, Integer.toString(proxyPort), proxyUser, proxyPassW));
                } else {
                    c.sslSocketFactory(new SSLTunnelSocketFactory(proxyHost, Integer.toString(proxyPort)));
                }
            }
            if (p != null) {
                c = c.proxy(p);
            }
            response = c.userAgent(this.userAgent)
                    .header("Accept-Language", "en-US")
                    .header("Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Cookie", cookies)
                    .referrer(referer)
                    .ignoreContentType(true) // This is used because Jsoup "approved" content-types parsing is enabled by default by Jsoup
                    .followRedirects(false)
                    .timeout(60 * 1000)
                    .execute();

            if (response.hasHeader("location") && isFollowRedirect) {
                String redirectUrl = "";
                redirectUrl = response.header("location");
                Map<String, String> cookiesMap = response.cookies();
                cookies = "";
                Iterator it = cookiesMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = (String) cookiesMap.get(key);
                    if (cookies != "") {
                        cookies += ";";
                    }
                    cookies += key + "=" + value;
                }
                //ProjectsUI.console.append(" Reirect [" + redirectUrl + "]\r\n");
                //ProjectsUI.console.append(" Coocies [" + cookies + "]\r\n");
                req.setCurrentURL(redirectUrl);
                
                return getJsoup(req, cookies, isFollowRedirect, proxyHost, proxyPort, proxyUser, proxyPassW,"http://www.google.com");
            }
            String body = response.body();
            //System.out.print(body);
            return Jsoup.parse(body);
        } catch (HttpStatusException e) {
            e.printStackTrace();
            LastHTTPError.errorCode = e.getStatusCode();
            if (e.getStatusCode() == 429) { // to many request
                return null;
                /*
                if (u != null && u.getHost().indexOf("google") > -1) {
                    String ext = u.getHost().substring(u.getHost().indexOf("google.")+"google.".length());
                    if (ext.equals("com")) { // we will try to redirect to come other sites
                        String url = req.getOriginalURL().replaceFirst(".com", ".co.uk");
                        if (url.startsWith("https:"))
                        {
                            url = url.replaceFirst("https:", "http:");
                        }
                        req.setOriginalURL(url);
                        req.setCurrentURL(url);
                        
                        return getJsoup(req, "", isFollowRedirect,
                                 proxyHost, proxyPort, proxyUser, proxyPassW,"");
                    } else if (ext.equals(".co.uk")) {
                        String url = req.getOriginalURL().replaceFirst(".co.uk", ".com.au");
                         req.setOriginalURL(url);
                        req.setCurrentURL(url);
                        return getJsoup(req, "", isFollowRedirect,
                                 proxyHost, proxyPort, proxyUser, proxyPassW,"");

                    }
                }// end if (u != null && u.getHost().indexOf("google") > -1)
                */
           }else
                return null;
        }catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
            
        return null;
    }

    /**
     * SSLSocket used to tunnel through a proxy
     */
    class SSLTunnelSocketFactory extends SSLSocketFactory {

        private String tunnelHost;
        private int tunnelPort;
        private SSLSocketFactory dfactory;
        private String tunnelPassword;
        private String tunnelUserName;
        private boolean socketConnected = false;
        private int falsecount = 0;

        /**
         * Constructor for the SSLTunnelSocketFactory object
         *
         * @param proxyHost The url of the proxy host
         * @param proxyPort the port of the proxy
         */
        public SSLTunnelSocketFactory(String proxyHost, String proxyPort) {
            System.err.println("creating Socket Factory");
            tunnelHost = proxyHost;
            tunnelPort = Integer.parseInt(proxyPort);
            dfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }

        /**
         * Constructor for the SSLTunnelSocketFactory object
         *
         * @param proxyHost The url of the proxy host
         * @param proxyPort the port of the proxy
         * @param proxyUserName username for authenticating with the proxy
         * @param proxyPassword password for authenticating with the proxy
         */
        public SSLTunnelSocketFactory(String proxyHost, String proxyPort, String proxyUserName, String proxyPassword) {
            System.err.println("creating Socket Factory with password/username");
            tunnelHost = proxyHost;
            tunnelPort = Integer.parseInt(proxyPort);
            tunnelUserName = proxyUserName;
            tunnelPassword = proxyPassword;
            dfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }

        /**
         * Sets the proxyUserName attribute of the SSLTunnelSocketFactory object
         *
         * @param proxyUserName The new proxyUserName value
         */
        public void setProxyUserName(String proxyUserName) {
            tunnelUserName = proxyUserName;
        }

        /**
         * Sets the proxyPassword attribute of the SSLTunnelSocketFactory object
         *
         * @param proxyPassword The new proxyPassword value
         */
        public void setProxyPassword(String proxyPassword) {
            tunnelPassword = proxyPassword;
        }

        /**
         * Gets the supportedCipherSuites attribute of the
         * SSLTunnelSocketFactory object
         *
         * @return The supportedCipherSuites value
         */
        public String[] getSupportedCipherSuites() {
            return dfactory.getSupportedCipherSuites();
        }

        /**
         * Gets the defaultCipherSuites attribute of the SSLTunnelSocketFactory
         * object
         *
         * @return The defaultCipherSuites value
         */
        public String[] getDefaultCipherSuites() {
            return dfactory.getDefaultCipherSuites();
        }

        /**
         * Gets the socketConnected attribute of the SSLTunnelSocketFactory
         * object
         *
         * @return The socketConnected value
         */
        public synchronized boolean getSocketConnected() {
            return socketConnected;
        }

        /**
         * Creates a new SSL Tunneled Socket
         *
         * @param s Ignored
         * @param host destination host
         * @param port destination port
         * @param autoClose wether to close the socket automaticly
         * @return proxy tunneled socket
         * @exception IOException raised by an IO error
         * @exception UnknownHostException raised when the host is unknown
         */
        public Socket createSocket(Socket s, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            Socket tunnel = new Socket(tunnelHost, tunnelPort);
            doTunnelHandshake(tunnel, host, port);
            SSLSocket result = (SSLSocket) dfactory.createSocket(tunnel, host, port, autoClose);
            result.addHandshakeCompletedListener(
                    new HandshakeCompletedListener() {
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    System.out.println("Handshake Finished!");
                    System.out.println("\t CipherSuite :" + event.getCipherSuite());
                    System.out.println("\t SessionId: " + event.getSession());
                    System.out.println("\t PeerHost: " + event.getSession().getPeerHost());
                    setSocketConnected(true);
                }
            });
            // thanks to David Lord in the java forums for figuring out this line is the problem
            // result.startHandshake(); //this line is the bug which stops Tip111 from working correctly
            return result;
        }

        /**
         * Creates a new SSL Tunneled Socket
         *
         * @param host destination host
         * @param port destination port
         * @return tunneled SSL Socket
         * @exception IOException raised by IO error
         * @exception UnknownHostException raised when the host is unknown
         */
        public Socket createSocket(String host, int port)
                throws IOException, UnknownHostException {
            return createSocket(null, host, port, true);
        }

        /**
         * Creates a new SSL Tunneled Socket
         *
         * @param host Destination Host
         * @param port Destination Port
         * @param clientHost Ignored
         * @param clientPort Ignored
         * @return SSL Tunneled Socket
         * @exception IOException Raised when IO error occurs
         * @exception UnknownHostException Raised when the destination host is
         * unknown
         */
        public Socket createSocket(String host, int port, InetAddress clientHost,
                int clientPort)
                throws IOException, UnknownHostException {
            return createSocket(null, host, port, true);
        }

        /**
         * Creates a new SSL Tunneled Socket
         *
         * @param host destination host
         * @param port destination port
         * @return tunneled SSL Socket
         * @exception IOException raised when IO error occurs
         */
        public Socket createSocket(InetAddress host, int port)
                throws IOException {
            return createSocket(null, host.getHostName(), port, true);
        }

        /**
         * Creates a new SSL Tunneled Socket
         *
         * @param address destination host
         * @param port destination port
         * @param clientAddress ignored
         * @param clientPort ignored
         * @return tunneled SSL Socket
         * @exception IOException raised when IO exception occurs
         */
        public Socket createSocket(InetAddress address, int port,
                InetAddress clientAddress, int clientPort)
                throws IOException {
            return createSocket(null, address.getHostName(), port, true);
        }

        /**
         * Sets the socketConnected attribute of the SSLTunnelSocketFactory
         * object
         *
         * @param b The new socketConnected value
         */
        private synchronized void setSocketConnected(boolean b) {
            socketConnected = b;
        }

        /**
         * Description of the Method
         *
         * @param tunnel tunnel socket
         * @param host destination host
         * @param port destination port
         * @exception IOException raised when an IO error occurs
         */
        private void doTunnelHandshake(Socket tunnel, String host, int port) throws IOException {
            OutputStream out = tunnel.getOutputStream();
            //generate connection string
            String msg = "CONNECT " + host + ":" + port + " HTTP/1.0\n"
                    + "User-Agent: "
                    + WebConnector.USER_AGENT;
            if (tunnelUserName != null && tunnelPassword != null) {
                //add basic authentication header for the proxy
              //  sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                
                String encodedPassword =new String( Base64.getEncoder().encode((tunnelUserName + ":" + tunnelPassword).getBytes()));
                msg = msg + "\nProxy-Authorization: Basic " + encodedPassword;
            }
            msg = msg + "\nContent-Length: 0";
            msg = msg + "\nPragma: no-cache";

            msg = msg + "\r\n\r\n";

            System.err.println(msg);
            byte b[];
            try {
                //we really do want ASCII7 as the http protocol doesnt change with locale
                b = msg.getBytes("ASCII7");
            } catch (UnsupportedEncodingException ignored) {
                //If ASCII7 isn't there, something is seriously wrong!
                b = msg.getBytes();
            }
            out.write(b);
            out.flush();

            byte reply[] = new byte[200];
            int replyLen = 0;
            int newlinesSeen = 0;
            boolean headerDone = false;

            InputStream in = tunnel.getInputStream();
            boolean error = false;

            while (newlinesSeen < 2) {
                int i = in.read();
                if (i < 0) {
                    throw new IOException("Unexpected EOF from Proxy");
                }
                if (i == '\n') {
                    headerDone = true;
                    ++newlinesSeen;
                } else if (i != '\r') {
                    newlinesSeen = 0;
                    if (!headerDone && replyLen < reply.length) {
                        reply[replyLen++] = (byte) i;
                    }
                }
            }

            //convert byte array to string
            String replyStr;
            try {
                replyStr = new String(reply, 0, replyLen, "ASCII7");
            } catch (UnsupportedEncodingException ignored) {
                replyStr = new String(reply, 0, replyLen);
            }

            //we check for connection established because our proxy returns http/1.1 instead of 1.0
            if (replyStr.toLowerCase().indexOf("200 connection established") == -1) {
                System.err.println(replyStr);
                throw new IOException("Unable to tunnel through " + tunnelHost + ":" + tunnelPort + ". Proxy returns\"" + replyStr + "\"");
            }
            //tunneling hanshake was successful
        }

    }

}






