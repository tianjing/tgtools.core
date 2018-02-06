package tgtools.message;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import tgtools.cache.CacheFactory;
import tgtools.exceptions.APPErrorException;
import tgtools.util.GUID;

import java.util.ArrayList;
import java.util.List;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：11:31
 */
public class MessageEhcacheStore implements IMessageStore {
    private List<String> m_Keys = new ArrayList<String>();
    private Cache m_Cache = null;
    public MessageEhcacheStore() {
        if (null == CacheFactory.get("MessageCache")) {
            Cache cache = new Cache("MessageCache", 100000, false, true, 0, 0);
            CacheFactory.create(cache);
        }
        m_Cache = CacheFactory.get("MessageCache");
        m_Cache.getCacheEventNotificationService().registerListener(new MyCacheEventListener(m_Keys));
    }

    @Override
    public void addMessage(Message p_Message) throws APPErrorException {
        if (null == m_Cache) {
            throw new APPErrorException("无效的消息缓存");
        }
        String key = GUID.newGUID();
        m_Keys.add(key);
        m_Cache.put(new Element(key, p_Message));
    }

    @Override
    public Message getMessage() throws APPErrorException {
        if (null == m_Cache) {
            throw new APPErrorException("无效的消息缓存");
        }
        if (m_Keys.size() < 1) {
            return null;
        }

        String key = m_Keys.get(0);
        Message message = null == m_Cache.get(key) ? null : (Message) m_Cache.get(key).getObjectValue();
        removeMessage(key);
        return message;

    }

    private void removeMessage(String p_Key) {
        m_Keys.remove(p_Key);
        m_Cache.remove(p_Key);
    }

    private static class MyCacheEventListener implements CacheEventListener
    {
        private List<String> m_Keys;
        MyCacheEventListener(List<String> p_Keys)
        {
            m_Keys=p_Keys;
        }

        @Override
        public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        }

        @Override
        public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
            if(null!=element.getObjectKey())
                m_Keys.add(element.getObjectKey().toString());
        }

        @Override
        public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {

        }

        @Override
        public void notifyElementExpired(Ehcache ehcache, Element element) {

        }

        @Override
        public void notifyElementEvicted(Ehcache ehcache, Element element) {

        }

        @Override
        public void notifyRemoveAll(Ehcache ehcache) {

        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return null;
        }

        @Override
        public void dispose() {

        }
    }
}
