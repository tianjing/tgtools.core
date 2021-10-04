package tgtools.net.rpc;

import tgtools.exceptions.APPErrorException;
import tgtools.net.WebClient;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * 一个通用的rpc（soap webservice 客户端类）
 * @author tianjing
 */
public class RPCClient {
    private WebClient webClient = new WebClient();
    private String url;

    /**
     * 构造
     *
     * @param pUrl 访问的url
     */
    public RPCClient(String pUrl) {
        url = pUrl;
    }

    public int getConnectTimeout() {
        return webClient.getConnectTimeout();
    }

    public void setConnectTimeout(int pTimeOut) {
        webClient.setConnectTimeout(pTimeOut);
    }

    public int getReadTimeout() {
        return webClient.getReadTimeout();
    }

    public void setReadTimeout(int pTimeOut) {
        webClient.setReadTimeout(pTimeOut);
    }

    /**
     * 访问
     *
     * @param pBody 请求的XML内容
     * @return 字符串结果
     * @throws APPErrorException
     */
    public String invoke(tgtools.net.rpc.RequestBody pBody) throws APPErrorException {
        webClient.setUrl(url);
        return webClient.doInvokeAsString(parseRequest(pBody));
    }


    /**
     * 执行 并返回流
     *
     * @param pBody
     * @return
     * @throws APPErrorException
     */
    private InputStream doInvokeAsStream(tgtools.net.rpc.RequestBody pBody) throws APPErrorException {
        webClient.setUrl(url);
        return webClient.doInvokeAsStream(parseRequest(pBody));
    }

    /**
     * 执行并返回字符串
     *
     * @param pBody
     * @return
     * @throws APPErrorException
     */
    private String doInvoke(tgtools.net.rpc.RequestBody pBody) throws APPErrorException {
        try {
            webClient.setUrl(url);
            return webClient.doInvokeAsString(parseRequest(pBody));
        } catch (Exception e) {
            throw new APPErrorException("获取字符内容出错", e);
        }

    }

    /**
     * 执行并将返回的信息放入 response
     *
     * @param pBody     需要发送的内容
     * @param pResponse 接收内容的对象
     * @param <T>
     * @throws APPErrorException
     */
    public <T extends tgtools.net.rpc.ResponseBody> void invoke(tgtools.net.rpc.RequestBody pBody, T pResponse) throws APPErrorException {
        pResponse.init(doInvokeAsStream(pBody));
        pResponse.parse();
    }

    /**
     * 执行并将返回的信息放入 response
     * 注：参数名为in0 in1 时才能用，否则请使用 requestbody
     *
     * @param method     方法名称
     * @param param      参数
     * @param pResponse 接收内容的对象
     * @param <T>
     * @throws APPErrorException
     */
    public <T extends tgtools.net.rpc.ResponseBody> void invoke(String method, Object[] param, T pResponse) throws APPErrorException {
        tgtools.net.rpc.RequestBody body = new tgtools.net.rpc.RequestBody();
        body.setMethod(method);
        body.setParam(param);
        invoke(body, pResponse);
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
