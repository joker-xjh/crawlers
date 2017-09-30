package orm.BedProviders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCBedProvider implements BedProvider {
	
	private static final String username = "";
	
	private static final String password = "";
	
	private static final String driverName = "com.mysql.jdbc.Driver";
	
	private static final String url = "jdbc:mysql://localhost:3306/test";
	
	
	static {
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

}
