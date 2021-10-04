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
    protected String name;
    protected boolean isCancel;
    protected String status;
    protected int intervalTime = 0;
    protected ITaskListener taskListener;
    protected Date preProcessTime = DateUtil.getCurrentDate();
    protected boolean isBusy = false;
    protected boolean isAsync = false;


    public boolean isBusy() {
        return isBusy;
    }

    public void setsBusy(boolean pIsBusy) {
        this.isBusy = pIsBusy;
    }


    public void setTaskListener(ITaskListener pTaskListener) {
        taskListener = pTaskListener;
    }

    /**
     * 出错处理
     */
    protected void onError(Exception pException) {
        if (null != taskListener) {
            taskListener.onError(pException);
        }
    }

    /**
     * 完成处理
     */
    protected void onComplete(TaskContext pContext) {
        if (null != taskListener) {
            taskListener.onCompleted(pContext);
        }
    }

    /**
     * 取消
     */
    protected void onCancel() {
        if (null != taskListener) {
            taskListener.onCancel();
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
    protected void onProgressChange(int pPercentage) {
        if (null != taskListener) {
            taskListener.onProgressChanged(pPercentage);
        }
    }

    /**
     * 表示状态变化需要的处理
     *
     * @param pStatus
     */
    protected void onStatusChange(String pStatus) {
        if (null != taskListener) {
            taskListener.onStatusChanged(pStatus);
        }
    }

    /**
     * 任务名称 不可为null或者"" 子类可重写 自定义一个任务名称
     *
     * @return
     */
    public String getName() {
        if (StringUtil.isNullOrEmpty(name)) {
            name = GUID.newGUID();
        }
        return name;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void resetCancel() {
        isCancel = false;
    }

    /**
     * 取消当前任务
     */
    public void cancel() {
        if (canCancel()) {
            isCancel = true;
        }
    }

    /**
     * 一个 Task 具体处理的事情
     *
     * @param pParam
     */
    public abstract void run(TaskContext pParam);

    /**
     * 使用线程 运行 当前任务
     *
     * @param pParam
     */
    public void runThread(TaskContext pParam) {
        isAsync = true;
        isBusy = true;
        final TaskContext pTaskContext = pParam;
        ThreadPoolFactory.addTask(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(pTaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    isAsync = false;
                    isBusy = false;
                }
            }
        });
    }

    /**
     * 线程方式运行 直到结束
     *
     * @param pParam
     */
    public void runThreadWait(TaskContext pParam) {
        isAsync = true;
        isBusy = true;
        final TaskContext pTaskContext = pParam;
        ThreadPoolFactory.addTask(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(pTaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    synchronized (Task.this) {
                        Task.this.notifyAll();
                    }
                    isAsync = false;
                    isBusy = false;
                }
            }
        });
    }

    /**
     * 任务 线程 运行
     *
     * @param pParam          运行参数
     * @param pExecutorService 线程管理器
     */
    public void runThread(TaskContext pParam, ExecutorService pExecutorService) {
        isAsync = true;
        isBusy = true;
        final TaskContext pTaskContext = pParam;
        pExecutorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(pTaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    isAsync = false;
                    isBusy = false;
                }
            }
        });
    }

    /**
     * 任务 线程 运行
     *
     * @param pParam          运行参数
     * @param pExecutorService 线程管理器
     */
    public void runThreadWait(TaskContext pParam, ExecutorService pExecutorService) {
        isAsync = true;
        isBusy = true;
        final TaskContext pTaskContext = pParam;
        pExecutorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    Task.this.run(pTaskContext);
                } catch (Exception e) {
                    Task.this.onError(e);
                } finally {
                    synchronized (Task.this) {
                        Task.this.notifyAll();
                    }
                    isAsync = false;
                    isBusy = false;
                }
            }
        });
    }

    /**
     * 报告状态变化
     *
     * @param pStatus
     */
    public void reportStatus(String pStatus) {
        status = pStatus;
        onStatusChange(status);
    }

    public void reportComplete(TaskContext pContext) {
        reportProgress(100, pContext);
    }

    /**
     * 报告执行进度 注意 p_Percentage为100时 会调用 onComplete 请勿重复调用
     *
     * @param pPercentage 进度百分比 0-100
     * @param pContext    参数
     * @throws TaskException
     */
    public void reportProgress(int pPercentage, TaskContext pContext) {
        if (pPercentage < 0 || pPercentage > 100) {
            return;
        }
        if (pPercentage < 100) {
            onProgressChange(pPercentage);
        } else {
            onComplete(pContext);
        }
    }

    /**
     * 报告异常
     *
     * @param pException
     */
    public void reportError(Exception pException) {
        isBusy = false;
        onError(pException);
    }

    /**
     * 设置执行周期时间
     *
     * @param pSecond
     */
    public void setIntervalTime(int pSecond) {
        intervalTime = pSecond * Time_OneSecond;
    }

    /**
     * 是否可执行
     *
     * @return
     */
    public boolean canRun() {
        if (intervalTime < 1) {
            return true;
        }
        long currtime = System.currentTimeMillis();
        long pretime = preProcessTime.getTime();
        if ((currtime - pretime) >= intervalTime) {
            return true;
        }

        return false;
    }

    /**
     * 将上次执行时间设置为当前时间
     */
    public void setPreProcessTimeForCurrent() {
        preProcessTime = DateUtil.getCurrentDate();
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
         * @param pPercentage
         */
        void onProgressChanged(int pPercentage);

        /**
         * 错误事件
         *
         * @param pException
         */
        void onError(Exception pException);

        /**
         * 状态变化事件
         *
         * @param pStatus
         */
        void onStatusChanged(String pStatus);

        /**
         * 任务完成事件
         *
         * @param pContext
         */
        void onCompleted(TaskContext pContext);
    }


}
