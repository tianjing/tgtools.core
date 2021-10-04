package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;
import tgtools.net.udp.listener.IUDPClientListener;

import java.net.InetAddress;

/**
 * @author tianjing
 */
public interface IUDPClient extends IDispose{
    /**
     * 设置超时时间
     * @param pTimeOut
     */
    void setTimeOut(int pTimeOut);

    /**
     * 设置缓冲
     * @param pBuffeSize
     */
    void setBuffeSize(int pBuffeSize);

    /**
     * 设置监听
     * @param pListener
     */
    void setListener(IUDPClientListener pListener);

    /**
     * 设置目标地址
     * @param pInetAddress
     */
    void setTargetAddress(InetAddress pInetAddress);

    /**
     * 设置目标端口
     * @param pTargetPort
     */
    void setTargetPort(int pTargetPort);

    /**
     * 发送消息
     * @param pTarget 目标地址
     * @param pTargetPort 目标端口
     * @param pData 消息内容
     * @throws APPErrorException
     */
    void send(InetAddress pTarget, int pTargetPort, byte[] pData) throws APPErrorException;

    /**
     * 发送消息 根据设置的目标地址和目标端口
     * @param pData
     * @throws APPErrorException
     */
    void send(byte[] pData) throws APPErrorException;

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     * @param pData 数据
     * @throws APPErrorException
     */
    void multiSend(byte[] pData) throws APPErrorException;

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     * @param pTarget 目标IP
     * @param pTargetPort 目标端口
     * @param pData 数据
     * @throws APPErrorException
     */
    void multiSend(InetAddress pTarget, int pTargetPort, byte[] pData) throws APPErrorException;

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     * @param pTarget 目标IP
     * @param pTargetPort 目标端口
     * @param pData 数据
     * @param pLength 单包长度
     * @throws APPErrorException
     */
    void multiSend(InetAddress pTarget, int pTargetPort, byte[] pData,int pLength) throws APPErrorException;
}
