package tgtools.net.udp.listener;

import tgtools.interfaces.Event;

import java.net.InetAddress;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：16:13
 */
public class UDPMessageEvent extends Event{
    public UDPMessageEvent(Object pSender,InetAddress pAddress,int pPort,byte[] pMeaage){
        sender=pSender;
        meaage=pMeaage;
        address=pAddress;
        port=pPort;
    }
    private Object sender;
    private byte[] meaage;
    private InetAddress address;
    private int port;

    /**
     * 获取对象
     * @return
     */
    public Object getSender() {
        return sender;
    }

    /**
     * 获取信息
     * @return
     */
    public byte[] getMeaage() {
        return meaage;
    }

    /**
     * 获取目标地址
     * @return
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * 获取目标端口
     * @return
     */
    public int getPort() {
        return port;
    }
}
