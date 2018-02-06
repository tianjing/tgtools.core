package tgtools.plugin;

public interface IPlugin {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void load()throws Exception;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void unload()throws Exception;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Object execute(Object... params)throws Exception;
	
}
