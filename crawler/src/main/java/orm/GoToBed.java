package orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import orm.BedProviders.BedProvider;
import orm.annotations.DoNotSave;
import orm.annotations.Sleeper;
import orm.annotations.SleeperId;
import orm.exception.BedException;

public class GoToBed {
	
	private BedProvider bedProvider;
	
	public GoToBed(BedProvider bedProvider) {
		this.bedProvider = bedProvider;
		Reflections reflections = new Reflections("orm");
		Set<Class<?>> sleepers = reflections.getTypesAnnotatedWith(Sleeper.class);
		try {
			
			for(Class<?> clazz : sleepers) {
				createOrUpdateTableFor(clazz);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createOrUpdateTableFor(Class<?> clazz) throws SQLException {
		if(isTableExists(clazz)) {
			updateTableColumnsFor(clazz);
		}
		else {
			createTableFor(clazz);
		}
	}
	
	
	private boolean isTableExists(Class<?> clazz) throws SQLException{
		boolean exists = false;
		Connection connection = bedProvider.getConnection();
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet resultSet = databaseMetaData.getTables(null, null, getTableNameFor(clazz), null);
		if(resultSet.next())
			exists = true;
		connection.close();
		return exists;
	}
	
	
	private void createTableFor(Class<?> clazz) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(getTableNameFor(clazz)).append(" ( ");
		List<Field> list = getPersistableFields(clazz);
		for(int i=0; i<list.size(); i++) {
			Field field = list.get(i);
			if(isIdFiled(field)) {
				if(!(field.getType()==int.class || field.getType() == Integer.class || field.getType() == long.class || field.getType() == Long.class)) {
					throw new SQLException("主键的类型必须为 int, Integer,long,Long 其中之一");
				}
				String columnName = getColumnNameFor(field);
				String columnType = getStringColumnTypeFor(field.getType());
				sb.append(columnName).append(" ").append(columnType).append(" ");
				sb.append("AUTO_INCREMENT").append(" ").append("PRIMARY KEY");
			}
			else {
				String columnName = getColumnNameFor(field);
				String columnType = getStringColumnTypeFor(field.getType());
				sb.append(columnName).append(" ").append(columnType);
			}
			if(i <list.size() -1)
				sb.append(",");
		}
		sb.append(");");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = bedProvider.getConnection();
		Statement statement = connection.createStatement();
		statement.execute(sql);
		connection.close();
	}
	
	private List<Field> getPersistableFields(Class<?> clazz) {
		List<Field> list = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++) {
			Field field = fields[i];
			if(isFiledPersistable(field))
				list.add(field);
		}
		return list;
	}
	
	
	private boolean isIdFiled(Field field) {
		return field.isAnnotationPresent(SleeperId.class);
	}
	
	
	private String getTableNameFor(Class<?> clazz) {
		return clazz.getSimpleName().toUpperCase();
	}
	
	
	private String getColumnNameFor(Field field) {
		return field.getName().toUpperCase();
	}
	
	
	private boolean isFiledPersistable(Field field) {
		if(field.isAnnotationPresent(DoNotSave.class)) {
			return false;
		}
		else if(java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
			return false;
		}
		else if(!persistenceIsImplementedForClass(field.getType())) {
			return false;
		}
		return true;
	}
	
	
	private boolean persistenceIsImplementedForClass(Class<?> clazz) {
		String columnType = getStringColumnTypeFor(clazz);
		if(columnType == null)
			return false;
		return true;
	}
	
	
	private String getStringColumnTypeFor(Class<?> clazz) {
		if(clazz == String.class) {
			return "varchar(128)";
		}
		else if(clazz == long.class || clazz == Long.class) {
			return "bigint";
		}
		else if(clazz == int.class || clazz == Integer.class) {
			return "int";
		}
		else if(clazz == double.class || clazz == Double.class) {
			return "double";
		}
		else if(clazz == float.class || clazz == Float.class) {
			return "float";
		}
		else if(clazz == Date.class) {
			return "datetime";
		}
		return null;
	}
	
	
	private void updateTableColumnsFor(Class<?> clazz) throws SQLException {
		Connection connection = bedProvider.getConnection();
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++) {
			Field field = fields[i];
			if(isFiledPersistable(field)) {
				if(!existsColumnFor(field, connection)) {
					createColumnFor(field, connection);
				}
			}
			else {
				System.out.println(field+" 没有映射到数据库中");
			}
		}
	}
	
	
	
	
	private boolean existsColumnFor(Field field, Connection connection) throws SQLException {
		boolean exist = false;
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet resultSet = databaseMetaData.getColumns(null, null, getTableNameFor(field.getDeclaringClass()), getColumnNameFor(field));
		if(resultSet.next())
			exist = true;
		return exist;
	}
	
	
	private void createColumnFor(Field field, Connection connection) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("alter table ");
		sb.append(getTableNameFor(field.getDeclaringClass())).append(" ");
		sb.append("add").append(" ");
		sb.append(getColumnNameFor(field)).append(" ");
		sb.append(getStringColumnTypeFor(field.getType())).append(";");
		String sql = sb.toString();
		System.out.println(sql);
		Statement statement = connection.createStatement();
		statement.execute(sql);
		connection.close();
	}
	
	
//	private boolean hasId(Object object) throws IllegalArgumentException, IllegalAccessException {
//		Field field = getIdFieldFor(object.getClass());
//		if(field != null) {
//			field.setAccessible(true);
//			Object val = field.get(object).toString();
//			field.setAccessible(false);
//			if(val != null && !val.equals("0")) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	private Field getIdFieldFor(Class<?> clazz) {
		Field id = null;
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++) {
			Field field = fields[i];
			if(field.isAnnotationPresent(SleeperId.class)) {
				id = field;
				break;
			}
		}
		return id;
	}
	
	
	public void insert(Object object) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(getTableNameFor(object.getClass()));
		sb.append("( ");
		List<Field> fields = getPersistableFields(object.getClass());
		for(int i=0; i<fields.size(); i++) {
			Field field = fields.get(i);
			sb.append(getColumnNameFor(field));
			if(i < fields.size()-1)
				sb.append(",");
		}
		sb.append(") values ( ");
		for(int i=0; i<fields.size(); i++) {
			sb.append("?");
			if(i < fields.size()-1)
				sb.append(",");
		}
		sb.append(")");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection =null;
		try {
			connection = bedProvider.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			for(int i=0; i<fields.size(); i++) {
				Field field = fields.get(i);
				field.setAccessible(true);
				preparedStatement.setObject(i+1,field.get(object));
				field.setAccessible(false);
			}
			preparedStatement.execute();
		} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	public void delete(Object object) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(getTableNameFor(object.getClass()));
		sb.append("where ");
		Field id = getIdFieldFor(object.getClass());
		id.setAccessible(true);
		String columnName = getColumnNameFor(id);
		sb.append(columnName).append(" = ?");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = null;
		try {
			 connection = bedProvider.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql);
			 Object value = id.get(object);
			 preparedStatement.setObject(1, value);
			 preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	public void update(Object object) {
		StringBuilder sb = new StringBuilder();
		Class<?> clazz = object.getClass();
		sb.append("update ").append(getTableNameFor(clazz)).append(" ").append("set ");
		List<Field> list = getPersistableFields(clazz);
		for(int i=0; i<list.size(); i++) {
			Field field = list.get(i);
			String columnName = getColumnNameFor(field);
			sb.append(columnName).append(" = ?");
			if(i<list.size()-1)
				sb.append(",");
		}
		sb.append("where ");
		Field id = getIdFieldFor(clazz);
		String idColumnName = getColumnNameFor(id);
		sb.append(idColumnName).append(" = ? ");
		String sql = sb.toString();
		Connection connection = null;
		try {
			connection = bedProvider.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			for(int i=0; i<list.size(); i++) {
				Field field = list.get(i);
				field.setAccessible(true);
				statement.setObject(i+1, field.get(object));
				field.setAccessible(false);
			}
			statement.setObject(list.size()+1, id.get(object));
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public <T> T select(Class<T> clazz, Serializable id) throws BedException {
		T object = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(getTableNameFor(clazz)).append(" ");
		Field idField = getIdFieldFor(clazz);
		String idFieldName = getColumnNameFor(idField);
		sb.append("where ").append(idFieldName).append(" = ?");
		String sql = sb.toString();
		System.out.println(sql);
		try (Connection connection = bedProvider.getConnection()){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setObject(1,id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				object = clazz.newInstance();
				List<Field> fields = getPersistableFields(clazz);
				for(int i=0; i<fields.size(); i++) {
					Field field = fields.get(i);
					field.setAccessible(true);
					String columnName = getColumnNameFor(field);
					field.set(object, resultSet.getObject(columnName));
				}
			}
		} catch (Exception e) {
			throw new BedException(e);
		}
		
		return object;
	}
	
	public <T> List<T> selectAll(Class<T> clazz) throws BedException {
		List<T> objects = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(getTableNameFor(clazz));
		String sql = sb.toString();
		System.out.println(sql);
		try (Connection connection = bedProvider.getConnection()){
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<Field> fields = getPersistableFields(clazz);
			while(resultSet.next()) {
				T object = clazz.newInstance();
				for(int i=0; i<fields.size(); i++) {
					Field field = fields.get(i);
					field.setAccessible(true);
					String columnName = getColumnNameFor(field);
					field.set(object, resultSet.getObject(columnName));
				}
				objects.add(object);
			}
		} catch (Exception e) {
			throw new BedException(e);
		}
		return objects;
	}
	
	
	
	

}
