package tgtools.message;

import tgtools.exceptions.APPErrorException;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 名  称：本机消息缓存 利用本机的内存存储或取出消息
 * @author tianjing
 * 功  能：
 * 时  间：18:23
 */
public class MessageLocalStore implements IMessageStore {
    private  LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<Message>();


    @Override
    public  void addMessage(Message pMessage) throws APPErrorException {
        try {
            messages.put(pMessage);
        } catch (InterruptedException e) {
            throw new APPErrorException("Message存储失败，原因：" + e.getMessage(), e);
        }
    }

    @Override
    public Message getMessage() throws APPErrorException {
        if (messages.isEmpty()) {
            return null;
        }
        try {
            Message message= messages.take();
            return message;
        } catch (InterruptedException e) {
            throw new APPErrorException("Message取出失败，原因：" + e.getMessage(), e);
        }
    }


}
