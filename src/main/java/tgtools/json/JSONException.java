package tgtools.json;

import tgtools.exceptions.APPErrorException;

public class JSONException extends APPErrorException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public JSONException(Throwable p_exception) {
		super(p_exception);
	}

	public JSONException(int p_errorCode) {
		this("", p_errorCode);
	}

	public JSONException(int p_errorCode, Throwable p_excpt) {
		this("", p_errorCode, p_excpt);
	}

	public JSONException(String p_excptMsg) {
		this(p_excptMsg, 0);
	}

	public JSONException(String p_excptMsg, Throwable p_excpt) {
		this( p_excptMsg, 0, p_excpt);
	}

	public JSONException(String p_excptMsg, int p_errorCode) {
		super(p_excptMsg);
		setErrorCode(p_errorCode);
	}

	public JSONException(String p_excptMsg, int p_errorCode,
			Throwable p_excpt) {
		super(p_excptMsg , p_excpt);
		setErrorCode(p_errorCode);
	}

}
