package tgtools.exceptions;

/**
 * 表示程序警告型 错误，一般此类异常用于提示用户或者询问用户如何处理
 * 
 * @author tianjing
 * 
 */
public class APPWarningException extends BaseException {

	private static final long serialVersionUID = 6849567905200767852L;

	public APPWarningException(Throwable p_exception) {
		super(p_exception);
	}

	public APPWarningException(int p_errorCode) {
		this("", p_errorCode);
	}

	public APPWarningException(int p_errorCode, Throwable p_excpt) {
		this("", p_errorCode, p_excpt);
	}

	public APPWarningException(String p_excptMsg) {
		this(p_excptMsg, 0);
	}

	public APPWarningException(String p_excptMsg, Throwable p_excpt) {
		this(p_excptMsg , 0, p_excpt);
	}

	public APPWarningException(String p_excptMsg, int p_errorCode) {
		super(p_excptMsg);
		setErrorCode(p_errorCode);
	}

	public APPWarningException(String p_excptMsg, int p_errorCode,
			Throwable p_excpt) {
		super(p_excptMsg , p_excpt);
		setErrorCode(p_errorCode);
	}
}
