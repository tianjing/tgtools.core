package tgtools.exceptions;

/**
 * 表示程序运行异常，不需要处理的异常（不影响程序的功能，也不需要提示用户）
 * @author tianjing
 *
 */
public class APPRuntimeException extends RuntimeException{


	private static final long serialVersionUID = 6358732841528232544L;
	
	public APPRuntimeException(Throwable p_Exception) {
		super(p_Exception);
	}
	public APPRuntimeException(String p_Message) {
		super(p_Message);
	}
	public APPRuntimeException(String p_Message, Throwable p_Exception) {
		super(p_Message,p_Exception);
	}
}
