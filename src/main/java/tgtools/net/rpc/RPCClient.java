package tgtools.net.rpc;

import tgtools.exceptions.APPErrorException;
import tgtools.net.WebClient;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 一个通用的rpc（soap webservice 客户端类）
 */
public class RPCClient {
    private WebClient m_WebClient = new WebClient();
    private String m_Url;

    /**
     * 构造
     *
     * @param p_Url 访问的url
     */
    public RPCClient(String p_Url) {
        m_Url = p_Url;
    }

    public int getConnectTimeout() {
        return m_WebClient.getConnectTimeout();
    }

    public void setConnectTimeout(int pTimeOut) {
        m_WebClient.setConnectTimeout(pTimeOut);
    }

    public int getReadTimeout() {
        return m_WebClient.getReadTimeout();
    }

    public void setReadTimeout(int pTimeOut) {
        m_WebClient.setReadTimeout(pTimeOut);
    }

    /**
     * 访问
     *
     * @param p_Body 请求的XML内容
     * @return 字符串结果
     * @throws APPErrorException
     */
    public String invoke(tgtools.net.rpc.RequestBody p_Body) throws APPErrorException {
        m_WebClient.setUrl(m_Url);
        return m_WebClient.doInvokeAsString(parseRequest(p_Body));
    }


    /**
     * 执行 并返回流
     *
     * @param p_Body
     * @return
     * @throws APPErrorException
     */
    private InputStream doInvokeAsStream(tgtools.net.rpc.RequestBody p_Body) throws APPErrorException {
        m_WebClient.setUrl(m_Url);
        return m_WebClient.doInvokeAsStream(parseRequest(p_Body));//doInvokeAsByte(p_Body).getResponseBodyAsStream();
    }

    /**
     * 执行并返回字符串
     *
     * @param p_Body
     * @return
     * @throws APPErrorException
     */
    private String doInvoke(tgtools.net.rpc.RequestBody p_Body) throws APPErrorException {
        try {
            m_WebClient.setUrl(m_Url);
            return m_WebClient.doInvokeAsString(parseRequest(p_Body));// doInvokeAsByte(p_Body).getResponseBodyAsString();
        } catch (Exception e) {
            throw new APPErrorException("获取字符内容出错", e);
        }

    }

    /**
     * 执行并将返回的信息放入 response
     *
     * @param p_Body     需要发送的内容
     * @param p_Response 接收内容的对象
     * @param <T>
     * @throws APPErrorException
     */
    public <T extends tgtools.net.rpc.ResponseBody> void invoke(tgtools.net.rpc.RequestBody p_Body, T p_Response) throws APPErrorException {
        p_Response.init(doInvokeAsStream(p_Body));
        p_Response.parse();
    }

    /**
     * 执行并将返回的信息放入 response
     * 注：参数名为in0 in1 时才能用，否则请使用 requestbody
     *
     * @param method     方法名称
     * @param param      参数
     * @param p_Response 接收内容的对象
     * @param <T>
     * @throws APPErrorException
     */
    public <T extends tgtools.net.rpc.ResponseBody> void invoke(String method, Object[] param, T p_Response) throws APPErrorException {
        tgtools.net.rpc.RequestBody body = new tgtools.net.rpc.RequestBody();
        body.setMethod(method);
        body.setParam(param);
        invoke(body, p_Response);
    }

    private String parseRequest(tgtools.net.rpc.RequestBody body) throws APPErrorException {
        StringWriter ss = new StringWriter();
        XMLStreamWriter write = null;
        try {
            write = tgtools.xml.XmlSerializeHelper.createXmlOutputFactory()
                    .createXMLStreamWriter(ss);

            body.writeXml(write);
            write.close();
            return ss.toString();
        } catch (XMLStreamException e1) {
            throw new APPErrorException("请求数据转换失败！", e1);
        } finally {
            if (null != write) {
                try {
                    write.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}
