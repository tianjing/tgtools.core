package tgtools.service;

import tgtools.tasks.Task;
import tgtools.util.DateUtil;

import java.util.Date;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 2017-10-18 10:22
 */
public abstract class BaseService extends Task {

    public Date lastTime;
    public boolean isStop;

    public BaseService() {
        isStop = false;
    }

    /**
     * Milliseconds
     *
     * @return
     */
    protected abstract int getInterval();

    /**
     *   getEndTime
     * @return
     */
    protected abstract Date getEndTime();

    /**
     * isConcurrency
     * @return
     */
    public boolean isConcurrency() {
        return false;
    }

    /**
     * getLastTime
     * @return
     */
    public Date getLastTime() {
        return lastTime;
    }

    /**
     *  setLastTime
     * @param pLastTime
     */
    public void setLastTime(Date pLastTime) {
        this.lastTime = pLastTime;
    }

    /**
     * isStop
     * @return
     */
    public boolean isStop() {
        return isStop;
    }

    /**
     * setIsStop
     * @param pIsStop
     */
    public void setIsStop(boolean pIsStop) {
        this.isStop = pIsStop;
    }

    /**
     *  start
     */
    public void start() {
        if (!isStop) {
            runThread(null);

        }
    }

    public void stop() {
        isStop = true;
    }

    /**
     * 是否可以运行
     *
     * @return
     */
    @Override
    public boolean canRun() {
        if (isStop) {
            return false;
        }
        if (isBusy()) {
            return false;
        }

        //第一次运行不用判断时间周期
        if (lastTime == null) {
            return true;
        }

        if (DateUtil.getCurrentDate().getTime() == getEndTime().getTime()) {
            return false;
        }
        if (DateUtil.getCurrentDate().getTime() >= DateUtil.addSeconds(lastTime, getInterval() / 1000).getTime()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    protected void onError(Exception pException) {
        this.isStop = true;
        super.onError(pException);
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setIsAsync(boolean pIsAsync) {
        this.isAsync = pIsAsync;
    }
}


