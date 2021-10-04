package tgtools.tasks;

import tgtools.exceptions.APPRuntimeException;


/**
 * 当 Task cancell 时 抛出的异常
 * @author tianjing
 *
 */
public class UserCancelledException extends APPRuntimeException {

	public UserCancelledException(String pMessage) {
		super(pMessage);
	}
	public UserCancelledException(Throwable pException) {
		super(pException);
	}
	public UserCancelledException(String pMessage, Throwable pException) {
		super(pMessage,pException);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6110180130007291345L;
	
}
