package orm2.bedprovider;

import java.sql.Connection;
import java.sql.SQLException;

public interface BedProvider {
	
	Connection getConnection() throws SQLException;

}
