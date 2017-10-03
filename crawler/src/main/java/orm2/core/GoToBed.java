package orm2.core;

import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import orm2.annotations.Awake;
import orm2.annotations.Sleeper;
import orm2.annotations.SleeperId;
import orm2.exceptions.BedException;
import orm2.pool.imp.ConnectionPool;
import orm2.pool.interfaces.Pool;

public class GoToBed {
	
	//private BedProvider bedProvider;
	
	private Pool<Connection> pool;
	
//	public GoToBed() {
//		this(new JDBCBedProvider());
//	}
	
	public GoToBed() {
		//this.bedProvider = bedProvider;
		pool = new ConnectionPool(10);
		Reflections reflections = new Reflections();
		Set<Class<?>> sleepers = reflections.getTypesAnnotatedWith(Sleeper.class);
		try {
			for(Class<?> clazz : sleepers) {
				createOrUpdateTable(clazz);
			}
		} catch (BedException e) {
			e.printStackTrace();
		}
		
	}
	
	//创建或者更新表
	private void createOrUpdateTable(Class<?> clazz) throws BedException {
		if(checkTableExists(clazz)) {
			updateTable(clazz);
		}
		else {
			createTable(clazz);
		}
	}
	
	//检查类对应的表是否存在
	private boolean checkTableExists(Class<?> clazz) throws BedException{
		boolean exists = false;
		Connection connection = pool.get();
		try{
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet resultSet = databaseMetaData.getTables(null, null, getTableName(clazz), null);
			if(resultSet.next())
				exists = true;
		} catch (SQLException e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
		return exists;
	}
	
	//取得类对应的表的名字
	private String getTableName(Class<?> clazz) {
		return clazz.getSimpleName().toUpperCase();
	}
	
	
	//更新表中的列
	private void updateTable(Class<?> clazz) throws BedException {
		StringBuilder sb = new StringBuilder();
		sb.append("alter table ").append(getTableName(clazz)).append(" add ");
		Set<String> existingColumns = getTableColumns(clazz);
		List<Field> fields = getSleeperColumns(clazz);
		for(int i=0; i<fields.size(); i++) {
			Field field = fields.get(i);
			String columnName = getColumnName(field);
			if(!existingColumns.contains(columnName)) {
				createColumn(field);
			}
		}
	}
	
	
	//返回已存在的表中的列名
	private Set<String> getTableColumns (Class<?> clazz) throws BedException {
		Set<String> columns = new HashSet<>();
		Connection connection = pool.get();
		try{
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet resultSet = databaseMetaData.getTables(null, null, getTableName(clazz), null);
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			resultSetMetaData.getColumnCount();
			for(int i=0; i<resultSetMetaData.getColumnCount(); i++) {
				String column = resultSetMetaData.getColumnName(i+1);
				columns.add(column);
			}
		} catch (SQLException e) {
			throw new BedException(e);
		}finally {
			pool.release(connection);
		}
		return columns;
	}
	
	
	//返回entity中能够被映射到数据库中的属性
	private List<Field> getSleeperColumns(Class<?> clazz) throws BedException {
		List<Field> fields = new ArrayList<>();
		Field[] f = clazz.getDeclaredFields();
		for(int i=0; i<f.length; i++) {
			Field field = f[i];
			if(checkFieldCanSleep(field)) {
				fields.add(field);
			}
		}
		return fields;
	}
	
	
	//返回entity属性对应的数据库中的类型
	private String getFieldType(Field field) {
		Class<?> type = field.getType();
		if(type == String.class) {
			return "varchar(100)";
		}
		else if(type == int.class || type == Integer.class) {
			return "int";
		}
		else if(type == long.class || type == Long.class) {
			return "bigint";
		}
		else if(type == float.class || type == Float.class) {
			return "float";
		}
		else if(type == double.class || type == Double.class) {
			return "double";
		}
		else if(type == Date.class) {
			return "datetime";
		}
		return null;
	}
	
	//检查属性能否被映射到数据库中
	private boolean checkFieldCanSleep(Field field) {
		boolean b = true;
		String type = getFieldType(field);
		if(type == null)
			b = false;
		else if(field.isAnnotationPresent(Awake.class))
			b = false;
		else if(Modifier.isStatic(field.getModifiers()))
			b = false;
		return b;
	}
	
	
	//返回entity中属性对应的表的列名
	private String getColumnName(Field field) {
		return field.getName().toUpperCase();
	}
	
	
	//为表添加列
	private void createColumn(Field field) throws BedException{
		StringBuilder sb = new StringBuilder();
		sb.append("alter table ").append(getTableName(field.getDeclaringClass())).append(" add ");
		String columnName = getColumnName(field);
		sb.append(columnName).append(" ");
		String columnType = getFieldType(field);
		sb.append(columnType);
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try {
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
	}
	
	
	//创建表
	private void createTable(Class<?> clazz) throws BedException {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(getTableName(clazz)).append(" ( ");
		List<Field> fields = getSleeperColumns(clazz);
		for(int i=0; i<fields.size(); i++) {
			Field field = fields.get(i);
			String columnName = getColumnName(field);
			String columnType = getFieldType(field);
			sb.append(columnName).append(" ").append(columnType);
			if(isIdField(field)) {
				sb.append(" auto_increment ").append(" primary key ");
			}
			if(i < fields.size() - 1)
				sb.append(",");
		}
		sb.append(" ) ");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try {
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
	}
	
	
	//判断属性是否有sleepId注解
	private boolean isIdField(Field field) {
		if(field.isAnnotationPresent(SleeperId.class))
			return true;
		return false;
	}
	
	private Field getIdField(Class<?> clazz) {
		Field id = null;
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++) {
			if(fields[i].isAnnotationPresent(SleeperId.class)) {
				id = fields[i];
				break;
			}
		}
		return id;
	}
	
	
	//inser obj
	public void insert(Object object) throws BedException {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(getTableName(object.getClass())).append(" ( ");
		List<Field> list = getSleeperColumns(object.getClass());
		for(int i=0; i<list.size(); i++) {
			Field field = list.get(i);
			String columnName = getColumnName(field);
			sb.append(columnName);
			if(i<list.size()-1)
				sb.append(",");
		}
		sb.append(" ) values ( ");
		for(int i=0; i<list.size(); i++) {
			sb.append("?");
			if(i<list.size()-1)
				sb.append(",");
		}
		sb.append(" ) ");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			for(int i=0; i<list.size(); i++) {
				Field field = list.get(i);
				preparedStatement.setObject(i+1, field.get(object));
			}
			preparedStatement.execute();
		} catch (Exception e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
	}
	
	//删除object
	public void delete(Object object) throws BedException {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(getTableName(object.getClass()));
		Field id = getIdField(object.getClass());
		sb.append(" where ").append(getColumnName(id)).append(" = ?");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setObject(1, id.get(object));
			preparedStatement.execute();
		} catch (Exception e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
	}
	
	
	//修改object
	public void update(Object object) throws BedException{
		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(getTableName(object.getClass())).append(" set ");
		List<Field> list = getSleeperColumns(object.getClass());
		for(int i=0; i<list.size(); i++) {
			Field field = list.get(i);
			sb.append(getColumnName(field)).append(" = ? ");
			if(i<list.size()-1)
				sb.append(",");
		}
		Field id = getIdField(object.getClass());
		sb.append(" where ").append(getColumnName(id)).append(" = ? ");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			for(int i=0; i<list.size(); i++) {
				Field field = list.get(i);
				preparedStatement.setObject(i+1, field.get(object));
			}
			preparedStatement.setObject(list.size()+1, id.get(object));
			preparedStatement.execute();
		} catch (Exception e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
	}
	
	
	//查找obj
	public <T> T select(Class<T> clazz, Serializable id) throws BedException {
		T obj = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(getTableName(clazz));
		Field idField = getIdField(clazz);
		sb.append(" where ").append(getColumnName(idField)).append(" = ? ");
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try{
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setObject(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				obj = clazz.newInstance();
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
				List<Field> list = getSleeperColumns(clazz);
				for(int i=0; i<list.size(); i++) {
					Field field = list.get(i);
					field.setAccessible(true);
					String columnName = resultSetMetaData.getColumnName(i);
					field.set(obj, resultSet.getObject(columnName));
				}
			}
		} catch (Exception e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
		
		return obj;
	}
	
	
	//查找所有的obj
	public <T> List<T> selectAll(Class<T> clazz) throws BedException {
		List<T> objects = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(getTableName(clazz));
		String sql = sb.toString();
		System.out.println(sql);
		Connection connection = pool.get();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<Field> fields = getSleeperColumns(clazz);
			while(resultSet.next()) {
				T obj = clazz.newInstance();
				for(int i=0; i<fields.size(); i++) {
					Field field = fields.get(i);
					String columnName =getColumnName(field);
					field.set(obj, resultSet.getObject(columnName));
				}
				objects.add(obj);
			}
		} catch (Exception e) {
			throw new BedException(e);
		}
		finally {
			pool.release(connection);
		}
		
		return objects;
	}
	
	
	
	

}
