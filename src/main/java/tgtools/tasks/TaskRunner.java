package tgtools.tasks;

import tgtools.interfaces.IDispose;
import tgtools.util.StringUtil;

import java.util.ArrayList;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：14:44
 */
public class TaskRunner<E extends Task> extends ArrayList<E> implements IDispose {


    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    @Deprecated
    public void runThreanTillEnd() {
        runThreanTillEnd(new TaskContext());
    }

    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreadTillEnd() {
        runThreadTillEnd(new TaskContext());
    }

    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    @Deprecated
    public void runThreanTillEnd(TaskContext pTaskContext) {
        runThreadTillEnd(pTaskContext);
    }

    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreadTillEnd(TaskContext pTaskContext) {
        restCancel();
        for (int i = 0; i < this.size(); i++) {
            this.get(i).runThreadWait(pTaskContext);
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).isBusy()) {
                synchronized (this.get(i)) {
                    try {
                        this.get(i).wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreadTillEndWithOutLock() {
        runThreadTillEndWithOutLock(new TaskContext(), 0);
    }

    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     * 使用循环等待，和runThreadTillEnd 用 synchronized 锁的方式区分开
     * 因为如果 task 内部有锁的话 容易引起死锁
     *
     * @param pTaskContext
     * @param pWaitMillis   等待间隔  默认100ms
     */
    public void runThreadTillEndWithOutLock(TaskContext pTaskContext, int pWaitMillis) {
        restCancel();

        if (pWaitMillis < 1) {
            pWaitMillis = 100;
        }

        for (int i = 0; i < this.size(); i++) {
            this.get(i).runThread(pTaskContext);
        }

        while (true) {
            boolean allend = true;
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i).isBusy()) {
                    allend = false;
                    try {
                        Thread.sleep(pWaitMillis);
                    } catch (InterruptedException e) {
                    }

                    continue;
                }
            }

            if (allend) {
                break;
            }
        }

    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    public void runThread(TaskContext pTaskContext) {
        restCancel();
        for (int i = 0; i < this.size(); i++) {
            this.get(i).runThread(pTaskContext);
        }
    }

    /**
     * 一个任务一个任务的执行（不使用线程）
     */
    public void run(TaskContext pTaskContext) {
        restCancel();
        for (int i = 0; i < this.size(); i++) {
            try {
                this.get(i).run(pTaskContext);
            } catch (Exception ex) {
                this.get(i).onError(ex);
            }
        }
    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    @Deprecated
    public void runThrean() {
        runThrean(new TaskContext());
    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    public void runThread() {
        runThrean(new TaskContext());
    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    @Deprecated
    public void runThrean(TaskContext pTaskContext) {
        runThread(pTaskContext);
    }

    /**
     * 一个任务一个任务的执行（不使用线程）
     */
    public void run() {
        run(new TaskContext());
    }


    /**
     * 通过任务名称查找任务是否存在
     *
     * @param pTaskName
     * @return
     */
    public boolean hasTask(String pTaskName) {
        if (StringUtil.isNullOrEmpty(pTaskName)) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (pTaskName.equals(this.get(i).getName())) {
                return true;
            }
        }
        return false;
    }

    public void cancel() {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).cancel();
        }
    }

    public void restCancel() {
        for (int i = 0; i < this.size(); i++) {
            this.get(i).resetCancel();
        }
    }

    @Override
    public void Dispose() {
        this.clear();
    }

}
