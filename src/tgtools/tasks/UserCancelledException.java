package tgtools.tasks;

import tgtools.exceptions.APPRuntimeException;


/**
 * 当 Task cancell 时 抛出的异常
 * @author tianjing
 *
 */
public class UserCancelledException extends APPRuntimeException {

	public UserCancelledException(String p_Message) {
		super(p_Message);
	}
	public UserCancelledException(Throwable p_Exception) {
		super(p_Exception);
	}
	public UserCancelledException(String p_Message, Throwable p_Exception) {
		super(p_Message,p_Exception);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6110180130007291345L;
	
}
