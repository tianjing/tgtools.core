package tgtools.message;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import tgtools.cache.CacheFactory;
import tgtools.exceptions.APPErrorException;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：18:23
 */
public class MessageLocalStore implements IMessageStore {
    private  LinkedBlockingQueue<Message> m_Messages = new LinkedBlockingQueue<Message>();


    @Override
    public  void addMessage(Message p_Message) throws APPErrorException {
        try {
            m_Messages.put(p_Message);
        } catch (InterruptedException e) {
            throw new APPErrorException("Message存储失败，原因：" + e.getMessage(), e);
        }
    }

    @Override
    public Message getMessage() throws APPErrorException {
        if (m_Messages.isEmpty()) {
            return null;
        }
        try {
            Message message= m_Messages.take();
            return message;
        } catch (InterruptedException e) {
            throw new APPErrorException("Message取出失败，原因：" + e.getMessage(), e);
        }
    }


}
