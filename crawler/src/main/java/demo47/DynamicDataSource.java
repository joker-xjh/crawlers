package demo47;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.CollectionUtils;

public class DynamicDataSource extends AbstractDataSource implements InitializingBean {
	
	private DataSource writeDataSource;
	
	private Map<String, DataSource> readDataSourceMap;
	
	private String[] readDataSourceNames;
	
	private DataSource[] readDataSources;
	
	private int readDataSourceCount;
	
	private AtomicInteger count = new AtomicInteger(0);
	
	public void setReadDataSourceMap(Map<String, DataSource> readDataSourceMap) {
	    this.readDataSourceMap = readDataSourceMap;
	}
	
	public void setWriteDataSource(DataSource writeDataSource) {
		this.writeDataSource = writeDataSource;
	}
	

	@Override
	public Connection getConnection() throws SQLException {
		return choiceDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return choiceDataSource().getConnection(username, password);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		 if(writeDataSource == null) {
	            throw new IllegalArgumentException("property 'writeDataSource' is required");
	        }
	        if(CollectionUtils.isEmpty(readDataSourceMap)) {
	            throw new IllegalArgumentException("property 'readDataSourceMap' is required");
	        }
	     readDataSourceCount = readDataSourceMap.size();
	     readDataSourceNames = new String[readDataSourceCount];
	     readDataSources = new DataSource[readDataSourceCount];
	     int i = 0;
	     for(Map.Entry<String, DataSource> entry : readDataSourceMap.entrySet()) {
	    	 readDataSourceNames[i] = entry.getKey();
	    	 readDataSources[i] = entry.getValue();
	    	 i++;
	     }
		
	}
	
	private DataSource choiceReadDataSource() {
		int index = count.incrementAndGet() % readDataSourceCount;
		if(index < 0)
			index = -index;
		return readDataSources[index];
	}
	
	private DataSource choiceDataSource() {
		if(ReadWriteDataSourceChoice.isChoiceWrite())
			return writeDataSource;
		if(ReadWriteDataSourceChoice.isChoiceNone())
			return writeDataSource;
		return choiceReadDataSource();
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
