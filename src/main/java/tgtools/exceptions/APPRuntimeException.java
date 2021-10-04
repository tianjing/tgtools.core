package tgtools.exceptions;

/**
 * 表示程序运行异常，不需要处理的异常（不影响程序的功能，也不需要提示用户）
 * @author tianjing
 *
 */
public class APPRuntimeException extends RuntimeException{


	private static final long serialVersionUID = 6358732841528232544L;
	
	public APPRuntimeException(Throwable pException) {
		super(pException);
	}
	public APPRuntimeException(String pMessage) {
		super(pMessage);
	}
	public APPRuntimeException(String pMessage, Throwable pException) {
		super(pMessage,pException);
	}
}
