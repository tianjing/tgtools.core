package tgtools.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * slf4j 默认的logger
 * @author tianjing
 */
public class DefaultLoger implements ILoger{

	public DefaultLoger(String pName)
	{
		m_Name=pName;
	}
	private String m_Name;
	@Override
	public void error(Object pMessage, Throwable pError) {
		getLoger().error(MarkerFactory.getMarker("error"),"{}",pMessage, pError);
	}
	@Override
	public void warn(Object pMessage) {
		getLoger().warn("{}",pMessage);
	}
	@Override
	public void warn(Object pMessage, Throwable pError) {
		getLoger().warn(MarkerFactory.getMarker("warn"),"{}",pMessage, pError);
	}
	
	@Override
	public void info(Object pMessage) {
		getLoger().info("{}",pMessage);
	}

	private Logger getLoger() {
		return LoggerFactory.getLogger(m_Name);
	}

}
