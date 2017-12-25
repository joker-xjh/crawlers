package demo62;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class MyLineNumberReader extends BufferedReader{
	
	private int lineNumber;
	private int markedLineNumber;
	private boolean skipLF;
	private boolean markedSkipLF;

	public MyLineNumberReader(Reader in) {
		super(in);
	}
	
	public MyLineNumberReader(Reader in, int size) {
		super(in, size);
	}
	
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	
	@Override
	public int read() throws IOException {
		synchronized(lock) {
			int c = super.read();
			if(skipLF) {
				if(c == '\n')
					c= super.read();
				skipLF = false;
			}
			switch(c) {
				case '\r' :
					skipLF = true;
				case '\n' :
					lineNumber++;
					return '\n';
			}
			return c;
		}
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		synchronized(lock) {
			int n = super.read(cbuf, off, len);
			for(int i=off; i<off + n; i++) {
				char c = cbuf[i];
				if(skipLF) {
					skipLF = false;
					if(c == '\n')
						continue;
				}
				switch(c) {
					case '\r':
						skipLF = true;
					case '\n':
						lineNumber++;
						break;
				}
			}
			
			return n;
		}
	}
	
	@Override
	public String readLine() throws IOException {
		synchronized(lock) {
			String line = super.readLine();
			if(line != null)
				lineNumber++;
			skipLF = false;
			return line;
		}
	}
	
	private static final int maxSkipBufferSize = 8192;
	
	private char[] skipBuffer;
	
	@Override
	public long skip(long n) throws IOException {
		if(n < 0)
			throw new IllegalArgumentException();
		int nn = (int) Math.min(n, maxSkipBufferSize);
		synchronized(lock) {
			long r = n;
			if(skipBuffer == null || skipBuffer.length < nn)
				skipBuffer = new char[nn];
			while(r > 0) {
				int nr = read(skipBuffer, 0, (int)Math.min(nn, r));
				if(nr < 0)
					break;
				r -= nr;
			}
			return n-r;
		}
	}
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		synchronized(lock) {
			super.mark(readAheadLimit);
			markedLineNumber = lineNumber;
			markedSkipLF = skipLF;
		}
	}
	
	
	@Override
	public void reset() throws IOException {
		synchronized (lock) {
			super.reset();
			lineNumber = markedLineNumber;
			skipLF = markedSkipLF;
		}
	}
	
	

}
