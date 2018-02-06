package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.net.udp.listener.IUDPClientListener;
import tgtools.net.udp.listener.UDPErrorEvent;
import tgtools.net.udp.listener.UDPMessageEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：udp 简单客户端，用于发送消息
 * 时  间：13:42
 */
public class UDPClient implements IUDPClient {
    public UDPClient(){}
    public UDPClient(int pPort)
    {
        m_SelfPort =pPort;
    }
    protected int m_SelfPort=0;
    protected int m_TargetPort;
    protected InetAddress m_TargetAddress;
    protected DatagramSocket m_Socket = null;
    protected IUDPClientListener m_Listener;
    protected int m_BufferSize = 500;
    protected int m_TimeOut = 20000;
    protected int m_BufferPacketSize = 8192;

    private static void main(String[] args) throws UnsupportedEncodingException {
        String data = "扫积分卡的拉萨积分考拉打扫接口拉菲克";
        String fdsaf = "fdsafadskljfkdsajfkldsa1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111klfjkladsjfkljdslajfkladsjfkldasklfjkladsjfadsklfjadskljfkldsjaklfjadskl";
        fdsaf += fdsaf;
        fdsaf += fdsaf;
        fdsaf += fdsaf;
        data += "fkdjasklfjdlasjfjadsl;jfkl;adsjkl;fewijfkldjafkljadskl;fjkldjskl;afjkladsjfkl;jdsaklfjkl;adsjfkladskl;fjkl;adsjfkladsjklfjkladsjfkladsjfkldjasklewiofjdklajklfajldjklf";
        data += data;
        data += data;
        data += data;
        data += data;
        data += data;
        data += fdsaf;
        data += data;
        data += data;
        byte[] da = data.getBytes("GBK");

        UDPClient client = new UDPClient();
        try {
            client.setTargetAddress(InetAddress.getByName("192.168.88.128"));
            client.setTargetPort(6000);
            client.multiSend(da);
            System.out.println("111");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected DatagramSocket getSocket() throws APPErrorException {
        if (null == m_Socket) {
            try {
                if(m_SelfPort>0)
                {
                    m_Socket = new DatagramSocket(m_SelfPort);
                }
                else {
                    m_Socket = new DatagramSocket();
                }
                m_Socket.setSendBufferSize(m_BufferSize);
                m_Socket.setSoTimeout(m_TimeOut);
                return m_Socket;
            } catch (Exception ex) {
                throw new APPErrorException("创建Udp对象出错，原因：" + ex.getMessage());
            }
        }
        return m_Socket;
    }

    protected void close() {
        onClose();
        if (null != m_Socket) {
            m_Socket.close();
            m_Socket = null;
        }
    }

    /**
     * 设置超时
     *
     * @param p_TimeOut
     */
    @Override
    public void setTimeOut(int p_TimeOut) {
        m_TimeOut = p_TimeOut;
    }

    /**
     * 设置缓冲大小
     *
     * @param p_BuffeSize
     */
    @Override
    public void setBuffeSize(int p_BuffeSize) {
        m_BufferSize = p_BuffeSize;
    }

    /**
     * 设置监听
     *
     * @param p_Listener
     */
    @Override
    public void setListener(IUDPClientListener p_Listener) {
        m_Listener = p_Listener;
    }

    /**
     * 设置目标地址
     *
     * @param p_InetAddress
     */
    @Override
    public void setTargetAddress(InetAddress p_InetAddress) {
        m_TargetAddress = p_InetAddress;
    }

    /**
     * 设置目标端口
     *
     * @param p_TargetPort
     */
    @Override
    public void setTargetPort(int p_TargetPort) {
        m_TargetPort = p_TargetPort;
    }

    /**
     * 发送消息
     *
     * @param p_Target     目标地址
     * @param p_TargetPort 目标端口
     * @param p_Data       消息内容
     * @throws APPErrorException
     */
    @Override
    public void send(InetAddress p_Target, int p_TargetPort, byte[] p_Data) throws APPErrorException {
        try {
            DatagramPacket dataGramPacket = new DatagramPacket(p_Data, 0, p_Data.length, p_Target, p_TargetPort);
            getSocket().send(dataGramPacket);

        } catch (IOException e) {
            onError(e);
        }
    }

    /**
     * 关闭时
     */
    protected void onClose() {
        if (null != m_Listener) {
            m_Listener.onClose();
        }
    }

    /**
     * 发送信息时
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
     * 出错时
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
     * 发送信息
     *
     * @param p_Data
     * @throws APPErrorException
     */
    @Override
    public void send(byte[] p_Data) throws APPErrorException {
        send(m_TargetAddress, m_TargetPort, p_Data);
    }

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     *
     * @param p_Data 数据
     * @throws APPErrorException
     */
    @Override
    public void multiSend(byte[] p_Data) throws APPErrorException {
        multiSend(m_TargetAddress, m_TargetPort, p_Data, -1);
    }

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     *
     * @param p_Target     目标IP
     * @param p_TargetPort 目标端口
     * @param p_Data       数据
     * @throws APPErrorException
     */
    @Override
    public void multiSend(InetAddress p_Target, int p_TargetPort, byte[] p_Data) throws APPErrorException {
        multiSend(p_Target, p_TargetPort, p_Data, -1);
    }

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     *
     * @param p_Target     目标IP
     * @param p_TargetPort 目标端口
     * @param p_Data       数据
     * @param p_Length     单包长度
     * @throws APPErrorException
     */
    @Override
    public void multiSend(InetAddress p_Target, int p_TargetPort, byte[] p_Data, int p_Length) throws APPErrorException {
        int packetsize = p_Length;
        if (packetsize < 1) {
            packetsize = m_BufferPacketSize;
        }

        try {
            onMessage(p_Data, p_Target, p_TargetPort);
            int count = (p_Data.length / packetsize) + 1;
            int off = 0;
            for (int i = 0; i < count; i++) {
                if (off >= p_Data.length) {break;}
                int length = p_Data.length - off > packetsize ? packetsize : p_Data.length - off;
                DatagramPacket dataGramPacket = new DatagramPacket(p_Data, off, length, p_Target, p_TargetPort);
                getSocket().send(dataGramPacket);
                off = off + length;
            }
        } catch (IOException e) {
            onError(e);
        }
    }

    /**
     * 释放
     */
    @Override
    public void Dispose() {
        try {
            m_TargetAddress = null;
            m_Listener = null;
            close();
        } catch (Throwable ex) {
        }
    }


}
