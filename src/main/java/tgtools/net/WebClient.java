package tgtools.net;

import tgtools.exceptions.APPErrorException;
import tgtools.util.GUID;
import tgtools.util.StringUtil;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author tianjing
 */
public class WebClient implements IWebClient {
    private String url;
    private String encoding = "UTF-8";
    private String method = "POST";
    private Map<String, String> head;
    private boolean gZip;
    private Map<String, List<String>> responseHeader = new HashMap<String, List<String>>();
    private int responseCode;
    private int connectTimeout = -1;
    private int readTimeout = -1;
    private HashMap<String, List<HttpCookie>> cookies;
    private boolean useAutoRedirect = true;

    public WebClient() {
        cookies = new HashMap<String, List<HttpCookie>>();
        head = new HashMap<String, String>();
        head.put("accept", "*/*");
        head.put("connection", "Keep-Alive");
        head.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko");
        head.put("Content-Type", "text/xml; charset=utf-8");
    }

    public HashMap<String, List<HttpCookie>> getCookies() {
        return cookies;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int pResponseCode) {
        responseCode = pResponseCode;
    }

    public boolean getUseAutoRedirect() {
        return useAutoRedirect;
    }

    public void setUseAutoRedirect(boolean pUseAutoRedirect) {
        useAutoRedirect = pUseAutoRedirect;
    }

    @Override
    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, List<String>> pResponseHeader) {
        responseHeader.clear();
        responseHeader.putAll(pResponseHeader);
    }

    public void clearResponseHeader() {
        responseHeader.clear();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String pUrl) {
        url = pUrl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int pTimeOut) {
        connectTimeout = pTimeOut;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public void setReadTimeout(int pTimeOut) {
        readTimeout = pTimeOut;
    }

    /**
     * 获取字符编码
     */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /**
     * 设置字符编码
     *
     * @param pEncoding
     */
    @Override
    public void setEncoding(String pEncoding) {
        encoding = pEncoding;
    }

    @Override
    public String getMethod() {
        return method;
    }

    /**
     * 设置HTTP 方法 如 ：GET,POST
     *
     * @param pMethod
     */
    @Override
    public void setMethod(String pMethod) {
        method = pMethod;
    }

    @Override
    public boolean getGZip() {
        return gZip;
    }

    /**
     * 设置是否使用gzip 压缩（注：只适用POST）
     *
     * @param pGZip
     */
    @Override
    public void setGZip(boolean pGZip) {
        gZip = pGZip;
    }

    /**
     * 增加head
     *
     * @param pName
     * @param pValue
     */
    @Override
    public void addHead(String pName, String pValue) {
        head.put(pName, pValue);
    }

    @Override
    public Map<String, String> getHead() {
        return head;
    }

    @Override
    public File doInvokeAsFile(String params) throws APPErrorException {
        URLConnection conn = null;
        try {
            conn = doInvoke(params);
            return parseFile(getResponseStream(conn));
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public File doInvokeAsFile(Map<String, String> params) throws APPErrorException {
        return doInvokeAsFile(getParamsString(params));
    }

    public void setContentType(String pType) {
        if (!StringUtil.isNullOrEmpty(pType)) {
            getHead().put("Content-Type", pType);
        }
    }

    /**
     * 请求并返回收到的流
     *
     * @param params 输入的参数 可以是任意字符 如常规 a=1&b=2 或json xml
     * @return
     * @throws APPErrorException
     */
    @Override
    public InputStream doInvokeAsStream(String params) throws APPErrorException {
        URLConnection conn = null;
        try {
            conn = doInvoke(params);
            return getResponseStream(doInvoke(params));
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 请求并返回收到的流
     *
     * @param params 常规参数  params 会 拼接成 a=1&b=2 的形式
     * @return
     * @throws APPErrorException
     */
    @Override
    public InputStream doInvokeAsStream(Map<String, String> params) throws APPErrorException {
        return doInvokeAsStream(getParamsString(params));
    }

    /**
     * 请求并返回收到的字符
     *
     * @param params 输入的参数 可以是任意字符 如常规 a=1&b=2 或json xml
     * @return
     * @throws APPErrorException
     */
    @Override
    public String doInvokeAsString(String params) throws APPErrorException {
        URLConnection conn = null;
        try {
            conn = doInvoke(params);
            boolean isgzip = isGzip(conn.getHeaderFields());
            String encoding = getResponseEncoding(conn.getHeaderFields());
            if (isgzip) {
                return parseGZipString(getResponseStream(conn), encoding);
            }
            return parseString(getResponseStream(conn), encoding);
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 请求并返回收到的字符
     *
     * @param params 常规参数  params 会 拼接成 a=1&b=2 的形式
     * @return
     * @throws APPErrorException
     */
    @Override
    public String doInvokeAsString(Map<String, String> params) throws APPErrorException {
        return doInvokeAsString(getParamsString(params));
    }

    /**
     * 请求并返回收到的字节
     *
     * @param params 输入的参数 可以是任意字符 如常规 a=1&b=2 或json xml
     * @return
     * @throws APPErrorException
     */
    @Override
    public byte[] doInvokeAsByte(String params) throws APPErrorException {
        URLConnection conn = null;
        try {
            conn = doInvoke(params);
            return parseByte(getResponseStream(conn));
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        } finally {
            closeConnection(conn);
        }
    }

    public InputStream getResponseStream(URLConnection pUrlConnection) throws IOException {
        sun.net.www.protocol.http.HttpURLConnection conn1 = null;
        if (pUrlConnection instanceof sun.net.www.protocol.http.HttpURLConnection) {
            conn1 = (sun.net.www.protocol.http.HttpURLConnection) pUrlConnection;
        }
        if (responseCode == HttpURLConnection.HTTP_BAD_METHOD || responseCode == HttpURLConnection.HTTP_BAD_REQUEST
                || responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_BAD_GATEWAY ||
                responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            if (null != conn1) {
                return conn1.getErrorStream();
            } else {
                return pUrlConnection.getInputStream();
            }
        } else {
            return pUrlConnection.getInputStream();
        }

    }

    public InputStream doInvokeAsStream(InputStream pInput) throws APPErrorException {
        URLConnection conn = null;
        try {
            conn = doInvoke(pInput);
            return getResponseStream(conn);
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    public byte[] doInvokeAsByte(InputStream pInput) throws APPErrorException {
        InputStream is = doInvokeAsStream(pInput);
        return parseByte(is);
    }

    /**
     * 请求并返回收到的字节
     *
     * @param params 常规参数  params 会 拼接成 a=1&b=2 的形式
     * @return
     * @throws APPErrorException
     */
    @Override
    public byte[] doInvokeAsByte(Map<String, String> params) throws APPErrorException {
        return doInvokeAsByte(getParamsString(params));
    }

    /**
     * 将gzip信息解压
     *
     * @param pStream   输入的gzip 数据
     * @param pEncoding 字符编码
     * @return
     * @throws APPErrorException
     */
    public String parseGZipString(InputStream pStream, String pEncoding) throws APPErrorException {
        GZIPInputStream gzin = null;
        try {
            gzin = new GZIPInputStream(pStream);

            InputStreamReader isr = new InputStreamReader(gzin, pEncoding);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String tempbf;
            while ((tempbf = br.readLine()) != null) {
                sb.append(tempbf);
                sb.append("\r\n");
            }
            isr.close();
            gzin.close();
            return sb.toString();


        } catch (IOException e) {
            throw new APPErrorException("GZIP数据解析错误", e);
        }
    }

    public String parseString(URLConnection pUrlConnection) throws APPErrorException {
        try {
            return parseString(getResponseStream(pUrlConnection), encoding);
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        }
    }

    /**
     * 将输入的流转换成 字符
     *
     * @param pStream   输入的流
     * @param pEncoding 字符编码
     * @return
     * @throws APPErrorException
     */
    public String parseString(InputStream pStream, String pEncoding) throws APPErrorException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[4096];
            int count = -1;
            while ((count = pStream.read(data, 0, 4096)) != -1) {
                outStream.write(data, 0, count);
            }

            return new String(outStream.toByteArray(), pEncoding);

        } catch (UnsupportedEncodingException e) {
            throw new APPErrorException("字符串转换失败", e);
        } catch (IOException e) {
            throw new APPErrorException("输入信息获取错误", e);
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将输入的流转换成 字节
     *
     * @param pStream
     * @return
     * @throws APPErrorException
     */
    protected File parseFile(InputStream pStream) throws APPErrorException {
        FileOutputStream outStream = null;
        try {

            File vFile = File.createTempFile(GUID.newGUID() + "_tmp", ".tmp");

            outStream = new FileOutputStream(vFile);

            byte[] data = new byte[4096];
            int count = -1;

            while ((count = pStream.read(data, 0, 4096)) != -1) {
                outStream.write(data, 0, count);
            }

            data = null;
            return vFile;
        } catch (IOException e) {
            throw new APPErrorException("写入临时文件出错", e);
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将输入的流转换成 字节
     *
     * @param pStream
     * @return
     * @throws APPErrorException
     */
    protected byte[] parseByte(InputStream pStream) throws APPErrorException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        byte[] data = new byte[4096];
        try {
            int count = -1;

            while ((count = pStream.read(data, 0, 4096)) != -1) {
                outStream.write(data, 0, count);
            }

            data = null;
            return outStream.toByteArray();
        } catch (IOException e) {
            throw new APPErrorException("获取输入的信息出错", e);
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将参数 转换成 a=1&b=2的形式
     *
     * @param pParams
     * @return
     */
    protected String getParamsString(Map<String, String> pParams) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> item : pParams.entrySet()) {
            String key = item.getKey();
            String value = item.getValue();

            sb.append(key + "=" + value + "&");

        }
        return StringUtil.removeLast(sb.toString(), '&');
    }

    public void closeConnection(URLConnection pConn) {
        if (null != pConn && (pConn instanceof HttpURLConnection)) {
            ((HttpURLConnection) pConn).disconnect();
        }
    }

    /**
     * 设置请求头
     *
     * @param pConn
     */
    protected void setRequestHead(URLConnection pConn) {
        for (Map.Entry<String, String> item : head.entrySet()) {
            if ("GET".equals(getMethod().toUpperCase()) && "Content-Type".equals(item.getKey())) {
                continue;
            }
            pConn.setRequestProperty(item.getKey(), null == item.getValue() ? "" : item.getValue());
        }
        URL vUrl = pConn.getURL();
        String domain = vUrl.getHost();
        String path = vUrl.getPath().substring(0, vUrl.getPath().substring(1).indexOf("/") + 1);
        if (StringUtil.isNullOrEmpty(path) && StringUtil.isNotEmpty(vUrl.getPath())) {
            path = vUrl.getPath();
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<HttpCookie>> item : cookies.entrySet()) {
            if (domain.endsWith(item.getKey())) {
                for (HttpCookie cookie : item.getValue()) {
                    if ("/".equals(cookie.getPath()) || path.equals(cookie.getPath())) {
                        sb.append(cookie.toString() + "; ");
                    }
                }
            }
        }
        if (sb.length() > 1) {
            pConn.setRequestProperty("Cookie", sb.toString());
        }
    }

    /**
     * 获取响应头中 字符编码 如果不存在 则使用 设置的encoding
     *
     * @param pReponseHead
     * @return
     */
    protected String getResponseEncoding(Map<String, List<String>> pReponseHead) {
        if (null == pReponseHead || pReponseHead.size() < 1 || !pReponseHead.containsKey("Content-Type")) {
            return encoding;
        }
        List<String> names = pReponseHead.get("Content-Type");
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (name.indexOf("charset=") >= 0) {
                String[] codes = name.split("charset=");
                if (StringUtil.isNullOrEmpty(codes[1])) {
                    return codes[1];
                }
            }
        }
        return encoding;
    }

    /**
     * 判断响应数据是否为GZIP
     *
     * @param pReponseHead
     * @return
     */
    protected boolean isGzip(Map<String, List<String>> pReponseHead) {
        Object value = pReponseHead.get("content-encoding");
        String valuestr = null != value ? value.toString() : "";

        return valuestr.indexOf("gzip") > -1;
    }

    public URLConnection doInvoke(String pParam) throws APPErrorException {
        try {
            ByteArrayInputStream dd = new ByteArrayInputStream(pParam.getBytes(encoding));
            return doInvoke(dd);
        } catch (UnsupportedEncodingException e) {
            throw new APPErrorException("参数转码失败；参数：" + pParam + ";编码：" + encoding + ";原因：" + e.getMessage());
        }
    }

    /**
     * 请求
     *
     * @param pInput
     * @return
     * @throws APPErrorException
     */
    public URLConnection doInvoke(InputStream pInput) throws APPErrorException {

        String vUrl = url;
        OutputStream out = null;
        try {

            setResponseCode(0);
            clearResponseHeader();

            URL realUrl = new URL(vUrl);

            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();

            ((HttpURLConnection) conn).setInstanceFollowRedirects(false);


            if (connectTimeout > 0) {
                conn.setConnectTimeout(connectTimeout);
            }
            if (readTimeout > 0) {
                conn.setReadTimeout(readTimeout);
            }
            // 设置通用的请求属性
            setRequestHead(conn);


            if ("POST".equals(method.toUpperCase())) {
                // 发送POST请求必须设置如下两行
                conn.setDoOutput(true);
                conn.setDoInput(true);
                // 获取URLConnection对象对应的输te出流
                out = conn.getOutputStream();
                // 发送请求参数
                byte[] data = parseByte(pInput);
                out.write(data);
                // flush输出流的缓冲
                out.flush();
                // 定义BufferedReader输入流来读取URL的响应

            } else if ("GET".equals(method.toUpperCase())) {
                conn.connect();
            }
            setResponseHeader(conn.getHeaderFields());
            //处理 cookie
            if (null != getResponseHeader() && getResponseHeader().size() > 0 && getResponseHeader().containsKey("Set-Cookie")) {
                List<String> vCookies = getResponseHeader().get("Set-Cookie");
                if (null != cookies) {
                    for (int i = 0, size = vCookies.size(); i < size; i++) {
                        String cookieString = vCookies.get(i);
                        URL resultUrl = conn.getURL();
                        String domain = resultUrl.getHost();
                        List<HttpCookie> httpcookies = HttpCookie.parse(cookieString);
                        if (!cookies.containsKey(domain)) {
                            cookies.put(domain, new ArrayList<HttpCookie>());
                        }
                        for (HttpCookie httpCookie : httpcookies) {
                            addCookie(domain, httpCookie);
                        }
                    }
                }
            }
            if (conn instanceof java.net.HttpURLConnection) {
                java.net.HttpURLConnection conn1 = (java.net.HttpURLConnection) conn;
                setResponseCode(conn1.getResponseCode());
            }
            //302 自动跳转
            if (useAutoRedirect && 302 == getResponseCode()) {
                conn = doRedirect(conn);
            }
            return conn;

        } catch (Exception e) {
            throw new APPErrorException("请求出错！", e);
        }
    }

    protected void addCookie(String pDomain, HttpCookie pHttpCookie) {
        if (StringUtil.isNullOrEmpty(pHttpCookie.getDomain())) {
            cookies.get(pDomain).add(pHttpCookie);
        } else {
            if (!cookies.containsKey(pHttpCookie.getDomain())) {
                cookies.put(pHttpCookie.getDomain(), new ArrayList<HttpCookie>());
            }
            List<HttpCookie> vCookies = cookies.get(pHttpCookie.getDomain());
            if (!vCookies.contains(pHttpCookie)) {
                cookies.get(pHttpCookie.getDomain()).add(pHttpCookie);
            } else {
                vCookies.remove(vCookies.indexOf(pHttpCookie));
                vCookies.add(pHttpCookie);
            }
        }
    }

    /**
     * 重定向
     *
     * @return
     * @throws APPErrorException
     */
    public URLConnection doRedirect(URLConnection pUrlConnection) throws APPErrorException {
        String urlRedirect = pUrlConnection.getHeaderField("Location");
        if (!StringUtil.isNullOrEmpty(urlRedirect)) {
            String preMethod = getMethod();
            setMethod("GET");
            setUrl(urlRedirect);
            closeConnection(pUrlConnection);
            URLConnection vUrlConnection = doInvoke(StringUtil.EMPTY_STRING);
            setMethod(preMethod);
            return vUrlConnection;
        }
        return null;
    }
}
