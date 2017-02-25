package tgtools.log;

public interface ILoger {
	public void error(Object p_Message, Throwable p_Error);
	
	public void warn( Object p_Message);
	
	public void warn(Object p_Message, Throwable p_Error);
	
	public void info(Object p_Message);

	public void info( Object p_Message, Throwable p_Error);

}
