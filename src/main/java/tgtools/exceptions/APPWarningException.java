package tgtools.exceptions;

/**
 * 表示程序警告型 错误，一般此类异常用于提示用户或者询问用户如何处理
 *
 * @author tianjing
 */
public class APPWarningException extends BaseException {

    private static final long serialVersionUID = 6849567905200767852L;

    public APPWarningException(Throwable pException) {
        super(pException);
    }

    public APPWarningException(int pErrorCode) {
        this("", pErrorCode);
    }

    public APPWarningException(int pErrorCode, Throwable pExcpt) {
        this("", pErrorCode, pExcpt);
    }

    public APPWarningException(String pMessage) {
        this(pMessage, 0);
    }

    public APPWarningException(String pMessage, Throwable pExcpt) {
        this(pMessage, 0, pExcpt);
    }

    public APPWarningException(String pMessage, int pErrorCode) {
        super(pMessage);
        setErrorCode(pErrorCode);
    }

    public APPWarningException(String pMessage, int pErrorCode,
                               Throwable pException) {
        super(pMessage, pException);
        setErrorCode(pErrorCode);
    }
}
