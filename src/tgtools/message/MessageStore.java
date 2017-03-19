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
public class MessageStore {
    private static LinkedBlockingQueue<Message> m_Messages = null;
    private static Element m_Element=null;
    private static Cache m_Cache=null;
    static {
        m_Messages = new LinkedBlockingQueue<Message>();
        m_Element = new Element("Message", m_Messages);
        m_Cache=CacheFactory.get(CacheFactory.EverCache);
        m_Cache.put(m_Element);
    }

    public static void saveMessage(Message p_Message) throws APPErrorException {
        try {
            m_Messages.put(p_Message);
            updateCache();
        } catch (InterruptedException e) {
            throw new APPErrorException("Message存储失败，原因：" + e.getMessage(), e);
        }
    }

    public static Message getMessage() throws APPErrorException {
        if (m_Messages.isEmpty()) {
            return null;
        }
        try {
            Message message= m_Messages.take();
            updateCache();
            return message;
        } catch (InterruptedException e) {
            throw new APPErrorException("Message取出失败，原因：" + e.getMessage(), e);
        }
    }
    private static void updateCache()
    {
        m_Cache.put(new Element("Message", m_Messages));
    }

}
