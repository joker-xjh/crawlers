package orm2.pool.imp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import orm2.pool.interfaces.ObjectFactory;

public class ConnectionFactory implements ObjectFactory<Connection>{

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

	@Override
	public Connection create() {
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isValid(Connection obj) {
		try {
			if(obj.isClosed())
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void destroy(Connection obj) {
		try {
			obj.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
