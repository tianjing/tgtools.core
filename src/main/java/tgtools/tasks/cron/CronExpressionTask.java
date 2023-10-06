package tgtools.tasks.cron;

import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.DateUtil;
import tgtools.util.LogHelper;

import java.text.ParseException;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author 田径
 * @date 2020-10-09 13:42
 * @desc
 **/
public class CronExpressionTask extends Task {
    protected CronExpression teamCronExpression;
    protected Consumer<TaskContext> cronExpressionAction;
    protected Date lastRunDate;
    //第一次强制运行
    protected boolean useFirstRun = true;

    public CronExpressionTask() {

    }

    public CronExpressionTask(Consumer<TaskContext> pCronExpressionAction) {
        cronExpressionAction = pCronExpressionAction;
    }

    public static void main(String[] args) {
        CronExpressionTask vtask = new CronExpressionTask(content -> {
            System.out.println(DateUtil.formatLongtime(new Date()));
        });
        //vtask.setUseFirstRun(false);
        vtask.run(null);

    }

    public static CronExpressionTask create(Consumer<TaskContext> pCronExpressionAction, String pCronExpression) throws ParseException {
        CronExpressionTask vCronExpressionTask = new CronExpressionTask(pCronExpressionAction);
        vCronExpressionTask.setTeamCronExpression(pCronExpression);
        return vCronExpressionTask;
    }

    public Date getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(Date lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public boolean isUseFirstRun() {
        return useFirstRun;
    }

    public void setUseFirstRun(boolean useFirstRun) {
        this.useFirstRun = useFirstRun;
    }

    public CronExpression getTeamCronExpression() {
        return teamCronExpression;
    }

    public void setTeamCronExpression(CronExpression pTeamCronExpression) {
        teamCronExpression = pTeamCronExpression;
    }

    public void setTeamCronExpression(String pCronExpression) throws ParseException {
        teamCronExpression = CronExpression.create(pCronExpression);
    }

    public Consumer<TaskContext> getCronExpressionAction() {
        return cronExpressionAction;
    }

    public void setCronExpressionAction(Consumer<TaskContext> pCronExpressionAction) {
        cronExpressionAction = pCronExpressionAction;
    }

    /**
     * 默认1分钟1次
     */
    protected void createDefaultTeamCronExpression() {
        try {
            teamCronExpression = CronExpression.create("0 0/1 * * * ?");
        } catch (Exception e) {
            LogHelper.error("CronExpressionTask", "TeamCronExpression create error：" + e.toString(), "createDefaultTeamCronExpression", e);
        }
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    public void run(TaskContext p_Param) {
        if (null == teamCronExpression) {
            createDefaultTeamCronExpression();
        }

        //非第一次执行时，初始化当前时间，下个周期再运行
        if (!useFirstRun && null == lastRunDate) {
            lastRunDate = new Date();
        }
        while (true) {
            if (null != lastRunDate) {
                Date vNextDate = teamCronExpression.getTimeAfter(lastRunDate);

                if (vNextDate.getTime() > System.currentTimeMillis()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
            }

            try {
                cronExpressionAction.accept(p_Param);
                lastRunDate = tgtools.util.DateUtil.getCurrentDate();
            } catch (Exception e) {
                LogHelper.error("CronExpressionTask", "accept error：" + e.toString(), "run", e);
            }
        }
    }
}
