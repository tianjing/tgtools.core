package tgtools.service;

import java.util.Date;
import java.util.Hashtable;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.tasks.TaskContext;
import tgtools.threads.ThreadPoolFactory;
import tgtools.util.DateUtil;
import tgtools.util.LogHelper;
/**
 * @author tianjing
 */
public class ServiceFactory {
 static{
	 m_Services=new Hashtable<String, BaseService>();
 }
	private static Hashtable<String, BaseService> m_Services;
	

    private static boolean isRun;
	public static boolean isRun() {
		return isRun;
	}
	public static void setIsRun(boolean isRun) {
		ServiceFactory.isRun = isRun;
	}
    
    public static void start()
    {
        isRun = true;
    	ThreadPoolFactory.addTask(new Runnable() {

			@Override
			public void run() {
                runServices();
			}
        });
    }
    
    public static void stop()
    {
        isRun = false;
        stopAllServices();
    }

    
    private static void runServices()
    {
        while (isRun)
        {
            try {
                if (!isRun) {
                    return;
                }
                //if (null == m_Services || m_Services.size() < 1)
                //{
                //    return;
                //}

                for (BaseService service : m_Services.values()) {
                    if (!isRun) {
                        return;
                    }
                    if (service.canRun()) {
                        try {
                            service.start();
                        } catch (Throwable ex) {
                            LogHelper.error("", "服务出错：" + service.getName(), "ServiceFactory", ex);
                        }
                        service.setLastTime(DateUtil.getCurrentDate());
                    }
                }
                if (!isRun) {
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogHelper.error("", "服务出错：sleep", "ServiceFactory", e);
                }
            }catch (Throwable ex){
                LogHelper.error("", "服务出错：原因："+ex.getMessage() , "ServiceFactory", ex);
            }
        }
    }
    
    public static boolean hasService(String pName)
    {
    	return m_Services.containsKey(pName);
    }
    public static BaseService getService(String pName)
    {
    	if( m_Services.containsKey(pName))
    	{
    		return m_Services.get(pName);
    	}
    	return null;
    }
    public static void startService(String pName)
    {
        if (m_Services.containsKey(pName))
        {
        	m_Services.get(pName).start();
        }
    }
    public static void stopService(String pName)
    {
        if (m_Services.containsKey(pName))
        {
        	m_Services.get(pName).stop();
        }
    }
    public static boolean isStopService(String pName)
    {
        if (m_Services.containsKey(pName))
        {
            return m_Services.get(pName).isStop();
        }
        return true;
    
    }
    public static void stopAllServices()
    {
        for (BaseService service : m_Services.values())
        {
            if (service.isBusy() || !service.isStop())
            {
                service.stop();
            }
        }
    }
    /// <summary>
    /// 注册一个服务
    /// </summary>
    /// <param name="pService"></param>
    public static void register(BaseService pService) throws APPErrorException
    {
        if (m_Services.containsKey(pService.getName()))
        {
            throw new APPErrorException("注册的服务重复，请检查！");
        }
        m_Services.put(pService.getName(), pService);
        

    }

    /// <summary>
    /// 注销一个服务
    /// </summary>
    /// <param name="pService"></param>
    public static void unRegister(BaseService pService) throws APPErrorException
    {
        unRegister(pService.getName());
    }
    /// <summary>
    /// 注销一个服务
    /// </summary>
    /// <param name="pServiceName"></param>
    public static void unRegister(String pServiceName) throws APPErrorException
    {
        if (m_Services.containsKey(pServiceName))
        {
            if (m_Services.get(pServiceName).isBusy())
            {
                throw new APPErrorException("服务正在运行！无法注销！");
            }
            m_Services.remove(pServiceName);
        }


    }
    public static void main(String[] args) {
        try {
            tgtools.db.DataBaseFactory.add("DBCP", "jdbc:dm://192.168.1.240:5236", "BQ_SYS123", "BQ_SYS");
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 2; i++) {
            try {
                ServiceFactory.register(new TService());
            } catch (APPErrorException e) {
                e.printStackTrace();
            }
        }
        ServiceFactory.start();
        while(true)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("全部结束");
    }

    public static class TService extends BaseService {
        private static int index = 0;
        private int curindex = 0;
        public TService() {
            index = index + 1;
            curindex = index;
        }

        @Override
        protected int getInterval() {
            return 10000;
        }

        @Override
        protected Date getEndTime() {
            return DateUtil.getMaxDate();
        }

        @Override
        public void run(TaskContext pParam) {
            System.out.println("线程:" + curindex + ";开始");
            try {
                DataTable dt= tgtools.db.DataBaseFactory.getDefault().Query("select top 10000 * from loginfo");
                String ss=dt.toJson();
            } catch (APPErrorException e) {
                e.printStackTrace();
            }
            System.out.println("线程:" + curindex + ";结束");
        }
    }
}

