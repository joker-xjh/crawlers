package demo62;

import java.io.DataInput;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

public class MyDataInputStream extends FilterInputStream implements DataInput{

	protected MyDataInputStream(InputStream in) {
		super(in);
	}
	
	private byte[] bytearray = new byte[80];
	private char[] chararray = new char[80];
	
	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}
	
	

	@Override
	public void readFully(byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		if(len < 0)
			throw new ArrayIndexOutOfBoundsException();
		int count = 0;
		while(count < len) {
			int n = in.read(b, off + count, len - count);
			if(n < 0)
				break;
			count += n;
		}
	}

	@Override
	public int skipBytes(int n) throws IOException {
		int total = 0, cur = 0;
		while(total < n && ((cur = (int) in.skip(n - total)) > 0))
			total += cur;
		return total;
	}

	@Override
	public boolean readBoolean() throws IOException {
		int c = in.read();
		if(c < 0)
			throw new EOFException();
		return c != 0;
	}

	@Override
	public byte readByte() throws IOException {
		int c = in.read();
		if(c < 0)
			throw new EOFException();
		return (byte)c;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		int c = in.read();
		if(c < 0)
			throw new EOFException();
		return c;
	}

	@Override
	public short readShort() throws IOException {
		int c1 = in.read();
		int c2 = in.read();
		if((c1 | c2) < 0)
			throw new EOFException();
		return (short) (c1 << 8 + c2 << 0);
	}

	@Override
	public int readUnsignedShort() throws IOException {
		int c1 = in.read();
		int c2 = in.read();
		if((c1 | c2) < 0)
			throw new EOFException();
		return (c1 << 8) + (c2 << 0);
	}

	@Override
	public char readChar() throws IOException {
		int c1 = in.read();
		int c2 = in.read();
		if((c1 | c2) < 0)
			throw new EOFException();
		return (char) ((c1 << 8 ) + (c2 << 0));
	}

	@Override
	public int readInt() throws IOException {
		int c1 = in.read();
		int c2 = in.read();
		int c3 = in.read();
		int c4 = in.read();
		if((c1 | c2 | c3 | c4) < 0)
			throw new EOFException();
		return (c1 << 24) + (c2 << 16) + (c3 << 8) + (c4 << 0);
	}

	private byte[] longbuffer = new byte[8];
	
	@Override
	public long readLong() throws IOException {
		readFully(longbuffer);
		return (((long)longbuffer[0] << 56) +
				 ((long)longbuffer[1] << 48) +
				 ((long)longbuffer[2] << 40) +
				 ((long)longbuffer[3] << 32) +
				 ((long)longbuffer[4] << 24) +
				 (longbuffer[5] << 16) +
				 (longbuffer[6] << 8) +
				 (longbuffer[7]));
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public String readLine() throws IOException {
		return null;
	}

	@Override
	public String readUTF() throws IOException {
		return readUTF(this);
	}
	
	
	public static String readUTF(DataInput in) throws IOException {
		int length = in.readUnsignedShort();
		byte[] bytearray = null;
		char[] chararray = null;
		if(in instanceof MyDataInputStream) {
			MyDataInputStream dis = (MyDataInputStream) in;
			if(dis.bytearray.length < length) {
				dis.bytearray = new byte[length * 2];
				dis.chararray = new char[length * 2];
			}
			bytearray = dis.bytearray;
			chararray = dis.chararray;
		}
		else {
			bytearray = new byte[length];
			chararray = new char[length];
		}
		
		int c, char2, char3;
		int count = 0;
		int chararray_count = 0;
		in.readFully(bytearray, 0, length);
		while(count < length) {
			c = (int)bytearray[count] & 0xff;
			if(c > 127)
				break;
			count++;
			chararray[chararray_count++] = (char) c;
		}
		while(count < length) {
			c = (int)bytearray[count] & 0xff;
			switch(c >> 4) {
				case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
					chararray[chararray_count++] = (char) c;
					break;
				case 12 : case 13 :
					count += 2;
					if(count > length)
						throw new UTFDataFormatException(
	                            "malformed input: partial character at end");
					char2 = bytearray[count - 1];
					if((char2 & 0xc0) != 0x80)
						throw new UTFDataFormatException(
	                            "malformed input around byte " + count);
					chararray[chararray_count++] = (char) (((c & 0x1F) << 6 ) | (char2 & 0x3F));
					break;
				case 14 :
					count += 3;
					if(count > length)
						throw new UTFDataFormatException(
	                            "malformed input: partial character at end");
					char2 = bytearray[count -2];
					char3 = bytearray[count - 1];
					if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                            "malformed input around byte " + (count-1));
					chararray[chararray_count++] = (char) (((c & 0x0F) << 12) |
							                        (char2 & 0x3F) << 6 |
							                        (char3 & 0x3F));
					break;
				default:
					 throw new UTFDataFormatException(
		                        "malformed input around byte " + count);
			}
		}
		return new String(chararray, 0, chararray_count);
	}
}
