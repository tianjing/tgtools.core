package tgtools.net.file;

import java.io.InputStream;

/**
 * @author 田径
 * @date 2021-08-13 22:21
 * @desc
 **/
public class FileParam {

    private String name;
    private String fileName;
    private String contentType;
    private InputStream fileData;

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String pContentType) {
        contentType = pContentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String pFileName) {
        fileName = pFileName;
    }

    public InputStream getFileData() {
        return fileData;
    }

    public void setFileData(InputStream pFileData) {
        fileData = pFileData;
    }

}
