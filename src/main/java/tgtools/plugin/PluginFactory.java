package tgtools.plugin;

import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * @author tianjing
 */
public class PluginFactory {
    private static Hashtable<PluginInfo, Pluginloader> plugins;
    private static boolean isStartup;

    static {
        plugins = new Hashtable<PluginInfo, Pluginloader>();
        isStartup = false;
    }

    public static synchronized void delPlugin(String pName) {
        unloadPlugin(pName);
        Iterator<PluginInfo> it = plugins.keySet().iterator();
        while (it.hasNext()) {
            PluginInfo item = it.next();
            if (item.getName().equals(pName)) {
                it.remove();
            }
        }
    }

    public static boolean addPlugin(String pPath) {
        return addPlugin(new File(pPath), true);
    }

    /**
     * 加载指定路径中的一个插件
     *
     * @param pPath
     * @return
     */
    private static boolean addPlugin(File pPath, boolean pIgnoreIsLoad) {
        if (pPath.isDirectory()) {
            PluginInfo info = loadInfo(pPath);
            if (!pIgnoreIsLoad && !info.isIsload()) {
                return false;
            }

            if (!plugins.containsKey(info)) {
                Pluginloader plugin = createPlugin(info);
                if (null != plugin) {
                    plugins.put(info, plugin);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取所有插件信息
     *
     * @return
     * @author tian.jing
     * @date 2016年5月10日
     */
    public static Enumeration<PluginInfo> getPluginInfos() {
        return plugins.keys();
    }

    /**
     * 获取所有插件对象
     *
     * @return
     * @author tian.jing
     * @date 2016年5月10日
     */
    public static Set<PluginInfo> getPlugins() {
        return plugins.keySet();
    }

    public static IPlugin getPlugin(String name) {
        for (PluginInfo item : plugins.keySet()) {
            if (item.getName().equals(name)) {
                return plugins.get(item).getPlugin();
            }
        }
        return null;
    }

    private static Pluginloader getPluginloader(String name) {
        for (PluginInfo item : plugins.keySet()) {
            if (item.getName().equals(name)) {
                return plugins.get(item);
            }
        }
        return null;
    }

    /**
     * 加载插件
     *
     * @param pName
     * @return
     * @author tian.jing
     * @date 2016年5月10日
     */
    public static boolean loadPlugin(String pName) {
        Pluginloader loader = getPluginloader(pName);
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
     * @param pName
     * @return
     */
    public static boolean unloadPlugin(String pName) {
        Pluginloader loader = getPluginloader(pName);
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
     * @param pName
     */
    public static void resetPlugin(String pName) {
        unloadPlugin(pName);
        loadPlugin(pName);
    }

    private static Pluginloader createPlugin(PluginInfo pInfo) {
        if (null != pInfo && !StringUtil.isNullOrEmpty(pInfo.getName())) {
            Pluginloader plugin = new Pluginloader();
            plugin.init(pInfo);
            return plugin;
        }
        return null;
    }

    private static PluginInfo loadInfo(File pPath) {
        String[] files = tgtools.util.FileUtil.listFiles(
                pPath.getAbsolutePath(), new String[]{"xml"});
        if (null != files && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File file = new File(files[i]);
                if ("plugin.xml".equals(file.getName())) {

                    try {
                        Object obj = tgtools.util.XmlSerialize.deserialize(
                                tgtools.util.FileUtil.readFile(files[i]),
                                "PluginInfo", PluginInfo.class);

                        if (obj instanceof PluginInfo) {
                            PluginInfo info = (PluginInfo) obj;
                            if (!StringUtil.isNullOrEmpty(info.getName())) {
                                info.setPath(pPath.getAbsolutePath());
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
     * @param pPath
     */
    public synchronized static void startup(String pPath) {
        if (isStartup) {
            return;
        }

        isStartup = true;
        File[] files = tgtools.util.FileUtil.listAll(pPath);
        for (int i = 0; i < files.length; i++) {
            try {
                addPlugin(files[i], false);
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
            e.printStackTrace();
        }
        unloadPlugin(name);
        try {
            IPlugin plugin1 = getPlugin(name);
            System.out.println(plugin1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPlugin(name);
        try {
            IPlugin plugin2 = getPlugin(name);
            System.out.println(plugin2);
            plugin2.execute("");
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
