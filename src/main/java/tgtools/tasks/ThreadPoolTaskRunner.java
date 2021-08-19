package tgtools.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池 运行器
 * @author 田径
 * @date 2021-08-17 14:51
 * @desc
 **/
public class ThreadPoolTaskRunner<T extends Task> extends TaskRunner<T> {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolTaskRunner.class);

    protected ExecutorService executorService = new ThreadPoolExecutor(50, 50,
            10L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public ThreadPoolTaskRunner() {

    }

    public ThreadPoolTaskRunner(ThreadPoolExecutor pThreadPoolExecutor) {
        executorService = pThreadPoolExecutor;
    }

    /**
     * 线程方式运行
     * @param pTaskContext
     */
    @Override
    public void runThread(TaskContext pTaskContext) {
        for (int i = 0; i < size(); i++) {
            get(i).runThread(pTaskContext, executorService);
        }
    }

    /**
     * 线程方式运行并阻塞等待结束
     */
    @Override
    public void runThreadTillEnd() {
        this.runThreadTillEnd(null);
    }

    /**
     * 线程方式运行并阻塞等待结束
     * @param pTaskContext
     */
    @Override
    public void runThreadTillEnd(TaskContext pTaskContext) {
        for (int i = 0; i < size(); i++) {
            get(i).runThreadWait(pTaskContext, executorService);
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).isBusy()) {
                synchronized (this.get(i)) {
                    try {
                        this.get(i).wait();
                        log.info("wait end");
                    } catch (InterruptedException e) {
                        log.error("runThreadTillEnd error ", e);
                    }
                }
            }
        }
    }

    /**
     * 所有任务执行完成后清除
     */
    public void clearAllTillEnd() {
        for (; size() > 0; ) {
            if (this.get(0).isBusy()) {
                synchronized (this.get(0)) {
                    try {
                        this.get(0).wait();
                    } catch (InterruptedException e) {
                        log.error("clearAllTillEnd error ", e);
                    }
                    this.remove(this.get(0));
                }
            } else {
                this.remove(this.get(0));
            }
        }
    }
}
