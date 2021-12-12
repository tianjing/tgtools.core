package tgtools.net;

import org.junit.Test;
import tgtools.exceptions.APPErrorException;

import java.io.File;
import java.io.InputStream;

public class WebClientTest {

    @Test
    public void main() throws APPErrorException {
        WebClient vClient = new WebClient();
        String vResult = null;

        String url1 = "http://sms.js.sgcc.com.cn:17011/jssms/02grdxzx/01xxx.jsp";
        String url1_1 = "http://ids.js.sgcc.com.cn:8080/nidp/jsp/content.jsp?sid=0&id=10&sid=0";
        String url1_2 = "http://ids.js.sgcc.com.cn:8080/nidp/idff/sso?id=10&sid=0&option=credential&sid=0";

        String url2 = "http://ids.js.sgcc.com.cn:8080/nidp/idff/sso?sid=0";
        String url3 = "http://ids.js.sgcc.com.cn:8080/nidp/idff/sso?sid=0";


        vClient.setMethod("GET");
        vClient.setUrl(url1);
        InputStream stream = vClient.doInvokeAsStream("");


        System.out.println(vClient.parseString(stream, "utf-8"));

//
//        vClient.setUrl(url1_1);
//        vClient.doInvokeAsString("");
//
//        vClient.setUrl(url1_2);
//        vClient.doInvokeAsString("");
//
//
//        //登录
//        vClient.setUrl(url2);
//        vClient.setContentType("application/x-www-form-urlencoded");
//        vClient.setMethod("POST");
//        vClient.doInvokeAsString("option=credential&Ecom_User_ID=sddxfs&Ecom_Password=jsepc0123%21");
//
//        vClient.setMethod("GET");
//        vClient.setUrl(url3);
//        String vRes =vClient.doInvokeAsString("");
//        System.out.println(vRes);
//
//        vClient.setMethod("GET");
//        vClient.setUrl(url3);
//        vClient.doInvokeAsString("");
    }

    @Test
    public void testDownloadFile() throws APPErrorException {
        WebClient vClient = new WebClient();
        vClient.setUrl("https://file.cdn.xiangtatech.com/file/TSBrowser_840_4.0.8.14.exe");
        vClient.setMethod("GET");
        for(int i=0;i<30;i++) {
            File vFile = vClient.doInvokeAsFile("");
            System.out.println(vFile);
        }
    }
}