package tgtools.message;

import tgtools.exceptions.APPErrorException;

/**
 * @author tianjing
 */
public interface IMessageStore {

    /**
     * 添加消息
     * @param pMessage
     * @throws APPErrorException
     */
    void addMessage(Message pMessage)  throws APPErrorException;

    /**
     * 从队列中获取一个消息
     * @throws APPErrorException
     * @return
     */
    Message getMessage()  throws APPErrorException;

}
