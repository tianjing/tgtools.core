package tgtools.net.udp.listener;

import tgtools.interfaces.Event;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：16:13
 */
public class UDPErrorEvent extends Event {
    private Throwable error;
    private Object sender;
    public UDPErrorEvent(Object pSender, Throwable pError) {
        sender = pSender;
        error = pError;
    }

    /**
     * 获取异常信息
     * @return
     */
    public Throwable getError() {
        return error;
    }

    /**
     * 获取对象
     * @return
     */
    public Object getSender() {
        return sender;
    }
}
