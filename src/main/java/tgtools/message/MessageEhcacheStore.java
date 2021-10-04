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
 * @author tianjing
 * 功  能：
 * 时  间：11:31
 */
public class MessageEhcacheStore implements IMessageStore {
    private List<String> keys = new ArrayList<String>();
    private Cache cache = null;
    public MessageEhcacheStore() {
        if (null == CacheFactory.get("MessageCache")) {
            Cache cache = new Cache("MessageCache", 100000, false, true, 0, 0);
            CacheFactory.create(cache);
        }
        cache = CacheFactory.get("MessageCache");
        cache.getCacheEventNotificationService().registerListener(new MyCacheEventListener(keys));
    }

    @Override
    public void addMessage(Message pMessage) throws APPErrorException {
        if (null == cache) {
            throw new APPErrorException("无效的消息缓存");
        }
        String key = GUID.newGUID();
        keys.add(key);
        cache.put(new Element(key, pMessage));
    }

    @Override
    public Message getMessage() throws APPErrorException {
        if (null == cache) {
            throw new APPErrorException("无效的消息缓存");
        }
        if (keys.size() < 1) {
            return null;
        }

        String key = keys.get(0);
        Message message = null == cache.get(key) ? null : (Message) cache.get(key).getObjectValue();
        removeMessage(key);
        return message;

    }

    private void removeMessage(String pKey) {
        keys.remove(pKey);
        cache.remove(pKey);
    }

    private static class MyCacheEventListener implements CacheEventListener
    {
        private List<String> keys;
        MyCacheEventListener(List<String> pKeys)
        {
            keys=pKeys;
        }

        @Override
        public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        }

        @Override
        public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
            if(null!=element.getObjectKey()) {
                keys.add(element.getObjectKey().toString());
            }
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
