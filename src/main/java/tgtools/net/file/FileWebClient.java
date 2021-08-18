package tgtools.net.file;

import tgtools.exceptions.APPErrorException;
import tgtools.net.WebClient;
import tgtools.util.GUID;
import tgtools.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 田径
 * @date 2021-08-13 14:44
 * @desc
 **/
public class FileWebClient extends WebClient {
    protected static final String NEW_LINE = "\r\n";
    protected static final String MIME_STREAM = "application/octet-stream";
    protected static final int CHUNK_LEN = 40960;
    protected String boundary;
    protected boolean useAutoBuildBoundary = true;

    protected String newLine = NEW_LINE;
    protected int chunkLen = CHUNK_LEN;


    protected String buildBoundary() {
        if (getUseAutoBuildBoundary()) {
            String vBoundary = GUID.newGUID().replace("-", "");
            int vTotal = 38;
            return StringUtil.alignRight(vBoundary, vTotal, '-');
        }
        return boundary;
    }

    public String getNewLine() {
        return newLine;
    }

    public void setNewLine(String pNewLine) {
        newLine = pNewLine;
    }

    public boolean getUseAutoBuildBoundary() {
        return useAutoBuildBoundary;
    }

    public void setUseAutoBuildBoundary(boolean pUseAutoBuildBoundary) {
        useAutoBuildBoundary = pUseAutoBuildBoundary;
    }

    public int getChunkLen() {
        return chunkLen;
    }

    public void setChunkLen(int pChunkLen) {
        chunkLen = pChunkLen;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String pBoundary) {
        boundary = pBoundary;
    }

    /**
     * 请求
     *
     * @return
     * @throws APPErrorException
     */
    public URLConnection doInvokePre() throws APPErrorException {

        String url = getUrl();
        try {

            setResponseCode(0);
            clearResponseHeader();

            URL realUrl = new URL(url);

            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();

            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).setInstanceFollowRedirects(false);
                ((HttpURLConnection) conn).setChunkedStreamingMode(chunkLen);
            }
            conn.setAllowUserInteraction(false);

            if (getConnectTimeout() > 0) {
                conn.setConnectTimeout(getConnectTimeout());
            }
            if (getReadTimeout() > 0) {
                conn.setReadTimeout(getReadTimeout());
            }
            // 设置通用的请求属性
            setRequestHead(conn);


            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            return conn;
        } catch (Exception e) {
            throw new APPErrorException("请求出错！", e);
        }
    }

    public URLConnection doInvokeAfter(URLConnection pURLConnection) throws APPErrorException {
        try {
            setResponseHeader(pURLConnection.getHeaderFields());
            //处理 cookie
            if (null != getResponseHeader() && getResponseHeader().size() > 0 && getResponseHeader().containsKey("Set-Cookie")) {
                List<String> cookies = getResponseHeader().get("Set-Cookie");
                if (null != cookies) {
                    for (int i = 0, size = cookies.size(); i < size; i++) {
                        String cookie = cookies.get(i);
                        URL resultUrl = pURLConnection.getURL();
                        String domain = resultUrl.getHost();
                        List<HttpCookie> httpcookies = HttpCookie.parse(cookie);
                        if (!getCookies().containsKey(domain)) {
                            getCookies().put(domain, new ArrayList<HttpCookie>());
                        }
                        for (HttpCookie httpCookie : httpcookies) {
                            addCookie(domain, httpCookie);
                        }
                    }
                }
            }
            if (pURLConnection instanceof java.net.HttpURLConnection) {
                java.net.HttpURLConnection conn1 = (java.net.HttpURLConnection) pURLConnection;
                setResponseCode(conn1.getResponseCode());
            }
            return pURLConnection;
        } catch (Exception e) {
            throw new APPErrorException("请求出错！", e);
        }


    }

    /**
     * 请求并返回收到的流
     *
     * @return
     * @throws APPErrorException
     */
    public InputStream doInvokeFile(List<FileParam> pFileList, Map<String, String> pParam) throws APPErrorException {
        URLConnection conn = null;
        try {
            initFileParam();
            conn = doInvokePre();
            OutputStream vOutputStream = conn.getOutputStream();
            writeFile(vOutputStream, pFileList);
            writeParam(vOutputStream, pParam);
            writeEnd(vOutputStream);

            doInvokeAfter(conn);

            return getResponseStream(conn);
        } catch (IOException e) {
            throw new APPErrorException("获取返回信息出错", e);
        } finally {
            closeConnection(conn);
        }
    }

    protected void initFileParam() {
        setMethod("POST");
        String vBoundary = buildBoundary();
        setBoundary(vBoundary);
        setContentType("multipart/form-data; boundary=" + vBoundary);
    }

    protected void writeFile(OutputStream pOutputStream, List<FileParam> pFileList) throws APPErrorException {
        try {
            for (FileParam vFileParam : pFileList) {

                String vBoundary = "--" + getBoundary();
                String vContentDisposition = String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", vFileParam.getName(), vFileParam.getFileName());
                String vContentType = String.format("Content-Type: %s", StringUtil.isNullOrEmpty(vFileParam.getContentType()) ? MIME_STREAM : vFileParam.getContentType());


                String vContent = "";
                vContent += vBoundary;
                vContent += getNewLine();
                vContent += vContentDisposition;
                vContent += getNewLine() + getNewLine();
                ;
                vContent += vContentType;
                vContent += getNewLine();


                pOutputStream.write(vContent.getBytes(getEncoding()));

                InputStream vInputStream = vFileParam.getFileData();
                byte[] vData = new byte[chunkLen];
                int length = -1;
                while ((length = vInputStream.read(vData)) > 0) {
                    pOutputStream.write(vData, 0, length);
                }
                pOutputStream.write(getNewLine().getBytes(getEncoding()));
            }
        } catch (Exception e) {
            throw new APPErrorException("写入参数信息出错！" + e, e);
        }
    }

    protected void writeParam(OutputStream pOutputStream, Map<String, String> pParam) throws APPErrorException {
        try {
            for (Map.Entry<String, String> vItem : pParam.entrySet()) {


                String vBoundary = "--" + getBoundary();
                String vContentDisposition = "Content-Disposition: form-data; name=\"" + vItem.getKey() + "\"";

                String vContent = "";
                vContent += vBoundary;
                vContent += getNewLine();
                vContent += vContentDisposition;
                vContent += getNewLine() + getNewLine();
                vContent += vItem.getValue();
                vContent += getNewLine();

                pOutputStream.write(vContent.getBytes(getEncoding()));
            }

        } catch (Exception e) {
            throw new APPErrorException("写入参数信息出错！" + e, e);
        }
    }

    protected void writeEnd(OutputStream pOutputStream) throws APPErrorException {
        try {

            String vEndString = "--" + getBoundary() + "--" + getNewLine();
            pOutputStream.write(vEndString.getBytes(getEncoding()));
            pOutputStream.close();

        } catch (Exception e) {
            throw new APPErrorException("写入结束信息出错！" + e, e);
        }
    }

}

