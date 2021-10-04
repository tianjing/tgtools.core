package tgtools.data;

import tgtools.exceptions.APPRuntimeException;

/**
 *
 * @author tianjing
 */
public class DataAccessException extends APPRuntimeException{

	  /**
	 * 
	 */
	private static final long serialVersionUID = 3769316662937980305L;

	public DataAccessException(String pMessage)
	  {
	    super(pMessage);
	  }

	  public DataAccessException(String pMessage, Throwable pException)
	  {
	    super(pMessage, pException);
	  }

	
}
