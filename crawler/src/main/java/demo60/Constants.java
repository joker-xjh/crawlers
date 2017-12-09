package demo60;

enum FCB_Type { directory, file };
enum Status_Type { all_right, dupilication_of_name, illegal_name, memory_lack };

public class Constants {
	
	// 文件系统管理常量定义
	static int BLOCK_SIZE = 256; // 每一个块的大小
	static int CLUSTER_SIZE = BLOCK_SIZE * 4; // 每一个簇的大小
	static int CLUSTER_NUM = 2048;
	static int MEMORY_SIZE = CLUSTER_SIZE * CLUSTER_NUM; // 内存大小
	static String INFO_FILE = "data.dat";
	
	static FileFat END_OF_FAT = null; // Fat表结束标志
	static int PARENT_OF_ROOT = -1; // 根结点的父节点
}
