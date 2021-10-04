package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;
import tgtools.net.udp.listener.IUDPServerListener;

/**
 * @author tianjing
 */
public interface IUDPServer extends IDispose {
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
    void setListener(IUDPServerListener pListener);

    /**
     * 开始监听 (循环获取信息，阻塞当前线程)
     * @param pPort
     * @throws APPErrorException
     */
    void start(int pPort) throws APPErrorException;
    /**
     * 开始监听 (循环获取信息，使用线程)
     * @param pPort
     * @throws APPErrorException
     */
    void startWithThread(int pPort) throws APPErrorException;

    /**
     * 取消监听
     * @throws APPErrorException
     */
    void stop() throws APPErrorException;

    /**
     * 是否关闭
     * @return
     */
    boolean isClosed();

    /**
     * 是否已连接
     * @return
     */
    boolean isConnected();
}
