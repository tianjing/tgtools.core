package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.net.udp.listener.IUDPClientListener;
import tgtools.net.udp.listener.UDPErrorEvent;
import tgtools.net.udp.listener.UDPMessageEvent;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：udp 简单客户端，用于发送消息
 * 时  间：13:42
 */
public class UDPClient implements IUDPClient {
    protected int m_TargetPort;
    protected InetAddress m_TargetAddress;
    protected DatagramSocket m_Socket=null;
    protected IUDPClientListener m_Listener;
    protected int m_BufferSize=2048;
    protected int m_TimeOut=20000;

    protected DatagramSocket getSocket() throws APPErrorException {
        if(null==m_Socket)
        {
            try {
                m_Socket = new DatagramSocket();
                m_Socket.setSendBufferSize(m_BufferSize);
                m_Socket.setSoTimeout(m_TimeOut);
                return m_Socket;
            }catch (Exception ex)
            {
                throw new APPErrorException("创建Udp对象出错，原因："+ex.getMessage());
            }
        }
        return m_Socket;
    }
    protected void close()
    {
        onClose();
        if(null!=m_Socket)
        {
            m_Socket.close();
            m_Socket=null;
        }
    }


    @Override
    public void setTimeOut(int p_TimeOut) {
        m_TimeOut=p_TimeOut;
    }

    @Override
    public void setBuffeSize(int p_BuffeSize) {
        m_BufferSize=p_BuffeSize;
    }

    @Override
    public void setListener(IUDPClientListener p_Listener) {
        m_Listener=p_Listener;
    }
    @Override
    public void setTargetAddress(InetAddress p_InetAddress) {
        m_TargetAddress=p_InetAddress;
    }

    @Override
    public void setTargetPort(int p_TargetPort) {
        m_TargetPort=p_TargetPort;
    }

    @Override
    public void send(InetAddress p_Target, int p_TargetPort, byte[] p_Data) throws APPErrorException {
         try {
                onMessage(p_Data,p_Target,p_TargetPort);
                int count=(p_Data.length/m_BufferSize)+1;
                int off=0;
                for(int i=0;i<count;i++) {
                    if(off>=p_Data.length)break;
                    int length=p_Data.length-off>m_BufferSize?m_BufferSize:p_Data.length-off;
                    DatagramPacket dataGramPacket = new DatagramPacket(p_Data,off, length, p_Target, p_TargetPort);
                    getSocket().send(dataGramPacket);
                    off=off+length;
                }
        }  catch (IOException e) {
             onError(e);
        }
    }
    protected void onClose()
    {
        if(null!=m_Listener)
        {
            m_Listener.onClose();
        }
    }
    protected void onMessage(byte[] p_Data, InetAddress p_Address,int p_Port)
    {
        if(null!=m_Listener)
        {
            UDPMessageEvent event=new UDPMessageEvent(this,p_Address,p_Port,p_Data);
            m_Listener.onMessage(event);
        }
    }
    protected void onError(Throwable p_Error)
    {
        if(null!=m_Listener)
        {
            UDPErrorEvent event =new UDPErrorEvent(this,p_Error);
            m_Listener.onError(event);
        }
    }
    @Override
    public void send(byte[] p_Data) throws APPErrorException {
        send(m_TargetAddress,m_TargetPort,p_Data);
    }

    @Override
    public void Dispose() {
        try{
            m_TargetAddress=null;
            m_Listener=null;
            close();
        }
        catch (Throwable ex)
        {}
    }

    public static void main(String [] args) throws UnsupportedEncodingException {
        String data="扫积分卡的拉萨积分考拉打扫接口拉菲克";

        data+="1234567";

        byte[] da=data.getBytes("GBK");

        UDPClient client =new UDPClient();
        try {
            for (int i = 0; i < 10000000; i++) {
                client.setTargetAddress(InetAddress.getByName("192.168.88.128"));
                client.setTargetPort(60000);
                client.send(da);


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch(UnknownHostException e){
                e.printStackTrace();
            } catch(APPErrorException e){
                e.printStackTrace();
            }



    }
}
