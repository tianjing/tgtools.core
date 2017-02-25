package tgtools.plugin.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

public class JARLoader extends ClassLoader {
	List<String> jarFiles = new ArrayList<String>();

	public JARLoader(ClassLoader p_parent) {
		super(JARLoader.class.getClassLoader());
	}

	/**
	 * 添加jar包如"c:/1.jar;c:/2.jar"
	 * 
	 * @param paths
	 */
	public void addFiles(String paths) {
		if ((paths == null) || (paths.length() <= 0)) {
			return;
		}
		String separator = System.getProperty("path.separator");
		String[] pathToAdds = paths.split(separator);
		for (int i = 0; i < pathToAdds.length; i++)
			if (!StringUtil.isNullOrEmpty(pathToAdds[i])){
				LogHelper.info("", "正在加载jar包："+pathToAdds[i], "JARLoader.addPath");
				this.jarFiles.add(pathToAdds[i]);
			
			}
	}

	public void addPath(String path) {
		File file = new File(path);
		if (file.exists()) {
			String[] files = tgtools.util.FileUtil.listFiles(path,
					new String[] { "jar" });
			LogHelper.info("", "找到jar包："+files.length, "JARLoader.addPath");
			if (null != files && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					LogHelper.info("", "正在加载jar包："+files[i], "JARLoader.addPath");
					this.jarFiles.add(files[i]);
				}
			}
		}
		else
		{
			LogHelper.info("", "找不到路径："+file.getPath(), "JARLoader.addPath");
		}
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String className = name.replace(".", "/") + ".class";
		try {
			for (String path : this.jarFiles) {
				File f = new File(path);

				if (f.exists()) {
					ZipFile zipFile = null;
					try {
						zipFile = new ZipFile(new File(path), 1);

						ZipEntry en = zipFile.getEntry(className);
						if (en != null) {
							InputStream is = new BufferedInputStream(
									zipFile.getInputStream(en));

							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							while (true) {
								int i = is.read();
								if (i == -1) {
									break;
								}
								baos.write(i);
							}
							is.close();
							byte[] bytes = baos.toByteArray();
							Class<?> theClass = defineClass(name, bytes, 0,
									bytes.length);
							if (theClass == null)
								throw new ClassFormatError();
							return theClass;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (zipFile != null)
							zipFile.close();
					}
				}
			}
		} catch (Exception e) {
			throw new ClassNotFoundException();
		}

		return super.findClass(name);
	}
}