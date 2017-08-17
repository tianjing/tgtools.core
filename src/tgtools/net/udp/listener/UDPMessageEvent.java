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

    public Object getSender() {
        return m_Sender;
    }

    public byte[] getMeaage() {
        return m_Meaage;
    }
}
