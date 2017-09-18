package Core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {
	
	public static String Convert2MD5(String password) {
		MessageDigest md5 = null;
		
		try {
			md5 = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		char[] chararray = password.toCharArray();
		byte[] bytearray = new byte[chararray.length];
		for(int i=0; i<bytearray.length; i++)
			bytearray[i] = (byte)chararray[i];
		byte[] md5array = md5.digest(bytearray);
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<md5array.length; i++) {
			int val = ((int) md5array[i]) & 0xff; 
			if(val < 16)
				sb.append('0');
			sb.append(Integer.toHexString(val));
		}
		return sb.toString();
		
	}

}
