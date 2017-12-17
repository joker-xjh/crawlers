package demo62;

public class MyBits {
	
	static boolean getBoolean(byte[] b, int off) {
		return b[off] != 0;
	}
	
	static char getChar(byte[] b, int off) {
		return (char) (b[off] << 8 + (b[off+1] & 0xff));
	}
	
	static short getShort(byte[] b, int off) {
		return (short) (b[off] << 8 + (b[off+1] & 0xff));
	}
	
	static int getInt(byte[] b, int off) {
		return (b[off] << 24) +
				(b[off+1] & 0xff) << 16 +
				(b[off+2]& 0xff) << 8 +
				(b[off+3] & 0xff);
	}
	
	static float getFloat(byte[] b, int off) {
		return Float.intBitsToFloat(getInt(b, off));
	}
	
	static long getLong(byte[] b, int off) {
		return (b[off] << 56) +
				((b[off+1] & 0xff) << 48) +
				((b[off+2] & 0xff) << 40) +
				((b[off+3] & 0xff) << 32) +
				((b[off+4] & 0xff) << 24) +
				((b[off+5] & 0xff) << 16) +
				((b[off+6] & 0xff) << 8) +
				(b[off+7] & 0xff);
	}
	
	static double getDouble(byte[] b, int off) {
		return Double.longBitsToDouble(getLong(b, off));
	}
	
	static void putBoolean(byte[] b, int off, boolean val) {
		b[off] = (byte) (val ? 1 : 0);
	}
	
	static void putChar(byte[] b, int off, char val) {
		b[off] = (byte) (val >>> 8);
		b[off+1] = (byte) val;
	}
	
	static void putShort(byte[] b, int off, short val) {
		b[off] = (byte) (val >>> 8);
		b[off+1] = (byte) val;
	}
	
	static void putInt(byte[] b, int off, int val) {
		b[off] = (byte) (val >>> 24);
		b[off+1] = (byte) (val >>> 16);
		b[off+2] = (byte) (val >>> 8);
		b[off+3] = (byte) val;
	}
	
	static void putFloat(byte[] b, int off, float val) {
		putInt(b, off, Float.floatToIntBits(val));
	}
	
	static void putLong(byte[] b, int off, long val) {
		b[off] = (byte) (val >>> 56);
		b[off+1] = (byte) (val >>> 48);
		b[off+2] = (byte) (val >>> 40);
		b[off+3] = (byte) (val >>> 32);
		b[off+4] = (byte) (val >>> 24);
		b[off+5] = (byte) (val >>> 16);
		b[off+6] = (byte) (val >>> 8);
		b[off+7] = (byte) val;
	}
	
	static void putDouble(byte[] b, int off, double val) {
		putLong(b, off, Double.doubleToLongBits(val));
	}
	
	
	
	
	
	
	
	

}
