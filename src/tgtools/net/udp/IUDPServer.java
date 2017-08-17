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
    void setTimeOut(int p_TimeOut);
    void setBuffeSize(int p_BuffeSize);
    void setListener(IUDPServerListener p_Listener);
    void start(int p_Port) throws APPErrorException;

}
