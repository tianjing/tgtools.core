package tgtools.tasks;

import tgtools.exceptions.APPErrorException;


/**
 * 表示Task 的异常 
 * @author tianjing
 *
 */
public class TaskException extends APPErrorException {

	public TaskException(Throwable pException) {
		super(pException);
	}

	public TaskException(int pErrorCode) {
		this("", pErrorCode);
	}

	public TaskException(int pErrorCode, Throwable pExcpt) {
		this("", pErrorCode, pExcpt);
	}

	public TaskException(String pExcptMsg) {
		this(pExcptMsg, 0);
	}

	public TaskException(String pExcptMsg, Throwable pExcpt) {
		this( pExcptMsg, 0, pExcpt);
	}

	public TaskException(String pExcptMsg, int pErrorCode) {
		super(pExcptMsg);
		setErrorCode(pErrorCode);
	}

	public TaskException(String pExcptMsg, int pErrorCode,
			Throwable pExcpt) {
		super(pExcptMsg , pExcpt);
		setErrorCode(pErrorCode);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5244499608026043142L;

}
