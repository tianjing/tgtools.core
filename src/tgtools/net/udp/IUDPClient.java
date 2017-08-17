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
    void setTimeOut(int p_TimeOut);
    void setBuffeSize(int p_BuffeSize);
    void setListener(IUDPClientListener p_Listener);
    void setTargetAddress(InetAddress p_InetAddress);
    void setTargetPort(int p_TargetPort);
    void send(InetAddress p_Target, int p_TargetPort, byte[] p_Data) throws APPErrorException;
    void send(byte[] p_Data) throws APPErrorException;
}
