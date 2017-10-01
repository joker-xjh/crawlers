package orm2.bedprovider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCBedProvider implements BedProvider{
	
	private static String username = "";
	
	private static String password = "";
	
	private static String driver = "";
	
	private static String url = "";
	
	static {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}
