package tgtools.net;

import tgtools.exceptions.APPErrorException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author tianjing
 */
public interface IWebClient {

    /**
     * 设置连接超时
     * @param pTimeOut
     */
    void setConnectTimeout(int pTimeOut);

    /**
     * 设置响应超时
     * @param pTimeOut
     */
    void setReadTimeout(int pTimeOut);
    /**
     * 设置url
     * @param pUrl
     */
    void setUrl(String pUrl);

    /**
     *获取URL
     * @return
     */
    String getUrl();

    /**
     *设置字符编码
     * @param pEncoding
     */
    void setEncoding(String pEncoding);

    /**
     * 获取字符编码
     * @return
     */
    String getEncoding();

    /**
     * 设置 方法 如 GET POST等
     * @param pMethod
     */
    void setMethod(String pMethod);

    /**
     * 获取 方法
     * @return
     */
    String getMethod();

    /**
     * 设置 是否启用gzip
     * @param pGZip
     */
    void setGZip(boolean pGZip);

    /**
     * 获取是否使用gzip
     * @return
     */
    boolean getGZip();

    /**
     * 添加请求头
     * @param pName
     * @param pValue
     */
    void addHead(String pName, String pValue);

    /**
     * 获取 请求头
     * @return
     */
    Map<String,String> getHead();

    /**
     * 请求并返回流
     * @param params
     * @return
     * @throws APPErrorException
     */
    InputStream doInvokeAsStream(String params)throws APPErrorException;

    /**
     *请求并返回流
     * @param params
     * @return
     * @throws APPErrorException
     */
    InputStream doInvokeAsStream(Map<String, String> params)throws APPErrorException;

    /**
     *请求并返回字符
     * @param params
     * @return
     * @throws APPErrorException
     */
    String doInvokeAsString(String params)throws APPErrorException;

    /**
     *请求并返回字符
     * @param params
     * @return
     * @throws APPErrorException
     */
    String doInvokeAsString(Map<String, String> params)throws APPErrorException;

    /**
     *请求并返回字节
     * @param params
     * @return
     * @throws APPErrorException
     */
    byte[] doInvokeAsByte(String params)throws APPErrorException;

    /**
     *请求并返回字节
     * @param params
     * @return
     * @throws APPErrorException
     */
    byte[] doInvokeAsByte(Map<String, String> params)throws APPErrorException;

    /**
     * 请求并返回字节集
     * @param pInput
     * @return
     * @throws APPErrorException
     */
    byte[] doInvokeAsByte (InputStream pInput) throws APPErrorException;

    /**
     * 获取响应的头信息
     * @return
     */
    public Map<String, List<String>> getResponseHeader();
}
