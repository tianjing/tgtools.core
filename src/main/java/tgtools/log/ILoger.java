package tgtools.log;

public interface ILoger {
    void error(Object p_Message, Throwable p_Error);

    void warn(Object p_Message);

    void warn(Object p_Message, Throwable p_Error);

    void info(Object p_Message);


}
