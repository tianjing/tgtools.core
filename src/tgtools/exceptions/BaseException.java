package tgtools.exceptions;

class BaseException  extends Exception{

	/**
	 * 框架异常的基类型
	 */
	private static final long serialVersionUID = 2792585915640297054L;
	private int errorCode = 0;
	public BaseException() {
		
	}

	public BaseException(String p_Message) {
		super(p_Message);
	}

	public BaseException(String p_Message, Throwable p_Exception) {
		super(p_Message,p_Exception);
	}

	public BaseException(Throwable p_Exception) {
		super(p_Exception);
	}


	public int getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(int p_errorCode) {
		this.errorCode = p_errorCode;
	}
/**
 * 将异常类型转换成字符串
 */
	public String toString() {
		String msg = super.toString();
		Throwable th = getCause();
		while (th != null) {
			msg = msg + th.toString();
			th = th.getCause();
		}
		if (getErrorCode() == 0) {
			return msg;
		}

		return String.format("APPError-%1$s: %2$s",
				new Object[] { Integer.valueOf(getErrorCode()), msg });
	}
}
