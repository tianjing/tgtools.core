package tgtools.net.udp.listener;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：16:09
 */
public interface IUDPClientListener {
    void onError(UDPErrorEvent p_Event);
    void onMessage(UDPMessageEvent p_Event);
    void onClose();
}
