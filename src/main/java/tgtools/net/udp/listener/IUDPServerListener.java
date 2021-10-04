package tgtools.net.udp.listener;

/**
 * @author tianjing
 */
public interface IUDPServerListener {
    /**
     * onError
     * @param pEvent
     */
    void onError(UDPErrorEvent pEvent);

    /**
     *  onMessage
     * @param pEvent
     */
    void onMessage(UDPMessageEvent pEvent);

    /**
     * onClose
     */
    void onClose();

    /**
     * onStart
     */
    void onStart();
}
