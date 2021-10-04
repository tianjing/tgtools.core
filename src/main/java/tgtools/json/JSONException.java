package tgtools.json;

import tgtools.exceptions.APPErrorException;
/**
 * @author tianjing
 */
public class JSONException extends APPErrorException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public JSONException(Throwable pException) {
		super(pException);
	}

	public JSONException(int pErrorCode) {
		this("", pErrorCode);
	}

	public JSONException(int pErrorCode, Throwable pExcpt) {
		this("", pErrorCode, pExcpt);
	}

	public JSONException(String pExcptMsg) {
		this(pExcptMsg, 0);
	}

	public JSONException(String pExcptMsg, Throwable pExcpt) {
		this( pExcptMsg, 0, pExcpt);
	}

	public JSONException(String pExcptMsg, int pErrorCode) {
		super(pExcptMsg);
		setErrorCode(pErrorCode);
	}

	public JSONException(String pExcptMsg, int pErrorCode,
			Throwable pExcpt) {
		super(pExcptMsg , pExcpt);
		setErrorCode(pErrorCode);
	}

}
