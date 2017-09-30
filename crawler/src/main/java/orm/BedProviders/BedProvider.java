package orm.BedProviders;

import java.sql.Connection;
import java.sql.SQLException;

public interface BedProvider {
	
	Connection getConnection() throws SQLException;

}
