package demo49.ioc.util;

public class PathUtils {
	
	private PathUtils() {
		
	}
	
	public static String pathToPackage(String path) {
		if(path.startsWith("/"))
			path = path.substring(1);
		return path.replaceAll("/", ".");
	}
	
	public static String packageToPath(String pack) {
		return pack.replaceAll(".", "/");
	}
	
	public static String concat(Object ...objects) {
		StringBuilder sb = new StringBuilder();
		for(Object object : objects) {
			sb.append(object);
		}
		return sb.toString();
	}
	
	public static String trimSuffix(String name) {
		int dotIndex = name.lastIndexOf('.');
		if(dotIndex == -1)
			return name;
		return name.substring(0, dotIndex);
	}
	
	public static String distillPathFromJarURL(String url) {
        int startPos = url.indexOf(':');
        int endPos = url.lastIndexOf('!');
        return url.substring(startPos + 1, endPos);
    }
	
}
