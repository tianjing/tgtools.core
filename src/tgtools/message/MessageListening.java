package tgtools.message;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：15:26
 */
public interface MessageListening {
    /**
     * 监听名称（也是接收名称）
     * @return
     */
    String getName();

    /**
     * 消息处理，如果不想让其他监听继续处理 可以将isComplete设置为true
     * @param p_Message
     */
    void onMessage(Message p_Message);

}
