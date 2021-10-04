package tgtools.message;

/**
 * @author tianjing
 */
public interface IMessageListening {
    /**
     * 监听名称（也是接收名称）
     * @return
     */
    String getName();

    /**
     * 消息处理，如果不想让其他监听继续处理 可以将isComplete设置为true
     * @param pMessage
     */
    void onMessage(Message pMessage);

}
