package tgtools.plugin;

import tgtools.plugin.util.JARLoader;
import tgtools.util.LogHelper;

public class Pluginloader {

	public Pluginloader()
	{
		m_IsLoad=false;
	}
	private JARLoader m_JARLoader;
	private IPlugin m_Plugin;
	private PluginInfo m_Info;
	private boolean m_IsLoad;
	public boolean isLoad()
	{
		return m_IsLoad;
	}
	public IPlugin getPlugin()
	{
		return m_Plugin;
	}
	public void init(PluginInfo p_Info)
	{
		m_Info=p_Info;
		if(m_Info.isIsload())
		{load(m_Info);}
	}
	public boolean load()
	{
		return load(m_Info);
	}
	private boolean load(PluginInfo info)
	{
		m_JARLoader=new JARLoader(ClassLoader.getSystemClassLoader());
		m_JARLoader.addPath(info.getPath());
		try {
			Class<?> clazz= m_JARLoader.loadClass(info.getClassname());
			if(null!=clazz)
			{
				Object obj= clazz.newInstance();
				if(null!=obj&& obj instanceof IPlugin){
				m_Plugin=(IPlugin)obj;
				m_Plugin.load();
				m_IsLoad=true;
				return true;
				}
			}
		} catch (Exception e) {
			LogHelper.error("","插件加载出错；原因："+e.getMessage(),"Pluginloader",e);
		}
		return false;
	}
	public void unload()
	{
		if(null!=m_Plugin)
		{
			try {
				m_Plugin.unload();
			} catch (Exception e) {
				LogHelper.error("","插件卸载出错；原因："+e.getMessage(),"Pluginloader",e);
			}
		}
		m_JARLoader=null;
		m_Plugin=null;
		m_IsLoad=false;
	}
	
	public Object execute(Object...params) throws Exception
	{
		return m_Plugin.execute(params);
	}
}
