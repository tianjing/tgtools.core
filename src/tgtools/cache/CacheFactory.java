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

	private static CacheManager m_Manager;
    public static final String TimerCache="TimerCache";
    public static final String EverCache="EverCache";
    
	public static void init() {
		URL url = CacheFactory.class.getResource("/ehcache.xml");

		m_Manager = CacheManager.create(url);
	}
	public static void init(URL p_Url) {
		m_Manager = CacheManager.create(p_Url);
	}
	/**
	 * 根据名称创建缓存区
	 * 
	 * @param p_Name
	 */
	public static Cache get(String p_Name) {
		return m_Manager.getCache(p_Name);
	}

	/**
	 * 根据名称创建缓存区
	 * 
	 * @param p_Cache
	 */
	public static void create(Cache p_Cache) {
		if (!m_Manager.cacheExists(p_Cache.getName())) {
			m_Manager.addCache(p_Cache);
		}
	}
	public static void create(String p_CacheName) {
		if (!m_Manager.cacheExists(p_CacheName)) {
			m_Manager.addCache(p_CacheName);
		}
	}
	/**
	 * 根据名称清除缓存区下所有缓存项
	 * 
	 * @param p_Name
	 */
	public static void clear(String p_Name) {
		m_Manager.getCache(p_Name).removeAll();
	}

	/**
	 * 根据名称删除缓存区
	 * 
	 * @param p_Name
	 */
	public static void remove(String p_Name) {
		m_Manager.removeCache(p_Name);
	}
	
	public static void main(String[] args)
	{
		init();
        String[] strs =m_Manager.getCacheNames();
        for(String item:strs)
		System.out.println(item);

		
	}
}
