package tgtools.net.udp;

import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;
import tgtools.net.udp.listener.IUDPServerListener;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：13:35
 */
public interface IUDPServer extends IDispose {
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
    void setListener(IUDPServerListener p_Listener);

    /**
     * 开始监听 (循环获取信息，阻塞当前线程)
     * @param p_Port
     * @throws APPErrorException
     */
    void start(int p_Port) throws APPErrorException;
    /**
     * 开始监听 (循环获取信息，使用线程)
     * @param p_Port
     * @throws APPErrorException
     */
    void startWithThread(int p_Port) throws APPErrorException;

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
