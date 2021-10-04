package tgtools.message;

import tgtools.cache.CacheFactory;
import tgtools.exceptions.APPErrorException;
import tgtools.threads.ThreadPoolFactory;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：15:24
 */
public class MessageFactory {
    private static Vector<IMessageListening> m_Listens = new Vector<IMessageListening>();
    private static Vector<IMessageListening> m_TempAddListens = new Vector<IMessageListening>();
    private static Vector<IMessageListening> m_TempDelListens = new Vector<IMessageListening>();
    private static IMessageStore m_MessageStroe=null;
    private static boolean m_isRun;

    public static void setMessageStroe(IMessageStore pMessageStroe) {
        m_MessageStroe = pMessageStroe;
    }

    /**
     * 是否运行
     * @return
     */
    public static boolean isRun() {
        return m_isRun;
    }

    /**
     * 设置运行状态
     * @param pIsRun
     */
    private static void setIsRun(boolean pIsRun) {
        MessageFactory.m_isRun = pIsRun;
    }
    /**
     * 启动消息处理器
     */
    public static void start(boolean pUseCache) {
        if(!pUseCache)
        {
            m_MessageStroe =new MessageLocalStore();
        }
        else
        {
            m_MessageStroe = new MessageEhcacheStore();
        }
        m_isRun = true;
        ThreadPoolFactory.addTask(new Runnable() {

            @Override
            public void run() {
                processMesage();
            }
        });
    }
    /**
     * 启动消息处理器
     */
    public static void start() {
        start(true);
    }
    public static void stop()
    {
        m_isRun=false;
    }

    public static void processMesage() {
        while (m_isRun) {
            if (!m_isRun) {
                return;
            }
            Message message = null;
            try {
                message = m_MessageStroe.getMessage();
            } catch (Exception ex) {
                LogHelper.error("", "Message取出时出错：" + ex.getMessage(), "ServiceFactory", ex);
            }
            if (null != message) {

                mergaListen();

                Enumeration<IMessageListening> list = m_Listens.elements();
                while (list.hasMoreElements()) {
                    IMessageListening listen = list.nextElement();
                    listen.onMessage(message);
                    if (message.getIsComplete()) {
                        break;
                    }
                }
            }
            if (!m_isRun) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LogHelper.error("", "Message处理出错：sleep", "ServiceFactory", e);
            }
        }
    }

    /**
     * 合并添加或删除的监听
     */
    private static void mergaListen()
    {
        if(!m_TempAddListens.isEmpty())
        {
            m_Listens.addAll(m_TempAddListens);
            m_TempAddListens.clear();
        }
        if(!m_TempDelListens.isEmpty())
        {
            m_Listens.removeAll(m_TempDelListens);
        }
    }

    /**
     * 发送消息
     * @param pMessage
     * @throws APPErrorException
     */
    public static void sendMessage(Message pMessage) throws APPErrorException {
        m_MessageStroe.addMessage(pMessage);
    }

    /**
     * 注册一个监听
     * @param pMessageListening
     * @throws APPErrorException
     */
    public static void registerListening(IMessageListening pMessageListening) throws APPErrorException {
        m_TempAddListens.add(pMessageListening);
    }

    /**
     * 注销一个监听
     *
     * @param pMessageListening
     * @throws APPErrorException
     */
    public static void unRegisterListening(IMessageListening pMessageListening) throws APPErrorException {
        m_TempDelListens.remove(pMessageListening);
    }

    /**
     * 注销监听，根据监听名称注销所有监听
     *
     * @param pListenName
     * @throws APPErrorException
     */
    public static void unRegisterListening(String pListenName) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pListenName)) {
            return;
        }

        List<IMessageListening> result = new ArrayList<IMessageListening>();
        for (int i = 0; i < m_Listens.size(); i++) {
            if (pListenName.equals(m_Listens.get(i).getName())) {
                result.add(m_Listens.get(i));
            }
        }

        for (int i = 0; i < result.size(); i++) {
            unRegisterListening(result.get(i));
        }
    }
}
