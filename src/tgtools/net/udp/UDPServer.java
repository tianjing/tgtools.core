package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.net.udp.listener.IUDPServerListener;
import tgtools.net.udp.listener.UDPErrorEvent;
import tgtools.net.udp.listener.UDPMessageEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：udp 简单服务端，用于接收消息
 * 时  间：14:44
 */
public class UDPServer implements IUDPServer {

    protected DatagramSocket m_Socket = null;
    protected int m_Port;
    protected IUDPServerListener m_Listener;
    protected int m_BufferSize = 2048;
    protected int m_TimeOut = 20000;


    protected DatagramSocket getSocket() throws APPErrorException {
        if (null == m_Socket && m_Port > 0) {
            try {
                m_Socket = new DatagramSocket(m_Port);
                m_Socket.setReceiveBufferSize(m_BufferSize);
                m_Socket.setSoTimeout(m_TimeOut);
                return m_Socket;
            } catch (Exception ex) {
                throw new APPErrorException("创建Udp对象出错，原因：" + ex.getMessage());
            }
        }
        return m_Socket;
    }

    /**
     * 设置超时
     * @param p_TimeOut
     */
    @Override
    public void setTimeOut(int p_TimeOut) {
        m_BufferSize = p_TimeOut;
    }

    /**
     * 设置缓冲
     * @param p_BuffeSize
     */
    @Override
    public void setBuffeSize(int p_BuffeSize) {
        m_BufferSize=p_BuffeSize;
    }

    /**
     * 设置监听
     * @param p_Listener
     */
    @Override
    public void setListener(IUDPServerListener p_Listener) {
        m_Listener = p_Listener;
    }

    /**
     * 启动监听
     * @param p_Port
     * @throws APPErrorException
     */
    @Override
    public void start(int p_Port) throws APPErrorException {
        m_Port = p_Port;
        onStart();

        while (true) {
            byte[] backbuf = new byte[m_BufferSize];
            DatagramPacket backPacket = new DatagramPacket(backbuf, backbuf.length);
            try {
                getSocket().receive(backPacket);
                ByteArrayOutputStream dd = new ByteArrayOutputStream();
                dd.write(backPacket.getData(), 0, backPacket.getLength());
                onMessage(dd.toByteArray(), backPacket.getAddress(), backPacket.getPort());
            } catch (IOException e) {
                onError(e);
            }
        }
    }

    /**
     * 关闭
     */
    protected void close() {
        onClose();
        if (null != m_Socket) {
            m_Socket.close();
            m_Socket = null;
        }
    }

    /**
     * 释放
     */
    @Override
    public void Dispose() {
        close();
        m_Listener = null;
    }

    /**
     *
     */
    protected void onClose() {
        if (null != m_Listener) {
            m_Listener.onClose();
        }
    }

    /**
     *
     * @param p_Data
     * @param p_Address
     * @param p_Port
     */
    protected void onMessage(byte[] p_Data, InetAddress p_Address, int p_Port) {
        if (null != m_Listener) {
            UDPMessageEvent event = new UDPMessageEvent(this, p_Address, p_Port, p_Data);
            m_Listener.onMessage(event);
        }
    }

    /**
     *
     * @param p_Error
     */
    protected void onError(Throwable p_Error) {
        if (null != m_Listener) {
            UDPErrorEvent event = new UDPErrorEvent(this, p_Error);
            m_Listener.onError(event);
        }
    }

    /**
     *
     */
    protected void onStart() {
        if (null != m_Listener) {
            m_Listener.onStart();
        }
    }


    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        try {
            server.setListener(new IUDPServerListener() {
                @Override
                public void onError(UDPErrorEvent p_Event) {

                }

                @Override
                public void onMessage(UDPMessageEvent p_Event) {
                    String receiveMessage = new String(p_Event.getMeaage());
                    System.out.println(receiveMessage);//暂时打印到控制台，一般输出到文件
                    System.out.println("aaaaaaaaaaaaaaaaaa---------------------");
                }

                @Override
                public void onClose() {

                }

                @Override
                public void onStart() {

                }
            });
            server.start(60000);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }
}
