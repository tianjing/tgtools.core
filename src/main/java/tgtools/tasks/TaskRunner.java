package tgtools.tasks;

import tgtools.interfaces.IDispose;
import tgtools.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：14:44
 */
public class TaskRunner<E extends Task>  extends  ArrayList<E> implements IDispose{


    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    @Deprecated
    public void runThreanTillEnd()
    {
        runThreanTillEnd(new TaskContext());
    }
    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreadTillEnd()
    {
        runThreadTillEnd(new TaskContext());
    }

    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    @Deprecated
    public void runThreanTillEnd(TaskContext p_TaskContext)
    {
        runThreadTillEnd(p_TaskContext);
    }
    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreadTillEnd(TaskContext p_TaskContext)
    {
        restCancel();
        for (int i = 0; i < this.size(); i++) {
            this.get(i).runThreadWait(p_TaskContext);
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
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    public void runThread(TaskContext p_TaskContext)
    {
        restCancel();
        for (int i = 0; i < this.size(); i++) {
            this.get(i).runThread(p_TaskContext);
        }
    }
    /**
     * 一个任务一个任务的执行（不使用线程）
     */
    public void run(TaskContext p_TaskContext)
    {
        restCancel();
        for (int i = 0; i < this.size(); i++) {
            try {
                this.get(i).run(p_TaskContext);
            }
            catch (Exception ex)
            {
                this.get(i).onError(ex);
            }
        }
    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    @Deprecated
    public void runThrean()
    {
        runThrean(new TaskContext());
    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    public void runThread()
    {
        runThrean(new TaskContext());
    }

    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    @Deprecated
    public void runThrean(TaskContext p_TaskContext)
    {
        runThread(p_TaskContext);
    }

    /**
     * 一个任务一个任务的执行（不使用线程）
     */
    public void run()
    {
        run(new TaskContext());
    }



    /**
     * 通过任务名称查找任务是否存在
     * @param p_TaskName
     * @return
     */
    public boolean hasTask(String p_TaskName)
    {
        if(StringUtil.isNullOrEmpty(p_TaskName))
        {
            return false;
        }
        for(int i=0;i<this.size();i++)
        {
            if(p_TaskName.equals(this.get(i).getName()))
            {
                return true;
            }
        }
        return false;
    }

    public void cancel()
    {
        for(int i=0;i<this.size();i++)
        {
          this.get(i).cancel();
        }
    }
    public void restCancel()
    {
        for(int i=0;i<this.size();i++)
        {
            this.get(i).resetCancel();
        }
    }
    @Override
    public void Dispose() {
        this.clear();
    }

}
