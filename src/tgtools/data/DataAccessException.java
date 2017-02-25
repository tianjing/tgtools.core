package tgtools.data;

import tgtools.exceptions.APPRuntimeException;

public class DataAccessException extends APPRuntimeException{

	  /**
	 * 
	 */
	private static final long serialVersionUID = 3769316662937980305L;

	public DataAccessException(String p_excptMsg)
	  {
	    super(p_excptMsg);
	  }

	  public DataAccessException(String p_excptMsg, Throwable p_excpt)
	  {
	    super(p_excptMsg, p_excpt);
	  }

	
}
