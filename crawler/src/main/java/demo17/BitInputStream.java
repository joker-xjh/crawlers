package demo17;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class BitInputStream extends FilterInputStream{
	
	private int bitsLeft;
	private int bitsCountLeft;

	protected BitInputStream(InputStream in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		clearBuff();
		return super.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		clearBuff();
		return super.read(b);
	}
	
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		clearBuff();
		return super.read(b, off, len);
	}
	
	
	@Override
	public long skip(long n) throws IOException {
		clearBuff();
		return super.skip(n);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		clearBuff();
		super.reset();
	}
	
	public int readBit() throws IOException{
		if(bitsCountLeft == 0) {
			bitsLeft = super.read();
			bitsCountLeft = 8;
			if(bitsLeft == -1) {
				bitsCountLeft = 0;
				return -1;
			}
		}
		return (bitsLeft & mask(--bitsCountLeft)) >>> bitsCountLeft;
	}
	
	
	public int[] readBitsToArray(int nbits) throws IOException {
		if(nbits <= 0)
			throw new IllegalArgumentException();
		int first = readBit();
		if(first == -1)
			return new int[] {};
		int[] bits = new int[nbits];
		bits[0] = first;
		for(int i=1; i<nbits; i++) {
			int bit = readBit();
			if(bit == -1) {
				return Arrays.copyOf(bits, i);
			}
			else {
				bits[i] = bit;
			}
		}
		return bits;
	}
	
	
	public int readBits(int nbits) throws IOException {
		if(nbits <=0 || nbits >32)
			throw new IllegalArgumentException();
		int bit = readBit();
		if(bit == -1)
			return -1;
		int result = bit;
		for(int i=1; i<nbits; i++) {
			bit = readBit();
			if(bit == -1)
				break;
			result = result << 1 | bit;
		}
		return result;
	}
	
	
	
	private static byte mask(int num) {
		if(num<=0 || num >=8) {
			throw new IllegalArgumentException();
		}
		return (byte) (1 << num);
	}
	
	private void clearBuff() {
		bitsCountLeft = 0;
		bitsLeft = 0;
	}

}
