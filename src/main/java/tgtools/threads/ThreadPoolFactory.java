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
		threadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                0L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
		
	}

	private static ExecutorService threadPool;

	private synchronized static ExecutorService getPool() {
		return threadPool;
	}

	public static void addTask(Runnable pTask) {
		getPool().execute(pTask);
	}

}
