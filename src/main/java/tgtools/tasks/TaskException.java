package tgtools.tasks;

import tgtools.exceptions.APPErrorException;


/**
 * 表示Task 的异常 
 * @author tianjing
 *
 */
public class TaskException extends APPErrorException {

	public TaskException(Throwable p_exception) {
		super(p_exception);
	}

	public TaskException(int p_errorCode) {
		this("", p_errorCode);
	}

	public TaskException(int p_errorCode, Throwable p_excpt) {
		this("", p_errorCode, p_excpt);
	}

	public TaskException(String p_excptMsg) {
		this(p_excptMsg, 0);
	}

	public TaskException(String p_excptMsg, Throwable p_excpt) {
		this( p_excptMsg, 0, p_excpt);
	}

	public TaskException(String p_excptMsg, int p_errorCode) {
		super(p_excptMsg);
		setErrorCode(p_errorCode);
	}

	public TaskException(String p_excptMsg, int p_errorCode,
			Throwable p_excpt) {
		super(p_excptMsg , p_excpt);
		setErrorCode(p_errorCode);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5244499608026043142L;

}
