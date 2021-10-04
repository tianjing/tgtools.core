package tgtools.util;

import org.junit.Test;
import tgtools.exceptions.APPErrorException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class ZipCompressTest {

    @Test
    public void main1() {
        String src = "f:\\中文包";// 指定压缩源，可以是目录或文件
        String src1="F:\\中文包\\Hallo.png";
        String src2="F:\\中文包\\说明.docx";
        String decompressDir = "f:\\depress";// 解压路径
        String archive = "f:\\中文压缩文件.zip";// 压缩包路径
        String comment = "Java Zip 测试.";// 压缩包注释
        // // ----压缩文件或目录
        // writeByApacheZipOutputStream(src, archive, comment);
        // /*
        // * 读压缩文件，注释掉，因为使用的是apache的压缩类，所以使用java类库中 解压类时出错，这里不能运行
        // */
        // // readByZipInputStream(archive, decompressDir);
        // // ----使用apace ZipFile读取压缩文件
        // readByApacheZipFile(archive, decompressDir);


    }

    @Test
    public void main() {
        String input="C:\\Works\\DQ\\javademos\\FTPDownLoadService\\file";
        String output="C:\\Works\\DQ\\javademos\\FTPDownLoadService\\file/back/111.zip";
        try {
            FileOutputStream out=new FileOutputStream(output);
            ZipCompress.writeByApacheZipOutputStream(input,out,"",false);
        } catch (APPErrorException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}