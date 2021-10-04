package tgtools.cache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
/**
 * 一个全局缓存池 ehcache
 * 可以自行增加定时缓存和永久缓存
 * @author TianJing
 *
 */
public class CacheFactory {

	private static CacheManager manager;
    public static final String  TimerCache="TimerCache";
    public static final String EverCache="EverCache";

    public static CacheManager getCacheManager()
	{
		return manager;
	}

	public static void  setCacheManager(CacheManager pCacheManager)
	{
		manager=pCacheManager;
	}

	public static void init() {
		URL url = CacheFactory.class.getResource("/ehcache.xml");

		manager = CacheManager.create(url);
	}
	public static void init(URL pUrl) {
		manager = CacheManager.create(pUrl);
	}
	/**
	 * 根据名称创建缓存区
	 * 
	 * @param pName
	 */
	public static Cache get(String pName) {
		return manager.getCache(pName);
	}

	/**
	 * 根据名称创建缓存区
	 * 
	 * @param pCache
	 */
	public static void create(Cache pCache) {
		if (!manager.cacheExists(pCache.getName())) {
			manager.addCache(pCache);
		}
	}
	public static void create(String pCacheName) {
		if (!manager.cacheExists(pCacheName)) {
			manager.addCache(pCacheName);
		}
	}
	/**
	 * 根据名称清除缓存区下所有缓存项
	 * 
	 * @param pName
	 */
	public static void clear(String pName) {
		manager.getCache(pName).removeAll();
	}

	/**
	 * 根据名称删除缓存区
	 *
	 * @param pName
	 */
	public static void remove(String pName) {
		manager.removeCache(pName);
	}

}
