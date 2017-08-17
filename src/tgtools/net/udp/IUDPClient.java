package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;
import tgtools.net.udp.listener.IUDPClientListener;

import java.net.InetAddress;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：13:35
 */
public interface IUDPClient extends IDispose{
    /**
     * 设置超时时间
     * @param p_TimeOut
     */
    void setTimeOut(int p_TimeOut);

    /**
     * 设置缓冲
     * @param p_BuffeSize
     */
    void setBuffeSize(int p_BuffeSize);

    /**
     * 设置监听
     * @param p_Listener
     */
    void setListener(IUDPClientListener p_Listener);

    /**
     * 设置目标地址
     * @param p_InetAddress
     */
    void setTargetAddress(InetAddress p_InetAddress);

    /**
     * 设置目标端口
     * @param p_TargetPort
     */
    void setTargetPort(int p_TargetPort);

    /**
     * 发送消息
     * @param p_Target 目标地址
     * @param p_TargetPort 目标端口
     * @param p_Data 消息内容
     * @throws APPErrorException
     */
    void send(InetAddress p_Target, int p_TargetPort, byte[] p_Data) throws APPErrorException;

    /**
     * 发送消息 根据设置的目标地址和目标端口
     * @param p_Data
     * @throws APPErrorException
     */
    void send(byte[] p_Data) throws APPErrorException;
}
