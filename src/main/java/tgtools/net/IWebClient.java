package tgtools.net;

import tgtools.exceptions.APPErrorException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by tian_ on 2016-06-25.
 */
public interface IWebClient {

    /**
     * 设置url
     * @param p_Url
     */
    void setUrl(String p_Url);

    /**
     *获取URL
     * @return
     */
    String getUrl();

    /**
     *设置字符编码
     * @param p_Encoding
     */
    void setEncoding(String p_Encoding);

    /**
     * 获取字符编码
     * @return
     */
    String getEncoding();

    /**
     * 设置 方法 如 GET POST等
     * @param p_Method
     */
    void setMethod(String p_Method);

    /**
     * 获取 方法
     * @return
     */
    String getMethod();

    /**
     * 设置 是否启用gzip
     * @param p_GZip
     */
    void setGZip(boolean p_GZip);

    /**
     * 获取是否使用gzip
     * @return
     */
    boolean getGZip();

    /**
     * 添加请求头
     * @param p_Name
     * @param p_Value
     */
    void addHead(String p_Name, String p_Value);

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
     * @param p_Input
     * @return
     * @throws APPErrorException
     */
    byte[] doInvokeAsByte (InputStream p_Input) throws APPErrorException;

    /**
     * 获取响应的头信息
     * @return
     */
    public Map<String, List<String>> getResponseHeader();
}
