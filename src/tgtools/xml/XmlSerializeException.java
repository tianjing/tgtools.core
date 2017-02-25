package tgtools.xml;

import tgtools.exceptions.APPRuntimeException;

public class XmlSerializeException extends APPRuntimeException{
	  private static final long serialVersionUID = 8731745468346909325L;

	  public XmlSerializeException(Throwable p_exception)
	  {
	    super(p_exception);
	  }

	  public XmlSerializeException(String p_excptMsg)
	  {
	    super(p_excptMsg);
	  }

	  public XmlSerializeException(String p_excptMsg, Throwable p_excpt)
	  {
	    super(p_excptMsg, p_excpt);
	  }
}
