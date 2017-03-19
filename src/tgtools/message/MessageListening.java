package tgtools.message;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：15:26
 */
public interface MessageListening {
    String getName();
    void onMessage(Message p_Message);

}
