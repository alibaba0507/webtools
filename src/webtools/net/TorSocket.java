/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webtools.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;

/**
 *
 * @author alibaba0507
 */
public class TorSocket {

    private String host;
    private int port;
    private Socket underlying;

    public TorSocket(String host, int port) {
        this.host = host;
        this.port = port;

        InetSocketAddress HiddenerProxyAddress = new InetSocketAddress(this.host,this.port);
        Proxy HiddenProxy = new Proxy(Proxy.Type.SOCKS, HiddenerProxyAddress);
        underlying = new Socket(HiddenProxy);

    }

    public static void main(String[] args) {
        try {
            URL u = new URL("https://www.google.com/search?q=my+ip");
            String html = new TorSocket("127.0.0.1", 9153).connect(u,null);
            System.out.println("//************************************************/");
            System.out.print(html);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String connect(URL u, String cookie) throws Exception {
        String html= "";
        try{
        SocketAddress sa = new InetSocketAddress(u.getHost(), 80);
        underlying.connect(sa);
        underlying.setKeepAlive(true);
        OutputStream theOutput = underlying.getOutputStream();
        InputStream in = underlying.getInputStream();
        html = socketBuffers(u,cookie,theOutput,in);
        }catch (Exception ex){
            html = "<html><b> ConnectionError <br><style color=\"red\">" 
                        + ex.getMessage() + "</style></b>";
            underlying.close();
        }finally{
            underlying.close();
            return html;
        }

    }
    
    private String socketBuffers(URL u,String cookie
                    ,OutputStream theOutput,InputStream in) throws Exception{
        
        // no auto-flushing
        PrintWriter pw = new PrintWriter(theOutput, false);

        String req = u.getPath() + "?" + u.getQuery();
        pw.print("GET /" + req + " HTTP/1.0\r\n");
        pw.print("Host: " + u.getHost() + " \r\n");
        if (cookie != null && cookie != "") {
            pw.print("Cookie:" + cookie + " \r\n");
        }
        pw.print("Accept-Language: en-US,en;q=0.8 \r\n");
        pw.print("User-Agent: Mozilla \r\n");
        pw.print("Accept: text/plain, text/html, text/*\r\n");
        pw.print("\r\n");
        pw.flush();

        
        Map<String, String> header = parseHTTPHeaders(in);
        if (header.get("Location") != null) {
            String newUrl = (String) header.get("Location");

            // get the cookie if need, for login
            String cookies = header.get("Set-Cookie");
            //return connect(new URL(newUrl), cookies);
            underlying.close();
            //return new TorSocket(this.host, this.port).connect(new URL(newUrl), cookie);
            return socketBuffers(new URL(newUrl),cookies,theOutput,in); 
        }
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        int c;
        StringBuffer buff = new StringBuffer();
        while ((c = br.read()) != -1) {
            System.out.print((char) c);
            buff.append((char) c);
        }
        
        return buff.toString();
    }

    public void connect() {
        try {
            InetSocketAddress HiddenerProxyAddress = new InetSocketAddress("127.0.0.1", 9153);
            Proxy HiddenProxy = new Proxy(Proxy.Type.SOCKS, HiddenerProxyAddress);
            Socket underlying = new Socket(HiddenProxy);
            SocketAddress sa = new InetSocketAddress("www.google.com", 80);
            underlying.connect(sa);
            underlying.setKeepAlive(true);

            OutputStream theOutput = underlying.getOutputStream();
            // no auto-flushing
            PrintWriter pw = new PrintWriter(theOutput, false);
            // native line endings are uncertain so add them manually
            URL u = new URL("https://www.google.com/search?q=my+ip");

            String req = u.getPath() + "?" + u.getQuery();
            pw.print("GET /" + req + " HTTP/1.0\r\n");
            pw.print("Accept-Language: en-US,en;q=0.8 \r\n");
            pw.print("User-Agent: Mozilla \r\n");
            pw.print("Accept: text/plain, text/html, text/*\r\n");
            pw.print("\r\n");
            pw.flush();

            InputStream in = underlying.getInputStream();
            Map<String, String> header = parseHTTPHeaders(in);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            int c;
            StringBuffer buff = new StringBuffer();
            while ((c = br.read()) != -1) {
                System.out.print((char) c);
                buff.append((char) c);
            }
            /*
             If you want to connect to a hidden service (onion address), you should create an unresolved socket address:
            InetSocketAddress sa = InetSocketAddress.createUnresolved("http://facebookcorewwwi.onion", 80)
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> parseHTTPHeaders(InputStream inputStream)
            throws IOException {
        int charRead;
        StringBuffer sb = new StringBuffer();
        while (true) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {            // if we've got a '\r'
                sb.append((char) inputStream.read()); // then write '\n'
                charRead = inputStream.read();        // read the next char;
                if (charRead == '\r') {                  // if it's another '\r'
                    sb.append((char) inputStream.read());// write the '\n'
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }

        String[] headersArray = sb.toString().split("\r\n");
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < headersArray.length - 1; i++) {
            headers.put(headersArray[i].split(": ")[0],
                    headersArray[i].split(": ")[1]);
        }

        return headers;
    }
}
