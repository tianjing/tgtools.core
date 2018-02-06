package tgtools.log;

import java.util.HashMap;

public class LoggerFactory {

	private static HashMap<String, ILoger> m_Logers;
	private static String m_DefaultName="Default";
	private static HashMap<String, ILoger> getLogers()
	{
		if(null==m_Logers)
		{
			m_Logers=new HashMap<String, ILoger>();
			m_Logers.put(m_DefaultName, new DefaultLoger(m_DefaultName));
		}
		return m_Logers;
	}
	public static ILoger getDefault()
	{
		return get(m_DefaultName);
	}
	/**
	 * 获取指定名称的日志，如果不存在则创建
	 * @param p_Name
	 * @return
	 */
	public static ILoger get(String p_Name)
	{
		if(!getLogers().containsKey(p_Name))
		{
			ILoger loger=createLoger(p_Name);
			if(null!=loger){
			getLogers().put(p_Name, loger);
			}
			else
			{
				return null;
			}
		}
		return getLogers().get(p_Name);
		
	}
	/**
	 * xinhuan
	 * 
	 * @param p_Name
	 * @return
	 */
	private static ILoger createLoger(String p_Name)
	{
		if(!getLogers().containsKey(p_Name))
		{
			ILoger loger=new DefaultLoger(p_Name);
			return loger;
		}
		return null;
	}
}
