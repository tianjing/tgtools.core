package tgtools.exceptions;

/**
 * 表示程序异常错误，必须处理
 * 
 * @author tianjing
 * 
 */
public class APPErrorException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6583911947820937015L;

	public APPErrorException(Throwable p_exception) {
		super(p_exception);
	}

	public APPErrorException(int p_errorCode) {
		this("", p_errorCode);
	}

	public APPErrorException(int p_errorCode, Throwable p_excpt) {
		this("", p_errorCode, p_excpt);
	}

	public APPErrorException(String p_excptMsg) {
		this(p_excptMsg, 0);
	}

	public APPErrorException(String p_excptMsg, Throwable p_excpt) {
		this( p_excptMsg, 0, p_excpt);
	}

	public APPErrorException(String p_excptMsg, int p_errorCode) {
		super(p_excptMsg);
		setErrorCode(p_errorCode);
	}

	public APPErrorException(String p_excptMsg, int p_errorCode,
			Throwable p_excpt) {
		super(p_excptMsg , p_excpt);
		setErrorCode(p_errorCode);
	}
}
