package tgtools.net.file;

import org.junit.Test;
import tgtools.exceptions.APPErrorException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FileWebClientTest {

    @Test
    public void doInvokeFile() throws FileNotFoundException {
        //        System.setProperty("http.proxySet", "true");
//        System.setProperty("http.proxyHost", "127.0.0.1");
//        System.setProperty("http.proxyPort", "8888");


        FileWebClient vFileWebClient = new FileWebClient();
        vFileWebClient.setUrl("http://192.168.88.1:1340/core/file/save");

        FileParam vFileParam1 = new FileParam();
        //vFileParam1.setInputStream(new FileInputStream("D:\\Disk\\iso\\测试sql.zip"));
        vFileParam1.setFileData(new FileInputStream("D:\\Disk\\iso\\windows_2008_7_sp1.iso"));

        vFileParam1.setFileName("测试sql.zip");
        vFileParam1.setName("file1");


        List<FileParam> vFileList = new ArrayList<>();
        vFileList.add(vFileParam1);

        Map<String, String> vF = new HashMap<>();
        vF.put("tableName", "fdaadfdas");
        vF.put("attr", "fdgsfdgfsdgfgsdfsfg");
        vF.put("objId", "222222222222222222222222");

        try {
            vFileWebClient.doInvokeFile(vFileList, vF);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }
}