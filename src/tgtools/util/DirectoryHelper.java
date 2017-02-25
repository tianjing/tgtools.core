package tgtools.util;

import java.io.File;
/**
 * 目录帮助类
 * @author tianjing
 *
 */
public class DirectoryHelper {
	/**
	 * 创建目录，如果给的目录不存在则会创建，如果已存在则不创建
	 * @param m_Path
	 */
	public static void createDirectory(String m_Path) {
		if(!StringUtil.isNullOrEmpty(m_Path))
		{
			File file=new  File(m_Path);
			if(!file.exists())
			{
				file.mkdirs();
			}
		}
	}
}
