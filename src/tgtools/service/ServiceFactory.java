package tgtools.service;

import java.util.Hashtable;

import tgtools.exceptions.APPErrorException;
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
            if (!m_IsRun) { return; }
            //if (null == m_Services || m_Services.size() < 1)
            //{
            //    return;
            //}

            for (BaseService service : m_Services.values())
            {
                if (!m_IsRun) { return; }
                if (service.canRun())
                {
                    try
                    {
                        service.start();//.Run(null);
                    }
                    catch (Exception ex) {LogHelper.error("","服务出错：" + service.getName(),"ServiceFactory", ex); }
                    service.setLastTime(DateUtil.getCurrentDate());
                }
            }
            if (!m_IsRun) { return; }
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LogHelper.error("","服务出错：sleep" ,"ServiceFactory", e); 
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
	
}
