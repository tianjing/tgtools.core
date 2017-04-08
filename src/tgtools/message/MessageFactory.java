package tgtools.message;

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
 * 编写者：田径
 * 功  能：
 * 时  间：15:24
 */
public class MessageFactory {
    private static Vector<IMessageListening> m_Listens = new Vector<IMessageListening>();
    private static Vector<IMessageListening> m_TempAddListens = new Vector<IMessageListening>();
    private static Vector<IMessageListening> m_TempDelListens = new Vector<IMessageListening>();
    private static IMessageStore m_MessageStroe=null;
    private static boolean m_IsRun;

    static{
        m_MessageStroe = new MessageLocalStore();
    }

    /**
     * 是否运行
     * @return
     */
    public static boolean isRun() {
        return m_IsRun;
    }

    /**
     * 设置运行状态
     * @param m_IsRun
     */
    private static void setIsRun(boolean m_IsRun) {
        MessageFactory.m_IsRun = m_IsRun;
    }

    /**
     * 启动消息处理器
     */
    public static void start() {
        m_IsRun = true;
        ThreadPoolFactory.addTask(new Runnable() {

            @Override
            public void run() {
                processMesage();
            }
        });
    }
    public static void stop()
    {
        m_IsRun=false;
    }

    public static void processMesage() {
        while (m_IsRun) {
            if (!m_IsRun) {
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
            if (!m_IsRun) {
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
     * @param p_Message
     * @throws APPErrorException
     */
    public static void sendMessage(Message p_Message) throws APPErrorException {
        m_MessageStroe.addMessage(p_Message);
    }

    /**
     * 注册一个监听
     * @param p_MessageListening
     * @throws APPErrorException
     */
    public static void registerListening(IMessageListening p_MessageListening) throws APPErrorException {
        m_TempAddListens.add(p_MessageListening);
    }

    /**
     * 注销一个监听
     *
     * @param p_MessageListening
     * @throws APPErrorException
     */
    public static void unRegisterListening(IMessageListening p_MessageListening) throws APPErrorException {
        m_TempDelListens.remove(p_MessageListening);
    }

    /**
     * 注销监听，根据监听名称注销所有监听
     *
     * @param p_ListenName
     * @throws APPErrorException
     */
    public static void unRegisterListening(String p_ListenName) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(p_ListenName)) {
            return;
        }

        List<IMessageListening> result = new ArrayList<IMessageListening>();
        for (int i = 0; i < m_Listens.size(); i++) {
            if (p_ListenName.equals(m_Listens.get(i).getName())) {
                result.add(m_Listens.get(i));
            }
        }

        for (int i = 0; i < result.size(); i++) {
            unRegisterListening(result.get(i));
        }
    }
}
