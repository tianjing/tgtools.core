package tgtools.plugin.util;

import sun.misc.URLClassPath;
import sun.net.www.ParseUtil;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
	@Override
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
	@Override
	protected URL findResource(String name) {
		for (String path : this.jarFiles) {
			File f = new File(path);

			if (f.exists()) {
				ZipFile zipFile = null;
				try {
					zipFile = new ZipFile(new File(path), 1);

					ZipEntry en = zipFile.getEntry(name);
					if (en != null) {
						return new URL("jar:file:/"+path+"!/"+name);

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					if (zipFile != null)
						try {
							zipFile.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}

		}
		return null;
	}

	public static void main(String[]args)
	{
		JARLoader loader=new JARLoader(JARLoader.class.getClassLoader());
		loader.addPath("C:\\Works\\DQ\\javademos\\PinyinPlugin\\out\\artifacts\\PinyinPlugin");
		URL url=loader.findResource("tgtools/plugins/pinyin/data/mutil_pinyin.dict");
		try {

			URLConnection urlc = url.openConnection();
			InputStream steam= urlc.getInputStream();
			System.out.println("steam:"+steam);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}