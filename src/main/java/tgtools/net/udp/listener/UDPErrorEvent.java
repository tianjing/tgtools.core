package tgtools.net.udp.listener;

import tgtools.interfaces.Event;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：16:13
 */
public class UDPErrorEvent extends Event {
    private Throwable m_Error;
    private Object m_Sender;
    public UDPErrorEvent(Object p_Sender, Throwable p_Error) {
        m_Sender = p_Sender;
        m_Error = p_Error;
    }

    /**
     * 获取异常信息
     * @return
     */
    public Throwable getError() {
        return m_Error;
    }

    /**
     * 获取对象
     * @return
     */
    public Object getSender() {
        return m_Sender;
    }
}
