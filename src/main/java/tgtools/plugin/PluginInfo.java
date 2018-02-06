package tgtools.plugin;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class PluginInfo {

	/**
	 * 插件唯一标识（不可重复）
	 */
	@Element(name="name")
	private String name;
	/**
	 * 插件的主类的全名 如 tgtools.plugin.core.PluginInfo
	 */
	@Element(name="classname")
	private String classname;
	/**
	 * 插件路径
	 */
	private String path;
	
	/**
	 * 是否加载插件
	 */
	@Element(name="isload")
	private Boolean isload;
	/**
	 * 描述
	 */
	@Element(required=false)
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public boolean isIsload() {
		return isload;
	}
	public void setIsload(boolean isload) {
		this.isload = isload;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(null!=obj&&obj instanceof PluginInfo)
		{
			PluginInfo info =(PluginInfo)obj;
			return this.name.equals(info.getName())|| this.path.equals(info.getPath());
		}
		return false;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
