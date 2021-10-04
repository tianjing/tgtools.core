package tgtools.log;
/**
 * @author tianjing
 */
public interface ILoger {
    /**
     * 错误
     * @param pMessage
     * @param pError
     */
    void error(Object pMessage, Throwable pError);

    /**
     * 告警
     * @param pMessage
     */
    void warn(Object pMessage);

    /**
     * 告警
     * @param pMessage
     * @param pError
     */
    void warn(Object pMessage, Throwable pError);

    /**
     * 信息
     * @param pMessage
     */
    void info(Object pMessage);


}
