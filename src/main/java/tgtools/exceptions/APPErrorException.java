package tgtools.exceptions;

/**
 * 表示程序异常错误，必须处理
 *
 * @author tianjing
 */
public class APPErrorException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 6583911947820937015L;

    public APPErrorException(Throwable pException) {
        super(pException);
    }

    public APPErrorException(int pErrorCode) {
        this("", pErrorCode);
    }

    public APPErrorException(int pErrorCode, Throwable pExcpt) {
        this("", pErrorCode, pExcpt);
    }

    public APPErrorException(String pExcptMsg) {
        this(pExcptMsg, 0);
    }

    public APPErrorException(String pExcptMsg, Throwable pExcpt) {
        this(pExcptMsg, 0, pExcpt);
    }

    public APPErrorException(String pExcptMsg, int pErrorCode) {
        super(pExcptMsg);
        setErrorCode(pErrorCode);
    }

    public APPErrorException(String pExcptMsg, int pErrorCode,
                             Throwable pExcpt) {
        super(pExcptMsg, pExcpt);
        setErrorCode(pErrorCode);
    }
}
