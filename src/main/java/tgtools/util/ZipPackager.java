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
 * 编写者：田径
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
    ZipOutputStream m_ZipOutputStream = null;
    private String m_Encode;
    private int m_ZipLevel;

    /**
     * 构造
     */
    public ZipPackager() {
        this("UTF-8", Deflater.NO_COMPRESSION);
    }

    /**
     * 构造
     *
     * @param p_Encode 文件名编码
     */
    public ZipPackager(String p_Encode) {
        this(p_Encode, Deflater.NO_COMPRESSION);
    }

    /**
     * 构造
     *
     * @param p_Encode 文件名编码
     * @param p_Level  压缩等级 参考java.util.zip.Deflater
     */
    public ZipPackager(String p_Encode, int p_Level) {
        m_Encode = p_Encode;
        m_ZipLevel = p_Level;
    }

    /**
     * 初始化 设置输出流
     *
     * @param p_OutputStream
     */
    public void init(OutputStream p_OutputStream) {
        if (null != p_OutputStream) {
            m_ZipOutputStream = new ZipOutputStream(new CheckedOutputStream(p_OutputStream,
                    new CRC32()));
        } else {
            m_ZipOutputStream = new ZipOutputStream(new CheckedOutputStream(new ByteArrayOutputStream(), new CRC32()));
        }
        // 支持中文
        m_ZipOutputStream.setEncoding(m_Encode);
        // 启用压缩
        m_ZipOutputStream.setMethod(ZipOutputStream.DEFLATED);
        // 压缩级别为最强压缩，但时间要花得多一点
        m_ZipOutputStream.setLevel(m_ZipLevel);
    }

    /**
     * 添加文件
     *
     * @param p_File 文件对象
     * @throws APPErrorException
     */
    public void addFile(File p_File) throws APPErrorException {
        if (null == p_File) {
            throw new APPErrorException("文件不能为NULL");
        }
        if (!p_File.exists() || !p_File.isFile()) {
            throw new APPErrorException("文件不存在或不是文件。file：" + p_File.getAbsolutePath());
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(p_File);
        } catch (Exception ex) {
            throw new APPErrorException("初始化文件流错误。file：" + p_File.getAbsolutePath(), ex);
        }
        if (null != in) {
            addFile(in, p_File.getName());
        }

    }

    /**
     * 添加文件
     *
     * @param p_InputStream 输入流
     * @param p_FileName    文件名
     * @throws APPErrorException
     */
    public void addFile(InputStream p_InputStream, String p_FileName) throws APPErrorException {
        addZipEntry(p_InputStream, p_FileName);
    }

    private void addZipEntry(InputStream p_InputStream, String p_EntryName) throws APPErrorException {
        try {
            BufferedInputStream bi = new BufferedInputStream(p_InputStream);

            // 开始写入新的ZIP文件条目并将流定位到条目数据的开始处
            ZipEntry zipEntry = new ZipEntry(p_EntryName);
            m_ZipOutputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[1024];
            int readCount = bi.read(buffer);

            while (readCount != -1) {
                m_ZipOutputStream.write(buffer, 0, readCount);
                readCount = bi.read(buffer);
            }
            // 注，在使用缓冲流写压缩文件时，一个条件完后一定要刷新一把，不
            // 然可能有的内容就会存入到后面条目中去了
            m_ZipOutputStream.flush();
        } catch (Exception ex) {
            throw new APPErrorException("压缩文件出错。文件名称：" + p_EntryName + ";原因:" + ex.getMessage(), ex);
        } finally {
            try {
                p_InputStream.close();
            } catch (IOException e) {
                LogHelper.error("", "输入流关闭错误", "ZipPackager.addFile", e);
            }
        }
    }

    /**
     * @param p_EntryName
     * @throws APPErrorException
     */
    private void addZipEntry(String p_EntryName) throws APPErrorException {
        ZipEntry zipEntry = new ZipEntry(p_EntryName);
        try {
            m_ZipOutputStream.putNextEntry(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addZipEntry(File p_File, String p_EntryName) throws APPErrorException {
        if (p_File.isDirectory()) {
            String entryName=p_EntryName+p_File.getName()+"/";
            addZipEntry(entryName);
            File [] files=p_File.listFiles();
            for(int i=0;i<files.length;i++)
            {
                addZipEntry(files[i],entryName);
            }
        }
        else
        {
            try {
                addZipEntry(new FileInputStream(p_File),p_EntryName+p_File.getName());
            } catch (FileNotFoundException e) {
                throw new APPErrorException("初始化文件流错误。file：" + p_File.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 添加文件
     *
     * @param p_Data     文件数据
     * @param p_FileName 文件名
     * @throws APPErrorException
     */
    public void addFile(byte[] p_Data, String p_FileName) throws APPErrorException {
        addFile(new ByteArrayInputStream(p_Data), p_FileName);
    }

    /**
     * 添加目录下所有文件
     * @param p_File
     * @throws APPErrorException
     */
    public void addDir(File p_File) throws APPErrorException {
        if (null == p_File) {
            throw new APPErrorException("目录不能为NULL");
        }
        if (!p_File.exists() || !p_File.isDirectory()) {
            throw new APPErrorException("目录不存在或不是目录。dir：" + p_File.getAbsolutePath());
        }
        addZipEntry(p_File, "");
    }


    /**
     * 释放
     */
    @Override
    public void Dispose() {
        try {
            m_ZipOutputStream.flush();
        } catch (IOException e) {
            LogHelper.error("", "flush压缩流错误", "ZipPackager.Dispose", e);
        }
        try {
            m_ZipOutputStream.close();
        } catch (IOException e) {
            LogHelper.error("", "close压缩流错误", "ZipPackager.Dispose", e);
        }
        m_ZipOutputStream = null;
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
