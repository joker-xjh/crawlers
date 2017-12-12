package demo62;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import sun.nio.cs.StreamDecoder;

@SuppressWarnings("all")
public class MyInputStreamReader extends Reader{
	
	private final StreamDecoder sd;
	
	public MyInputStreamReader(InputStream in) {
		super(in);
		try {
			sd = StreamDecoder.forInputStreamReader(in, this, (String)null);
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public int read() throws IOException {
		return sd.read();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return sd.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		sd.close();
	}

}
