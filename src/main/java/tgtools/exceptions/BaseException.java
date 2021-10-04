package tgtools.exceptions;

class BaseException extends Exception {

    /**
     * 框架异常的基类型
     */
    private static final long serialVersionUID = 2792585915640297054L;
    private int errorCode = 0;

    public BaseException() {

    }

    public BaseException(String pMessage) {
        super(pMessage);
    }

    public BaseException(String pMessage, Throwable pException) {
        super(pMessage, pException);
    }

    public BaseException(Throwable pException) {
        super(pException);
    }


    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int pErrorCode) {
        this.errorCode = pErrorCode;
    }

    /**
     * 将异常类型转换成字符串
     */
    @Override
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
                new Object[]{Integer.valueOf(getErrorCode()), msg});
    }
}
