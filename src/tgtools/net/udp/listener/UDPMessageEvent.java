package tgtools.net.udp.listener;

import tgtools.interfaces.Event;

import java.net.InetAddress;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：16:13
 */
public class UDPMessageEvent extends Event{
    public UDPMessageEvent(Object p_Sender,InetAddress p_Address,int p_Port,byte[] p_Meaage){
        m_Sender=p_Sender;
        m_Meaage=p_Meaage;
        m_Address=p_Address;
        m_Port=p_Port;
    }
    private Object m_Sender;
    private byte[] m_Meaage;
    private InetAddress m_Address;
    private int m_Port;

    /**
     * 获取对象
     * @return
     */
    public Object getSender() {
        return m_Sender;
    }

    /**
     * 获取信息
     * @return
     */
    public byte[] getMeaage() {
        return m_Meaage;
    }

    /**
     * 获取目标地址
     * @return
     */
    public InetAddress getAddress() {
        return m_Address;
    }

    /**
     * 获取目标端口
     * @return
     */
    public int getPort() {
        return m_Port;
    }
}
