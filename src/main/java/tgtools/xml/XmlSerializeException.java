package tgtools.xml;

import tgtools.exceptions.APPRuntimeException;
/**
 * @author tianjing
 */
public class XmlSerializeException extends APPRuntimeException{
	  private static final long serialVersionUID = 8731745468346909325L;

	  public XmlSerializeException(Throwable pException)
	  {
	    super(pException);
	  }

	  public XmlSerializeException(String pExcptMsg)
	  {
	    super(pExcptMsg);
	  }

	  public XmlSerializeException(String pExcptMsg, Throwable pExcpt)
	  {
	    super(pExcptMsg, pExcpt);
	  }
}
