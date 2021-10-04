package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.net.udp.listener.IUDPServerListener;
import tgtools.net.udp.listener.UDPErrorEvent;
import tgtools.net.udp.listener.UDPMessageEvent;
import tgtools.tasks.Task;
import tgtools.tasks.TaskContext;
import tgtools.util.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 名  称：
 * @author tianjing
 * 功  能：udp 简单服务端，用于接收消息
 * 时  间：14:44
 */
public class UDPServer implements IUDPServer {

    protected DatagramSocket socket = null;
    protected int port;
    protected IUDPServerListener listener;
    protected int bufferSize = 2048;
    protected int timeOut = 0;
    protected Task listenTask;

    protected DatagramSocket getSocket() throws APPErrorException {
        if (null == socket && port > 0) {
            try {
                socket = new DatagramSocket(port);
                socket.setReceiveBufferSize(bufferSize);
                socket.setSendBufferSize(bufferSize);
                if(timeOut>0) {
                    socket.setSoTimeout(timeOut);
                }

                return socket;
            } catch (Exception ex) {
                throw new APPErrorException("创建Udp对象出错，原因：" + ex.getMessage());
            }
        }
        return socket;
    }

    /**
     * 设置超时
     * @param pTimeOut
     */
    @Override
    public void setTimeOut(int pTimeOut) {
        timeOut = pTimeOut;
    }

    /**
     * 设置缓冲
     * @param pBuffeSize
     */
    @Override
    public void setBuffeSize(int pBuffeSize) {
        bufferSize=pBuffeSize;
    }

    /**
     * 设置监听
     * @param pListener
     */
    @Override
    public void setListener(IUDPServerListener pListener) {
        listener = pListener;
    }

    /**
     * 启动监听
     * @param pPort
     * @throws APPErrorException
     */
    @Override
    public void start(int pPort) throws APPErrorException {
        validCanRun();
        port = pPort;
        onStart();
        listenTask=new UdpListenerTask(this);
        listenTask.run(null);

    }

    @Override
    public void startWithThread(int pPort) throws APPErrorException {
        validCanRun();
        port = pPort;
        onStart();
        listenTask=new UdpListenerTask(this);
        listenTask.runThread(null);
    }

    @Override
    public void stop() throws APPErrorException {
        if(null==listenTask)
        {return;}
        listenTask.cancel();
        close();
        listenTask=null;
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    protected void validCanRun() throws APPErrorException {
        if(null!=listenTask)
        {
            throw new APPErrorException("正在监听无法启动。");
        }
    }
    protected void receive()throws APPErrorException
    {
        byte[] backbuf = new byte[bufferSize];
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
    /**
     * 关闭
     */
    protected void close() {
        onClose();
        if (null != socket) {
            socket.close();
            socket = null;
        }
    }

    /**
     * 释放
     */
    @Override
    public void Dispose() {
        close();
        listener = null;
    }

    /**
     *
     */
    protected void onClose() {
        if (null != listener) {
            listener.onClose();
        }
    }

    /**
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
     *
     */
    protected void onStart() {
        if (null != listener) {
            listener.onStart();
        }
    }
    public void send(InetAddress pTarget, int pTargetPort, byte[] pData) throws APPErrorException {
        try {
            DatagramPacket dataGramPacket = new DatagramPacket(pData, 0, pData.length, pTarget, pTargetPort);
            getSocket().send(dataGramPacket);

        } catch (IOException e) {
            onError(e);
        }
    }

    public static void main(String[] args) throws UnknownHostException, UnsupportedEncodingException {
        UDPServer server = new UDPServer();
        try {
            server.setListener(new IUDPServerListener() {
                @Override
                public void onError(UDPErrorEvent pEvent) {

                }

                @Override
                public void onMessage(UDPMessageEvent pEvent) {
                    String receiveMessage = new String(pEvent.getMeaage());
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

            server.startWithThread(6000);

            InetAddress add=InetAddress.getByName("192.168.88.128");
            server.send(add,45454,"你好".getBytes("GBK"));
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            System.out.println("停止监听");
            server.stop();


            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            System.out.println("启动监听");
            server.startWithThread(60000);

        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }

   private class UdpListenerTask extends tgtools.tasks.Task{
        public UdpListenerTask(UDPServer pServer){
            server=pServer;
        }
        private UDPServer server;
       @Override
       protected boolean canCancel() {
           return true;
       }

       @Override
       public void run(TaskContext pParam) {
           if(null==server)
           {
               return;
           }
           while(true)
           {
               if(this.isCancel())
               {return;}
                try {
                    server.receive();
                }catch (Throwable e)
                {
                    LogHelper.error("","接收信息错误；原因："+e.getMessage(),"UdpListenerTask",e);
                }
               if(this.isCancel())
               {return;}

           }
       }
   }
}
