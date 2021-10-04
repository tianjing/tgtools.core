package tgtools.message;

import tgtools.util.DateUtil;
import tgtools.util.GUID;
import tgtools.util.StringUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：15:24
 */
public class Message implements Serializable {
    public Message()
    {
        createTime= DateUtil.getCurrentDate();
        messageId= GUID.newGUID();
        isComplete=false;
        sender= StringUtil.EMPTY_STRING;
        receiver= StringUtil.EMPTY_STRING;
        content= StringUtil.EMPTY_STRING;
    }
    private String messageId;
    private String sender;
    private String receiver;
    private String content;
    private String event;
    private Date createTime;
    private boolean isComplete;


    public String getEvent() {
        return event;
    }

    public void setEvent(String pEvent) {
        event = pEvent;
    }

    /**
     * 获取消息ID
     * @return
     */
    public String getmessageId() {
        return messageId;
    }

    /**
     * 设置消息ID
     * @param pMessageId
     */
    public void setmessageId(String pMessageId) {
        messageId = pMessageId;
    }
    /**
     * 获取发送者
     * @return
     */
    public String getSender() {
        return sender;
    }

    /**
     * 设置发送者
     * @return
     */
    public void setSender(String pSender) {
        sender = pSender;
    }

    /**
     * 获取接收者
     * @return
     */
    public String getReciver() {
        return receiver;
    }

    /**
     * 设置接收者
     * @param pReciver
     */
    public void setReciver(String pReciver) {
        receiver = pReciver;
    }

    /**
     * 获取内容
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     * @param pContent
     */
    public void setContent(String pContent) {
        content = pContent;
    }

    /**
     * 获取消息创建时间
     * @return
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param pCreateTime
     */
    public void setCreateTime(Date pCreateTime) {
        createTime = pCreateTime;
    }
    /**
     * 是否消息处理结束，true：表示不再剩下的让监听处理 默认false
     * @return
     */
    public boolean getIsComplete() {
        return isComplete;
    }

    /**
     * 设置消息结束标识
     * @param pIsComplete
     */
    public void setIsComplete(boolean pIsComplete) {
        isComplete = pIsComplete;
    }
}
