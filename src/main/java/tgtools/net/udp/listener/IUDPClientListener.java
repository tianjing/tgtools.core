package tgtools.net.udp.listener;

/**
 * @author tianjing
 */
public interface IUDPClientListener {
    /**
     * onError
     * @param pEvent
     */
    void onError(UDPErrorEvent pEvent);

    /**
     * onMessage
     * @param pEvent
     */
    void onMessage(UDPMessageEvent pEvent);

    /**
     *
     */
    void onClose();
}
