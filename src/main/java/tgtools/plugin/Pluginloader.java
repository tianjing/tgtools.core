package tgtools.plugin;

import tgtools.plugin.util.JARLoader;
import tgtools.util.LogHelper;

/**
 * @author tianjing
 */
public class Pluginloader {

    private JARLoader jARLoader;
    private IPlugin plugin;
    private PluginInfo info;
    private boolean IsLoad;
    public Pluginloader() {
        IsLoad = false;
    }

    public boolean isLoad() {
        return IsLoad;
    }

    public IPlugin getPlugin() {
        return plugin;
    }

    public void init(PluginInfo pInfo) {
        info = pInfo;
        if (info.isIsload()) {
            load(info);
        }
    }

    public boolean load() {
        return load(info);
    }

    private boolean load(PluginInfo info) {
        jARLoader = new JARLoader(ClassLoader.getSystemClassLoader());
        jARLoader.addPath(info.getPath());
        try {
            Class<?> clazz = jARLoader.loadClass(info.getClassname());
            if (null != clazz) {
                Object obj = clazz.newInstance();
                if (null != obj && obj instanceof IPlugin) {
                    plugin = (IPlugin) obj;
                    plugin.load();
                    IsLoad = true;
                    return true;
                }
            }
        } catch (Exception e) {
            LogHelper.error("", "插件加载出错；原因：" + e.getMessage(), "Pluginloader", e);
        }
        return false;
    }

    public void unload() {
        if (null != plugin) {
            try {
                plugin.unload();
            } catch (Exception e) {
                LogHelper.error("", "插件卸载出错；原因：" + e.getMessage(), "Pluginloader", e);
            }
        }
        jARLoader = null;
        plugin = null;
        IsLoad = false;
    }

    public Object execute(Object... params) throws Exception {
        return plugin.execute(params);
    }
}
