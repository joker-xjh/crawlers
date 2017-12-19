package demo62;

import java.io.IOException;
import java.io.Reader;

public class MyBufferedReader extends Reader{
	
	private Reader in;
	private char[] cb;
	private int nextChar, nChar;
    private static final int INVALIDATED = -2;
    private static final int UNMARKED = -1;
    private int markedChar = UNMARKED;
    private int readAheadLimit = 0;
    private boolean skipLF = false;
    private boolean markedSkipLF = false;
    private static final int defaultCharBufferSize = 8192;
    private static final int defaultExpectedLineLength = 80;
    
    public MyBufferedReader(Reader in, int sz) {
		super(in);
		if(sz <= 0)
			throw new IllegalArgumentException("Buffer size <= 0");
		this.in = in;
		this.cb = new char[sz];
		nextChar = nChar = 0;
	}
    
    public MyBufferedReader(Reader in) {
		this(in, defaultCharBufferSize);
	}
    
    private void ensureOpen() throws IOException {
    	if(in == null)
    		throw new IOException("Stream closed");
    }
    
    private void fill() throws IOException {
    	int dst;
    	if(markedChar <= UNMARKED)
    		dst = 0;
    	else {
    		int delta = nextChar - markedChar;
    		if(delta >= readAheadLimit) {
    			dst = 0;
    			readAheadLimit = 0;
    			markedChar = INVALIDATED;
    		}
    		else {
    			if(readAheadLimit <= cb.length) {
    				System.arraycopy(cb, markedChar, cb, 0, delta);
    				markedChar = 0;
    				dst = delta;
    			}
    			else {
    				char[] temp = new char[readAheadLimit];
    				System.arraycopy(cb, markedChar, temp, 0, delta);
    				cb = temp;
    				markedChar = 0;
    				dst = delta;
    			}
    			nextChar = nChar = delta;
    		}
    	}
    	
    	int n;
    	do {
			n = in.read(cb, dst, cb.length - dst);
		} while (n == 0);
    	if(n > 0) {
    		nextChar = dst;
    		nChar = dst + n;
    	}	
    }
    
    @Override
    public int read() throws IOException {
    	synchronized(lock) {
    		ensureOpen();
    		for(;;) {
    			if(nextChar >= nChar) {
        			fill();
        			if(nextChar >= nChar)
        				return -1;
        		}
        		if(skipLF) {
        			skipLF = false;
        			if(cb[nextChar] == '\n') {
        				nextChar++;
        				continue;
        			}
        		}
        		return cb[nextChar];
    		}
    	}
    }
    
    private int read1(char[] buf, int off, int len) throws IOException {
    	if(nextChar >= nChar) {
    		if(len >= cb.length && markedChar <= UNMARKED && !skipLF)
    			return in.read(buf, off, len);
    		fill();
    	}
    	if(nextChar >= nChar)
    		return -1;
    	if(skipLF) {
    		skipLF = false;
    		if(cb[nextChar] == '\n')
    			nextChar++;
    		if(nextChar >= nChar)
        		return -1;
    	}
    	int min = Math.min(len, nChar - nextChar);
    	System.arraycopy(cb, nextChar, buf, off, len);
    	nextChar += min;
    	return min;
    }
    
    

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(off < 0 || off >= cbuf.length || len < 0 || off + len > cbuf.length || len + off < 0)
				throw new ArrayIndexOutOfBoundsException();
			else if(len == 0)
				return 0;
			int n = read1(cbuf, off, len);
			if(n <= 0)
				return n;
			while(n < len && in.ready()) {
				int n1 = read1(cbuf, off + n, len - n);
				if(n1 <= 0)
					break;
				n += n1;
			}
			return n;
		}
	}
	
	String readLine(boolean ignoreLF) throws IOException {
		StringBuffer s = null;
		int startChar;
		synchronized(lock) {
			ensureOpen();
			boolean omitLF = ignoreLF || skipLF;
			
				for(;;) {
					if(nextChar >= nChar)
						fill();
					if(nextChar >= nChar) {
						if(s != null && s.length() > 0)
							return s.toString();
						else
							return null;
					}
					
					boolean eof = false;
					char c = 0;
					int i;
					
					if(omitLF && cb[nextChar] == '\n')
						nextChar++;
					skipLF = false;
					omitLF = false;
					
					charloop:
						for(i=nextChar; i<nextChar; i++) {
							c = cb[i];
							if(c == '\r' || c == '\n') {
								eof = true;
								break charloop;
							}
						}
					startChar = nextChar;
					nextChar = i;
					if(eof) {
						String str;
						if(s == null)
							str = new String(cb, startChar, i - startChar);
						else {
							s.append(cb, startChar, i- startChar);
							str = s.toString();
						}
						nextChar++;
						if(c == '\r')
							skipLF = true;
						return str;
					}
					if(s == null)
						s = new StringBuffer(defaultExpectedLineLength);
					s.append(cb, startChar, i - startChar);
				}
		}
	}
	
	public String readLine() throws IOException {
		return readLine(false);
	}
	
	@Override
	public long skip(long n) throws IOException {
		if(n <= 0)
			throw new IllegalArgumentException("skip value is negative");
		synchronized(lock) {
			ensureOpen();
			long r = n;
			while(r > 0) {
				if(nextChar >= nChar)
					fill();
				if(nextChar >= nChar)
					break;
				if (skipLF) {
                    skipLF = false;
                    if (cb[nextChar] == '\n') {
                        nextChar++;
                    }
                }
				long d = nChar - nextChar;
				if( r <= d) {
					nextChar += r;
					r = 0;
					break;
				}
				else {
					r -= d;
					nextChar = nChar;
				}
			}
			
			return n - r;
		}
	}
	
	
	@Override
	public boolean ready() throws IOException {
		synchronized (lock) {
			ensureOpen();
			if(skipLF) {
				if(nextChar >= nChar && in.ready())
					fill();
				if(nextChar < nChar) {
					if(cb[nextChar] == '\n') {
						nextChar++;
					}
					skipLF = false;
				}
			}
			return nextChar < nChar || in.ready();
		}
	}
	
	@Override
	public boolean markSupported() {
		return true;
	}
	
	
	@Override
	public void mark(int readAheadLimit) throws IOException {
		if(readAheadLimit < 0)
			throw new IllegalArgumentException("Read-ahead limit < 0");
		synchronized(lock) {
			ensureOpen();
			this.readAheadLimit = readAheadLimit;
			this.markedChar = nextChar;
			this.markedSkipLF = skipLF;
		}
	}
	
	
	@Override
	public void reset() throws IOException {
		synchronized(lock) {
			ensureOpen();
			if(markedChar < 0)
				throw new IOException((markedChar == INVALIDATED)
                        ? "Mark invalid"
                        : "Stream not marked");
			nextChar = markedChar;
			skipLF = markedSkipLF;
		}
	}
	
	@Override
	public void close() throws IOException {
		synchronized(lock) {
			if(in == null) {
				return;
			}
			try {
				in.close();
			} finally {
				in = null;
				cb = null;
			}
		}
		
	}

}
