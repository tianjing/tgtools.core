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
 * @author tianjing
 * 功  能：udp 简单客户端，用于发送消息
 * 时  间：13:42
 */
public class UDPClient implements IUDPClient {
    public UDPClient(){}
    public UDPClient(int pPort)
    {
        selfPort =pPort;
    }
    protected int selfPort=0;
    protected int targetPort;
    protected InetAddress targetAddress;
    protected DatagramSocket socket = null;
    protected IUDPClientListener listener;
    protected int bufferSize = 500;
    protected int timeOut = 20000;
    protected int bufferPacketSize = 8192;

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
        if (null == socket) {
            try {
                if(selfPort>0)
                {
                    socket = new DatagramSocket(selfPort);
                }
                else {
                    socket = new DatagramSocket();
                }
                socket.setSendBufferSize(bufferSize);
                socket.setSoTimeout(timeOut);
                return socket;
            } catch (Exception ex) {
                throw new APPErrorException("创建Udp对象出错，原因：" + ex.getMessage());
            }
        }
        return socket;
    }

    protected void close() {
        onClose();
        if (null != socket) {
            socket.close();
            socket = null;
        }
    }

    /**
     * 设置超时
     *
     * @param pTimeOut
     */
    @Override
    public void setTimeOut(int pTimeOut) {
        timeOut = pTimeOut;
    }

    /**
     * 设置缓冲大小
     *
     * @param pBuffeSize
     */
    @Override
    public void setBuffeSize(int pBuffeSize) {
        bufferSize = pBuffeSize;
    }

    /**
     * 设置监听
     *
     * @param pListener
     */
    @Override
    public void setListener(IUDPClientListener pListener) {
        listener = pListener;
    }

    /**
     * 设置目标地址
     *
     * @param pInetAddress
     */
    @Override
    public void setTargetAddress(InetAddress pInetAddress) {
        targetAddress = pInetAddress;
    }

    /**
     * 设置目标端口
     *
     * @param pTargetPort
     */
    @Override
    public void setTargetPort(int pTargetPort) {
        targetPort = pTargetPort;
    }

    /**
     * 发送消息
     *
     * @param pTarget     目标地址
     * @param pTargetPort 目标端口
     * @param pData       消息内容
     * @throws APPErrorException
     */
    @Override
    public void send(InetAddress pTarget, int pTargetPort, byte[] pData) throws APPErrorException {
        try {
            DatagramPacket dataGramPacket = new DatagramPacket(pData, 0, pData.length, pTarget, pTargetPort);
            getSocket().send(dataGramPacket);

        } catch (IOException e) {
            onError(e);
        }
    }

    /**
     * 关闭时
     */
    protected void onClose() {
        if (null != listener) {
            listener.onClose();
        }
    }

    /**
     * 发送信息时
     *
     * @param pData
     * @param pAddress
     * @param pPort
     */
    protected void onMessage(byte[] pData, InetAddress pAddress, int pPort) {
        if (null != listener) {
            UDPMessageEvent event = new UDPMessageEvent(this, pAddress, pPort, pData);
            listener.onMessage(event);
        }
    }

    /**
     * 出错时
     *
     * @param pError
     */
    protected void onError(Throwable pError) {
        if (null != listener) {
            UDPErrorEvent event = new UDPErrorEvent(this, pError);
            listener.onError(event);
        }
    }

    /**
     * 发送信息
     *
     * @param pData
     * @throws APPErrorException
     */
    @Override
    public void send(byte[] pData) throws APPErrorException {
        send(targetAddress, targetPort, pData);
    }

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     *
     * @param pData 数据
     * @throws APPErrorException
     */
    @Override
    public void multiSend(byte[] pData) throws APPErrorException {
        multiSend(targetAddress, targetPort, pData, -1);
    }

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     *
     * @param pTarget     目标IP
     * @param pTargetPort 目标端口
     * @param pData       数据
     * @throws APPErrorException
     */
    @Override
    public void multiSend(InetAddress pTarget, int pTargetPort, byte[] pData) throws APPErrorException {
        multiSend(pTarget, pTargetPort, pData, -1);
    }

    /**
     * 多包发送（如果数据长度超出限制则分包发送）
     *
     * @param pTarget     目标IP
     * @param pTargetPort 目标端口
     * @param pData       数据
     * @param pLength     单包长度
     * @throws APPErrorException
     */
    @Override
    public void multiSend(InetAddress pTarget, int pTargetPort, byte[] pData, int pLength) throws APPErrorException {
        int packetsize = pLength;
        if (packetsize < 1) {
            packetsize = bufferPacketSize;
        }

        try {
            onMessage(pData, pTarget, pTargetPort);
            int count = (pData.length / packetsize) + 1;
            int off = 0;
            for (int i = 0; i < count; i++) {
                if (off >= pData.length) {break;}
                int length = pData.length - off > packetsize ? packetsize : pData.length - off;
                DatagramPacket dataGramPacket = new DatagramPacket(pData, off, length, pTarget, pTargetPort);
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
            targetAddress = null;
            listener = null;
            close();
        } catch (Throwable ex) {
        }
    }


}
