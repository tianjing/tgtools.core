package tgtools.xml;

import tgtools.exceptions.APPErrorException;


public class XMLStreamException extends APPErrorException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7342124783615934823L;
	protected Throwable nested;



    public XMLStreamException(String paramString) {
        super(paramString);
    }

    public XMLStreamException(Throwable paramThrowable) {
        super(paramThrowable);
        this.nested = paramThrowable;
    }

    public XMLStreamException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
        this.nested = paramThrowable;
    }



    public Throwable getNestedException() {
        return this.nested;
    }


}
