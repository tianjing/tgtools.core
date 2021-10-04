package tgtools.log;


import org.apache.log4j.Logger;
/**
 * @author tianjing
 */
public class Log4jLoger implements ILoger{

	public Log4jLoger(String pName)
	{
		m_Name=pName;
	}
	private String m_Name;
	@Override
	public void error(Object pMessage, Throwable pError) {
		getLoger().error(pMessage, pError);
	}
	@Override
	public void warn(Object pMessage) {
		getLoger().warn(pMessage);
	}
	@Override
	public void warn(Object pMessage, Throwable pError) {
		getLoger().warn(pMessage, pError);
	}
	
	@Override
	public void info(Object pMessage) {
		getLoger().info(pMessage);
	}

	private Logger getLoger() {
		return Logger.getLogger(m_Name);
	}

}
