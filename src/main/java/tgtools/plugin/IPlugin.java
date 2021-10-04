package tgtools.plugin;
/**
 * @author tianjing
 */
public interface IPlugin {

	/**
	 * load
	 * @throws Exception
	 */
	void load()throws Exception;

	/**
	 * unload
	 * @throws Exception
	 */
	void unload()throws Exception;

	/**
	 * execute
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Object execute(Object... params)throws Exception;
	
}
