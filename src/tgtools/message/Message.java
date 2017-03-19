package tgtools.message;

import tgtools.util.DateUtil;
import tgtools.util.GUID;

import java.util.Date;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：15:24
 */
public class Message {
    public Message()
    {
        m_CreateTime= DateUtil.getCurrentDate();
        m_MessageID= GUID.newGUID();
    }
    private String m_MessageID;
    private String m_Sender;
    private String m_Reciver;
    private String m_Content;
    private Date m_CreateTime;
    private boolean m_IsComplete;

    public String getMessageID() {
        return m_MessageID;
    }

    public String getSender() {
        return m_Sender;
    }

    public void setSender(String p_Sender) {
        m_Sender = p_Sender;
    }

    public String getReciver() {
        return m_Reciver;
    }

    public void setReciver(String p_Reciver) {
        m_Reciver = p_Reciver;
    }

    public String getContent() {
        return m_Content;
    }

    public void setContent(String p_Content) {
        m_Content = p_Content;
    }

    public Date getCreateTime() {
        return m_CreateTime;
    }

    public boolean getIsComplete() {
        return m_IsComplete;
    }

    public void setIsComplete(boolean p_IsComplete) {
        m_IsComplete = p_IsComplete;
    }
}
