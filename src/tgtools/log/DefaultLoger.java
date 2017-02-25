package tgtools.log;

import org.apache.log4j.Logger;

public class DefaultLoger implements ILoger{

	public DefaultLoger(String p_Name)
	{
		m_Name=p_Name;
	}
	private String m_Name;
	@Override
	public void error( Object p_Message, Throwable p_Error) {
		getLoger().error(p_Message, p_Error);
	}
	@Override
	public void warn( Object p_Message) {
		getLoger().warn(p_Message);
	}
	@Override
	public void warn(Object p_Message, Throwable p_Error) {
		getLoger().info(p_Message, p_Error);
	}
	@Override
	public void info( Object p_Message) {
		getLoger().info(p_Message);
	}
	@Override
	public void info( Object p_Message, Throwable p_Error) {
		getLoger().info(p_Message, p_Error);
	}

	private Logger getLoger() {
		return Logger.getLogger(m_Name);
	}

}
