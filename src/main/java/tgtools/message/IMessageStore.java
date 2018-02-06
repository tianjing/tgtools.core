package tgtools.message;

import tgtools.exceptions.APPErrorException;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：9:31
 */
public interface IMessageStore {

    /**
     * 添加消息
     * @param p_Message
     */
    void addMessage(Message p_Message)  throws APPErrorException;

    /**
     * 从队列中获取一个消息
     */
    Message getMessage()  throws APPErrorException;

}
