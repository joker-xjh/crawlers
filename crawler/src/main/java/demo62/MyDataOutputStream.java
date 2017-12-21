package demo62;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

public class MyDataOutputStream extends FilterOutputStream implements DataOutput{
	
	protected int written;
	
	private byte[] bytearray;

	public MyDataOutputStream(OutputStream out) {
		super(out);
	}
	
	private void incCount(int value) {
		int temp = written + value;
		if(temp < 0)
			temp = Integer.MAX_VALUE;
		written = temp;
	}
	
	@Override
	public synchronized void write(int b) throws IOException {
		out.write(b);
		incCount(1);
	}
	
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		incCount(len);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		out.write( v ? 1 : 0);
		incCount(1);
	}

	@Override
	public void writeByte(int v) throws IOException {
		out.write(v);
		incCount(1);
	}

	@Override
	public void writeShort(int v) throws IOException {
		out.write((v >>> 8) & 0xFF );
		out.write((v & 0xFF));
		incCount(2);
	}

	@Override
	public void writeChar(int v) throws IOException {
		out.write((v >>> 8) & 0xFF);
		out.write((v & 0xFF));
		incCount(2);
	}

	@Override
	public void writeInt(int v) throws IOException {
		out.write((v >>> 24) & 0xFF);
		out.write((v >>> 16) & 0xFF);
		out.write((v >>> 8) & 0xFF);
		out.write((v >>> 0) & 0xFF);
		incCount(4);
	}

	private byte[] longbuffer = new byte[8];
	
	@Override
	public void writeLong(long v) throws IOException {
		longbuffer[0] = (byte) ((v >>> 56) & 0xFF);
		longbuffer[1] = (byte) ((v >>> 48) & 0xFF);
		longbuffer[2] = (byte) ((v >>> 40) & 0xFF);
		longbuffer[3] = (byte) ((v >>> 32) & 0xFF);
		longbuffer[4] = (byte) ((v >>> 24) & 0xFF);
		longbuffer[5] = (byte) ((v >>> 16) & 0xFF);
		longbuffer[6] = (byte) ((v >>> 8) & 0xFF);
		longbuffer[7] = (byte) ((v >>> 0) & 0xFF);
		write(longbuffer);
		incCount(8);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeBytes(String s) throws IOException {
		int length = s.length();
		for(int i=0; i<length; i++) {
			out.write((byte)s.charAt(i));
		}
		incCount(length);
	}

	@Override
	public void writeChars(String s) throws IOException {
		int length = s.length();
		for(int i=0; i<length; i++) {
			char c = s.charAt(i);
			out.write((c >>> 8) & 0xFF);
			out.write((c >>> 0) & 0xFF);
		}
		incCount(length * 2);
	}

	@Override
	public void writeUTF(String s) throws IOException {
		int len = writeUTF(s, this);
		incCount(len);
	}
	
	public static int writeUTF(String str, DataOutput out) throws IOException {
		int strlen = str.length();
		int utflen = 0;
		int c, count = 0;
		for(int i=0; i<strlen; i++) {
			char ch = str.charAt(i);
			if((ch >= 0) && (ch <= 127))
				utflen++;
			else if(ch > 0x07FF)
				utflen += 3;
			else
				utflen += 2;
		}
		
		if (utflen > 65535)
            throw new UTFDataFormatException(
                "encoded string too long: " + utflen + " bytes");
		
		byte[] bytearray = null;
		if(out instanceof MyDataOutputStream) {
			MyDataOutputStream dos = (MyDataOutputStream) out;
			if(dos.bytearray == null || dos.bytearray.length < utflen + 2)
				dos.bytearray = new byte[utflen * 2 + 2];
			bytearray = dos.bytearray;
		}
		else {
			bytearray = new byte[utflen + 2];
		}
		
		bytearray[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearray[count++] = (byte) ((utflen >>> 0) & 0xFF);
		
		int i=0;
		for(; i<strlen; i++) {
			c = str.charAt(i);
			if(!(c >= 0 && c <= 127))
				break;
			bytearray[count++] = (byte) c;
		}
		for(; i<strlen; i++) {
			c = str.charAt(i);
			if((c >= 0 && c <= 127)) {
				bytearray[count++] = (byte) c;
			}
			else if(c > 0x07FF) {
				bytearray[count++] = (byte) ((0xE0) | ((c >>> 12) & 0x0F));
				bytearray[count++] = (byte) (0x80 | (c >>> 6) & 0x3F);
				bytearray[count++] = (byte) (0x80 | (c >>> 0) & 0x3F);
			}
			else {
				bytearray[count++] = (byte) (0xC0 | (c >>> 6) & 0x1F);
				bytearray[count++] = (byte) (0x80 | (c >>> 0) & 0x3F);
			}
		}
		out.write(bytearray, 0, utflen+2);
		return utflen + 2;
	}
	
	public int size() {
		return written;
	}

}
