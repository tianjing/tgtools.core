package tgtools.tasks;

import java.util.HashMap;
/**
 * Task 的参数类型 可 put 参数 供 Task run时调用
 * @author tianjing
 *
 */
public class TaskContext extends HashMap<String,Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6376769702989377918L;
	/**
	 * 文件下载地址
	 */
	public static final String TaskContext_Key_FileUrl="FileUrl";
	/**
	 * 下载文件的保存路径（含文件名）
	 */
	public static final String TaskContext_Key_FilePath="FilePath";
}
