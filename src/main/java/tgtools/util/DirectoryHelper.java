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
	 *
	 * @param m_Path
	 */
	public static void createDirectory(String m_Path) {
		if (!StringUtil.isNullOrEmpty(m_Path)) {
			File file = new File(m_Path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
	}

	/**
	 * 获取类所在目录(如果是jar 则返回jar所在目录)
	 * @param pClass
	 */
	public static String getClassDirectory(Class<?> pClass) {
		String path = pClass.getResource("").toString();
		path=StringUtil.replace(path,"\\","/");
		//windows
		if (path.indexOf("file:/") >= 0) {
			path = "/" + path.substring(path.indexOf("file:/") + 6);
		}
		//linux
		else {
			path = path.substring(path.indexOf(":") + 1);
		}

		if(path.indexOf("jar")>0) {
			path = path.substring(0, path.indexOf("jar"));
		}
		if(path.lastIndexOf("/")>0) {
			path = path.substring(0, path.lastIndexOf("/") + 1);
		}

		return path;
	}


}