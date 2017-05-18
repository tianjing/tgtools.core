package tgtools.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：14:44
 */
public class TaskRunner<T extends Task> {

    private ArrayList<T> tasks=new ArrayList<T>();

    /**
     * 添加任务
     * @param p_Task
     */
    public void add(T p_Task)
    {
        tasks.add(p_Task);
    }

    /**
     * 添加所有任务
     * @param p_Tasks
     */
    public void addAll(List<T> p_Tasks)
    {
        tasks.addAll(p_Tasks);
    }

    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreanTillEnd()
    {
        runThreanTillEnd(new TaskContext());
    }
    /**
     * 运行所有任务直到全部结束（一个任务一个线程）
     */
    public void runThreanTillEnd(TaskContext p_TaskContext)
    {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).runThreadWait(p_TaskContext);
        }
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isBusy()) {
                synchronized (tasks.get(i)) {
                    try {
                        tasks.get(i).wait();
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
    public void runThrean()
    {
        runThrean(new TaskContext());
    }
    /**
     * 运行所有任务 不等待 （一个任务一个线程）
     */
    public void runThrean(TaskContext p_TaskContext)
    {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).runThread(p_TaskContext);
        }
    }

    /**
     * 一个任务一个任务的执行（不使用线程）
     */
    public void run()
    {
        run(new TaskContext());
    }

    /**
     * 一个任务一个任务的执行（不使用线程）
     */
    public void run(TaskContext p_TaskContext)
    {
        for (int i = 0; i < tasks.size(); i++) {
            try {
                tasks.get(i).run(p_TaskContext);
            }
            catch (Exception ex)
            {
                tasks.get(i).onError(ex);
            }
        }
    }
}
