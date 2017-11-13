package demo47;

public class ReadWriteDataSourceChoice {
	
	public enum DataSourceType{
		write, read;
	}
	
	private static final ThreadLocal<DataSourceType> holder = new ThreadLocal<>();
	
	public static void markWrite() {
		holder.set(DataSourceType.write);
	}
	
	public static void markRead() {
		holder.set(DataSourceType.read);
	}
	
	public static void reset() {
		holder.remove();
	}
	
	public static boolean isChoiceNone() {
		return holder.get() == null;
	}
	
	public static boolean isChoiceWrite() {
		return DataSourceType.write == holder.get();
	}
	
	public static boolean isChoiceRead() {
		return DataSourceType.read == holder.get();
	}
	

}
