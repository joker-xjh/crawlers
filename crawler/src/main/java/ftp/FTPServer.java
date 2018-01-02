package ftp;

public class FTPServer {
	
	private static String fileDir="F://server-directory/";
	
	public static String getSystemPath(String fileName) {
		return fileDir + fileName;
	}

}
