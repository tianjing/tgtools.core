package tgtools.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 线程池对象工厂
 * @author TianJing
 *
 */
public class ThreadPoolFactory {
	static{
		m_ThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                0L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());//java.util.concurrent.Executors.newCachedThreadPool()
		
	}

	private static ExecutorService m_ThreadPool;

	private synchronized static ExecutorService getPool() {
		return m_ThreadPool;
	}

	public static void addTask(Runnable p_Task) {
		getPool().execute(p_Task);
	}

}
