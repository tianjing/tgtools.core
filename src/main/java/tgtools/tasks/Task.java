package tgtools.tasks;

import tgtools.threads.ThreadPoolFactory;
import tgtools.util.DateUtil;
import tgtools.util.GUID;
import tgtools.util.StringUtil;

import java.util.Date;
import java.util.concurrent.ExecutorService;


/**
 * 定义了一个 Task 抽象类 用于描述处理一件事的过程 run的代码格式
 * try {
 * dosomething();
 * onComplete();
 * }
 * <p>
 * catch(Exception e) {
 * onError(e);
 * }
 *
 * @author tianjing
 */
public abstract class Task {

    protected static final int Time_OneSecond = 1000;
    protected String m_Name;
    protected boolean m_IsCancel;
    protected String m_Status;
    protected int m_IntervalTime = 0;
    protected ITaskListener m_TaskListener;
    protected Date m_PreProcessTime = DateUtil.getCurrentDate();
    protected boolean m_IsBusy = false;
    protected boolean m_IsAsync = false;


    public boolean isBusy() {
        return m_IsBusy;
    }

    public void setisBusy(boolean p_IsBusy) {
        this.m_IsBusy = p_IsBusy;
    }


    public void setTaskListener(ITaskListener p_TaskListener) {
        m_TaskListener = p_TaskListener;
    }

    /**
     * 出错处理
     */
    protected void onError(Exception p_Exception) {
        if (null != m_TaskListener) {
            m_TaskListener.onError(p_Exception);
        }
    }

    /**
     * 完成处理
     */
    protected void onComplete(TaskContext p_Context) {
        if (null != m_TaskListener) {
            m_TaskListener.onCompleted(p_Context);
        }
    }

    /**
     * 取消
     */
    protected void onCancel() {
        if (null != m_TaskListener) {
            m_TaskListener.onCancel();
        }
    }

    /**
     * 表示当前任务是否支持取消。 如果支持，在 run中 需要判断 isCancel() 来中断 当前的任务
     *
     * @return
     */
    protected abstract boolean canCancel();

    /**
     * 表示进度变化的处理
     */
    protected void onProgressChange(int p_Percentage) {
        if (null != m_TaskListener) {
            m_TaskListener.onProgressChanged(p_Percentage);
        }
    }

    /**
     * 表示状态变化需要的处理
     *
     * @param p_Status
     */
    protected void onStatusChange(String p_Status) {
        if (null != m_TaskListener) {
            m_TaskListener.onStatusChanged(p_Status);
        }
    }

    /**
     * 任务名称 不可为null或者"" 子类可重写 自定义一个任务名称
     *
     * @return
     */
    public String getName() {
        if (StringUtil.isNullOrEmpty(m_Name)) {
            m_Name = GUID.newGUID();
        }
        return m_Name;
    }

    public boolean isCancel() {
        return m_IsCancel;
    }

    public void resetCancel() {
        m_IsCancel = false;
    }

    /**
     * 取消当前任务
     */
    public void cancel() {
        if (canCancel()) {
            m_IsCancel = true;
        }
    }

    /**
     * 一个 Task 具体处理的事情
     *
     * @param p_Param
     */
    public abstract void run(TaskContext p_Param);

    /**
     * 使用线程 运行 当前任务
     *
     * @param p_Param
     */
    public void runThread(TaskContext p_Param) {
        m_IsAsync = true;
        m_IsBusy = true;
        final TaskContext p_TaskContext = p_Param;
        ThreadPoolFactory.addTask(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(p_TaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    m_IsAsync = false;
                    m_IsBusy = false;
                }
            }
        });
    }

    /**
     * 线程方式运行 直到结束
     *
     * @param p_Param
     */
    public void runThreadWait(TaskContext p_Param) {
        m_IsAsync = true;
        m_IsBusy = true;
        final TaskContext p_TaskContext = p_Param;
        ThreadPoolFactory.addTask(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(p_TaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    synchronized (Task.this) {
                        Task.this.notifyAll();
                    }
                    m_IsAsync = false;
                    m_IsBusy = false;
                }
            }
        });
    }

    /**
     * 任务 线程 运行
     *
     * @param p_Param          运行参数
     * @param pExecutorService 线程管理器
     */
    public void runThread(TaskContext p_Param, ExecutorService pExecutorService) {
        m_IsAsync = true;
        m_IsBusy = true;
        final TaskContext p_TaskContext = p_Param;
        pExecutorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(p_TaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    m_IsAsync = false;
                    m_IsBusy = false;
                }
            }
        });
    }

    /**
     * 任务 线程 运行
     *
     * @param p_Param          运行参数
     * @param pExecutorService 线程管理器
     */
    public void runThreadWait(TaskContext p_Param, ExecutorService pExecutorService) {
        m_IsAsync = true;
        m_IsBusy = true;
        final TaskContext p_TaskContext = p_Param;
        pExecutorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(p_TaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    synchronized (Task.this) {
                        Task.this.notifyAll();
                    }
                    m_IsAsync = false;
                    m_IsBusy = false;
                }
            }
        });
    }

    /**
     * 报告状态变化
     *
     * @param p_Status
     */
    public void reportStatus(String p_Status) {
        m_Status = p_Status;
        onStatusChange(m_Status);
    }

    public void reportComplete(TaskContext p_Context) {
        reportProgress(100, p_Context);
    }

    /**
     * 报告执行进度 注意 p_Percentage为100时 会调用 onComplete 请勿重复调用
     *
     * @param p_Percentage 进度百分比 0-100
     * @param p_Context    参数
     * @throws TaskException
     */
    public void reportProgress(int p_Percentage, TaskContext p_Context) {
        if (p_Percentage < 0 || p_Percentage > 100) {
            return;
        }
        if (p_Percentage < 100) {
            onProgressChange(p_Percentage);
        } else {
            onComplete(p_Context);
        }
    }

    /**
     * 报告异常
     *
     * @param p_Exception
     */
    public void reportError(Exception p_Exception) {
        m_IsBusy = false;
        onError(p_Exception);
    }

    /**
     * 设置执行周期时间
     *
     * @param p_Second
     */
    public void setIntervalTime(int p_Second) {
        m_IntervalTime = p_Second * Time_OneSecond;
    }

    /**
     * 是否可执行
     *
     * @return
     */
    public boolean canRun() {
        if (m_IntervalTime < 1) {
            return true;
        }
        long currtime = System.currentTimeMillis();
        long pretime = m_PreProcessTime.getTime();
        if ((currtime - pretime) >= m_IntervalTime) {
            return true;
        }

        return false;
    }

    /**
     * 将上次执行时间设置为当前时间
     */
    public void setPreProcessTimeForCurrent() {
        m_PreProcessTime = DateUtil.getCurrentDate();
    }


    /**
     * Task的 监听，用于 外部获取 当前task 的状态 并 进行相应处理
     *
     * @author tianjing
     */
    public interface ITaskListener {
        /**
         * 取消事件
         */
        void onCancel();

        /**
         * 进度变化事件
         *
         * @param p_Percentage
         */
        void onProgressChanged(int p_Percentage);

        /**
         * 错误事件
         *
         * @param p_Exception
         */
        void onError(Exception p_Exception);

        /**
         * 状态变化事件
         *
         * @param p_Status
         */
        void onStatusChanged(String p_Status);

        /**
         * 任务完成事件
         *
         * @param p_Context
         */
        void onCompleted(TaskContext p_Context);
    }


}
