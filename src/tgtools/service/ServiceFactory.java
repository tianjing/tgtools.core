package tgtools.service;

import java.util.Date;
import java.util.Hashtable;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.tasks.TaskContext;
import tgtools.threads.ThreadPoolFactory;
import tgtools.util.DateUtil;
import tgtools.util.LogHelper;

public class ServiceFactory {
 static{
	 m_Services=new Hashtable<String, BaseService>();
 }
	private static Hashtable<String, BaseService> m_Services;
	

    private static boolean m_IsRun;
	public static boolean isRun() {
		return m_IsRun;
	}
	public static void setIsRun(boolean m_IsRun) {
		ServiceFactory.m_IsRun = m_IsRun;
	}
    
    public static void start()
    {
        m_IsRun = true;
    	ThreadPoolFactory.addTask(new Runnable() {

			@Override
			public void run() {
                RunServices();
			}
        });
    }
    
    public static void stop()
    {
        m_IsRun = false;
        stopAllServices();
    }

    
    private static void RunServices()
    {
        while (m_IsRun)
        {
            try {
                if (!m_IsRun) {
                    return;
                }
                //if (null == m_Services || m_Services.size() < 1)
                //{
                //    return;
                //}

                for (BaseService service : m_Services.values()) {
                    if (!m_IsRun) {
                        return;
                    }
                    if (service.canRun()) {
                        try {
                            service.start();//.Run(null);
                        } catch (Throwable ex) {
                            LogHelper.error("", "服务出错：" + service.getName(), "ServiceFactory", ex);
                        }
                        service.setLastTime(DateUtil.getCurrentDate());
                    }
                }
                if (!m_IsRun) {
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
    
    public static boolean hasService(String p_Name)
    {
    	return m_Services.containsKey(p_Name);
    }
    public static BaseService getService(String p_Name)
    {
    	if( m_Services.containsKey(p_Name))
    	{
    		return m_Services.get(p_Name);
    	}
    	return null;
    }
    public static void startService(String p_Name)
    {
        if (m_Services.containsKey(p_Name))
        {
        	m_Services.get(p_Name).start();
        }
    }
    public static void stopService(String p_Name)
    {
        if (m_Services.containsKey(p_Name))
        {
        	m_Services.get(p_Name).stop();
        }
    }
    public static boolean isStopService(String p_Name)
    {
        if (m_Services.containsKey(p_Name))
        {
            return m_Services.get(p_Name).isStop();
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
    /// <param name="p_Service"></param>
    public static void register(BaseService p_Service) throws APPErrorException
    {
        if (m_Services.containsKey(p_Service.getName()))
        {
            throw new APPErrorException("注册的服务重复，请检查！");
        }
        m_Services.put(p_Service.getName(), p_Service);
        

    }

    /// <summary>
    /// 注销一个服务
    /// </summary>
    /// <param name="p_Service"></param>
    public static void unRegister(BaseService p_Service) throws APPErrorException
    {
        unRegister(p_Service.getName());
    }
    /// <summary>
    /// 注销一个服务
    /// </summary>
    /// <param name="p_ServiceName"></param>
    public static void unRegister(String p_ServiceName) throws APPErrorException
    {
        if (m_Services.containsKey(p_ServiceName))
        {
            if (m_Services.get(p_ServiceName).isBusy())
            {
                throw new APPErrorException("服务正在运行！无法注销！");
            }
            m_Services.remove(p_ServiceName);
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
        public void run(TaskContext p_Param) {
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

