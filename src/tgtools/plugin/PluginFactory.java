package tgtools.plugin;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

public class PluginFactory {
	static {
		m_Plugins = new Hashtable<PluginInfo, Pluginloader>();
		m_IsStartup = false;
	}
	private static Hashtable<PluginInfo, Pluginloader> m_Plugins;
	private static boolean m_IsStartup;

	public static synchronized void  delPlugin(String p_Name)
	{
		unloadPlugin(p_Name);
//		for(PluginInfo item :m_Plugins.keySet())
//		{
//			if(item.getName().equals(p_Name))
//			{
//				m_Plugins.remove(item);
//			}
//		}
		Iterator<PluginInfo> it = m_Plugins.keySet().iterator();
		while(it.hasNext()) {
			PluginInfo item = it.next();
			if (item.getName().equals(p_Name))
				it.remove();
		}
	}
	public static boolean addPlugin(String p_Path)
	{
		return addPlugin(new File(p_Path));
	}
	/**
	 * 加载指定路径中的一个插件
	 * 
	 * @param p_Path
	 * @return
	 */
	private static boolean addPlugin(File p_Path) {
		if (p_Path.isDirectory()) {
			PluginInfo info = loadInfo(p_Path);
			if (!m_Plugins.containsKey(info)) {
				Pluginloader plugin = createPlugin(info);
				if (null != plugin) {
					m_Plugins.put(info, plugin);
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * 获取所有插件信息
	 * @author tian.jing
	 * @date 2016年5月10日
	 * @return
	 */
	public static Enumeration<PluginInfo> getPluginInfos()
	{
		return m_Plugins.keys();
	}
	/**
	 * 获取所有插件对象
	 * @author tian.jing
	 * @date 2016年5月10日
	 * @return
	 */
	public static Set<PluginInfo> getPlugins()
	{
		return m_Plugins.keySet();
	}
	public static IPlugin getPlugin(String name) {
		for (PluginInfo item : m_Plugins.keySet()) {
			if (item.getName().equals(name)) {
				return m_Plugins.get(item).getPlugin();
			}
		}
		return null;
	}
	
	private static Pluginloader getPluginloader(String name) {
		for (PluginInfo item : m_Plugins.keySet()) {
			if (item.getName().equals(name)) {
				return m_Plugins.get(item);
			}
		}
		return null;
	}
	/**
	 * 加载插件
	 * @author tian.jing
	 * @date 2016年5月10日
	 * @param p_Name
	 * @return
	 */
	public static boolean loadPlugin(String p_Name) {
		Pluginloader loader = getPluginloader(p_Name);
		if (null != loader) {
			if (!loader.isLoad()) {
				return loader.load();
			}
			return true;
		}
		return false;
	}

	/**
	 * 根据名称卸载一个插件
	 * 
	 * @param p_Name
	 * @return
	 */
	public static boolean unloadPlugin(String p_Name) {
		Pluginloader loader = getPluginloader(p_Name);
		if (null != loader) {
			if (loader.isLoad()) {
				 loader.unload();
				 return true;
			}
			return true;
		}
		return false;
	}

	/**
	 * 根据名称重置一个插件
	 * 
	 * @param p_Name
	 */
	public static void resetPlugin(String p_Name) {
		unloadPlugin(p_Name);
		loadPlugin(p_Name);
	}

	private static Pluginloader createPlugin(PluginInfo p_Info) {
		if (null != p_Info && !StringUtil.isNullOrEmpty(p_Info.getName())) {
			Pluginloader plugin = new Pluginloader();
			plugin.init(p_Info);
			return plugin;
		}
		return null;
	}

	private static PluginInfo loadInfo(File p_Path) {
		String[] files = tgtools.util.FileUtil.listFiles(
				p_Path.getAbsolutePath(), new String[] { "xml" });
		if (null != files && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				File file = new File(files[i]);
				if (file.getName().equals("plugin.xml")) {
					
					try {
						Object obj = tgtools.util.XmlSerialize.deserialize(
								tgtools.util.FileUtil.readFile(files[i]),
								"PluginInfo", PluginInfo.class);
						
						if (obj instanceof PluginInfo) {
							PluginInfo info = (PluginInfo) obj;
							if (!StringUtil.isNullOrEmpty(info.getName())) {
								info.setPath(p_Path.getAbsolutePath());
								return info;
							}
						}
					} catch (Exception e) {
						LogHelper.error("", "PluginInfo 反序列化失败", "PluginFactory.loadInfo", e);
					}
					
				}
			}
		}

		return null;
	}

	/**
	 * 加载指定路径下所有插件（只能调用一次）
	 * 
	 * @param p_Path
	 */
	public synchronized static void startup(String p_Path) {
		if (m_IsStartup)
			return;

		m_IsStartup = true;
		File[] files = tgtools.util.FileUtil.listAll(p_Path);
		for (int i = 0; i < files.length; i++) {
			try {
				addPlugin(files[i]);
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		String name = "test";
		startup("C:/Users/TianJing/Desktop/plugins");
		System.out.println("end");
		IPlugin plugin = getPlugin(name);
		try {
			plugin.execute("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unloadPlugin(name);
		try {
			IPlugin plugin1 = getPlugin(name);
			System.out.println(plugin1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadPlugin(name);
		try {
			IPlugin plugin2 = getPlugin(name);
			System.out.println(plugin2);
			plugin2.execute("");
			System.out.println("end");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
