package tgtools.message;

import tgtools.util.DateUtil;
import tgtools.util.GUID;
import tgtools.util.StringUtil;

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
        m_IsComplete=false;
        m_Sender= StringUtil.EMPTY_STRING;
        m_Reciver= StringUtil.EMPTY_STRING;
        m_Content= StringUtil.EMPTY_STRING;
    }
    private String m_MessageID;
    private String m_Sender;
    private String m_Reciver;
    private String m_Content;
    private Date m_CreateTime;
    private boolean m_IsComplete;





    /**
     * 获取消息ID
     * @return
     */
    public String getMessageID() {
        return m_MessageID;
    }

    /**
     * 设置消息ID
     * @param p_MessageID
     */
    public void setMessageID(String p_MessageID) {
        m_MessageID = p_MessageID;
    }
    /**
     * 获取发送者
     * @return
     */
    public String getSender() {
        return m_Sender;
    }

    /**
     * 设置发送者
     * @return
     */
    public void setSender(String p_Sender) {
        m_Sender = p_Sender;
    }

    /**
     * 获取接收者
     * @return
     */
    public String getReciver() {
        return m_Reciver;
    }

    /**
     * 设置接收者
     * @param p_Reciver
     */
    public void setReciver(String p_Reciver) {
        m_Reciver = p_Reciver;
    }

    /**
     * 获取内容
     * @return
     */
    public String getContent() {
        return m_Content;
    }

    /**
     * 设置内容
     * @param p_Content
     */
    public void setContent(String p_Content) {
        m_Content = p_Content;
    }

    /**
     * 获取消息创建时间
     * @return
     */
    public Date getCreateTime() {
        return m_CreateTime;
    }

    /**
     * 设置创建时间
     * @param p_CreateTime
     */
    public void setCreateTime(Date p_CreateTime) {
        m_CreateTime = p_CreateTime;
    }
    /**
     * 是否消息处理结束，true：表示不再剩下的让监听处理 默认false
     * @return
     */
    public boolean getIsComplete() {
        return m_IsComplete;
    }

    /**
     * 设置消息结束标识
     * @param p_IsComplete
     */
    public void setIsComplete(boolean p_IsComplete) {
        m_IsComplete = p_IsComplete;
    }
}
