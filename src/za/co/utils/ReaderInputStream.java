// jEdit settings:
// :tabSize=4:indentSize=4:noTabs=true:folding=explicit:collapseFolds=1:

package za.co.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


/**
 * This class is the opposite of <code>java.io.InputStreamReader</code>: it
 * provides a bridge from (new) character streams (<i>Reader</i>) to (old)
 * byte streams (<i>InputStream</i>). It reads characters and converts them
 * into bytes.
 *
 * @author Dirk Moebius
 */
public class ReaderInputStream extends InputStream
{

    protected Reader reader;


    public ReaderInputStream(Reader reader)
    {
        this.reader = reader;
    }


    public int available() throws IOException
    {
        return reader.ready() ? 1 : 0;
    }


    public void close() throws IOException
    {
        reader.close();
    }


    public void mark(int readAheadLimit)
    {
        try { reader.mark(readAheadLimit); } catch(IOException ignore) {}
    }


    public boolean markSupported()
    {
        return reader.markSupported();
    }


    public int read() throws IOException
    {
        return reader.read();
    }


    public void reset() throws IOException
    {
        reader.reset();
    }


    public long skip(long n) throws IOException
    {
        return reader.skip(n);
    }

}

