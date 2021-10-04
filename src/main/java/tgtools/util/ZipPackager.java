package tgtools.util;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;

/**
 * 名  称：zip 打包类
 * @author tianjing
 * 功  能：对多个文件进行打包
 * 例  子1：
 * ZipPackager zip = new ZipPackager("GBK");
 * FileOutputStream file = new FileOutputStream("C:\\tianjing\\Desktop\\myzip.zip");
 * zip.init(file);
 * zip.addFile(new File("C:\\tianjing\\Desktop\\DBPlugin文档-设备表接口.docx"));
 * zip.addFile(new File("C:\\tianjing\\Desktop\\工作联系单.docx"));
 * zip.Dispose();
 * <p>
 * 例  子2：
 * ZipPackager zip = new ZipPackager();
 * ByteArrayOutputStream file =new ByteArrayOutputStream();
 * zip.init(file);
 * zip.addFile(new File("C:\\tianjing\\Desktop\\DBPlugin文档-设备表接口.docx"));
 * zip.addFile(new File("C:\\tianjing\\Desktop\\工作联系单.docx"));
 * zip.Dispose();
 * byte[] dd=((ByteArrayOutputStream)file).toByteArray();
 * <p>
 * 时  间：10:39
 */
public class ZipPackager implements IDispose {
    ZipOutputStream zipOutputStream = null;
    private String encode;
    private int zipLevel;

    /**
     * 构造
     */
    public ZipPackager() {
        this("UTF-8", Deflater.NO_COMPRESSION);
    }

    /**
     * 构造
     *
     * @param pEncode 文件名编码
     */
    public ZipPackager(String pEncode) {
        this(pEncode, Deflater.NO_COMPRESSION);
    }

    /**
     * 构造
     *
     * @param pEncode 文件名编码
     * @param pLevel  压缩等级 参考java.util.zip.Deflater
     */
    public ZipPackager(String pEncode, int pLevel) {
        encode = pEncode;
        zipLevel = pLevel;
    }

    /**
     * 初始化 设置输出流
     *
     * @param pOutputStream
     */
    public void init(OutputStream pOutputStream) {
        if (null != pOutputStream) {
            zipOutputStream = new ZipOutputStream(new CheckedOutputStream(pOutputStream,
                    new CRC32()));
        } else {
            zipOutputStream = new ZipOutputStream(new CheckedOutputStream(new ByteArrayOutputStream(), new CRC32()));
        }
        // 支持中文
        zipOutputStream.setEncoding(encode);
        // 启用压缩
        zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
        // 压缩级别为最强压缩，但时间要花得多一点
        zipOutputStream.setLevel(zipLevel);
    }

    /**
     * 添加文件
     *
     * @param pFile 文件对象
     * @throws APPErrorException
     */
    public void addFile(File pFile) throws APPErrorException {
        if (null == pFile) {
            throw new APPErrorException("文件不能为NULL");
        }
        if (!pFile.exists() || !pFile.isFile()) {
            throw new APPErrorException("文件不存在或不是文件。file：" + pFile.getAbsolutePath());
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(pFile);
        } catch (Exception ex) {
            throw new APPErrorException("初始化文件流错误。file：" + pFile.getAbsolutePath(), ex);
        }
        if (null != in) {
            addFile(in, pFile.getName());
        }

    }

    /**
     * 添加文件
     *
     * @param pInputStream 输入流
     * @param pFileName    文件名
     * @throws APPErrorException
     */
    public void addFile(InputStream pInputStream, String pFileName) throws APPErrorException {
        addZipEntry(pInputStream, pFileName);
    }

    private void addZipEntry(InputStream pInputStream, String pEntryName) throws APPErrorException {
        try {
            BufferedInputStream bi = new BufferedInputStream(pInputStream);

            // 开始写入新的ZIP文件条目并将流定位到条目数据的开始处
            ZipEntry zipEntry = new ZipEntry(pEntryName);
            zipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int readCount = bi.read(buffer);

            while (readCount != -1) {
                zipOutputStream.write(buffer, 0, readCount);
                readCount = bi.read(buffer);
            }
            // 注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
            // 然可能有的内容就会存入到后面条目中去了
            zipOutputStream.flush();
        } catch (Exception ex) {
            throw new APPErrorException("压缩文件出错。文件名称：" + pEntryName + ";原因:" + ex.getMessage(), ex);
        } finally {
            try {
                pInputStream.close();
            } catch (IOException e) {
                LogHelper.error("", "输入流关闭错误", "ZipPackager.addFile", e);
            }
        }
    }

    /**
     * @param pEntryName
     * @throws APPErrorException
     */
    private void addZipEntry(String pEntryName) throws APPErrorException {
        ZipEntry zipEntry = new ZipEntry(pEntryName);
        try {
            zipOutputStream.putNextEntry(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addZipEntry(File pFile, String pEntryName) throws APPErrorException {
        if (pFile.isDirectory()) {
            String entryName=pEntryName+pFile.getName()+"/";
            addZipEntry(entryName);
            File [] files=pFile.listFiles();
            for(int i=0;i<files.length;i++)
            {
                addZipEntry(files[i],entryName);
            }
        }
        else
        {
            try {
                addZipEntry(new FileInputStream(pFile),pEntryName+pFile.getName());
            } catch (FileNotFoundException e) {
                throw new APPErrorException("初始化文件流错误。file：" + pFile.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 添加文件
     *
     * @param pData     文件数据
     * @param pFileName 文件名
     * @throws APPErrorException
     */
    public void addFile(byte[] pData, String pFileName) throws APPErrorException {
        addFile(new ByteArrayInputStream(pData), pFileName);
    }

    /**
     * 添加目录下所有文件
     * @param pFile
     * @throws APPErrorException
     */
    public void addDir(File pFile) throws APPErrorException {
        if (null == pFile) {
            throw new APPErrorException("目录不能为NULL");
        }
        if (!pFile.exists() || !pFile.isDirectory()) {
            throw new APPErrorException("目录不存在或不是目录。dir：" + pFile.getAbsolutePath());
        }
        addZipEntry(pFile, "");
    }


    /**
     * 释放
     */
    @Override
    public void Dispose() {
        try {
            zipOutputStream.flush();
        } catch (IOException e) {
            LogHelper.error("", "flush压缩流错误", "ZipPackager.Dispose", e);
        }
        try {
            zipOutputStream.close();
        } catch (IOException e) {
            LogHelper.error("", "close压缩流错误", "ZipPackager.Dispose", e);
        }
        zipOutputStream = null;
    }


    public static void main(String[] args) {
        OutputStream file = null;
        try {
            ZipPackager zip = new ZipPackager("GBK");
            file = new FileOutputStream("C:\\tianjing\\Desktop\\myzip11.zip");
            //file = new ByteArrayOutputStream();
            zip.init(file);
            zip.addDir(new File("C:\\tianjing\\Desktop\\PMIS"));
            zip.Dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (null != file) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
