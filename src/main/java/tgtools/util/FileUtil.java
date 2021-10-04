package tgtools.util;

import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;

import java.io.*;
import java.util.ArrayList;
/**
 * @author tianjing
 */
public final class FileUtil {
    /**
     * 根据目录和文件扩展名找到所有符合条件的文件
     *
     * @param pDirName
     * @param pExtName
     * @return
     */
    public static String[] listFiles(String pDirName, String[] pExtName) {
        ArrayList<String> fileNames = new ArrayList<String>();
        File dir = new File(pDirName);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String[] s = file.getCanonicalPath().split("\\.");
                        if (s.length > 0) {
                            if (file.isFile()) {
                                String fileExtName = s[(s.length - 1)];
                                if (null == pExtName || pExtName.length == 0) {
                                    fileNames.add(file.getCanonicalPath());
                                    continue;
                                }
                                for (String extName : pExtName) {
                                    if (fileExtName.equalsIgnoreCase(extName)) {
                                        fileNames.add(file.getCanonicalPath());
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new APPRuntimeException("读取目录文件发生异常。", e);
                    }
                }
            }
        }
        return (String[]) fileNames.toArray(new String[fileNames.size()]);
    }

    /**
     * 根据目录和文件扩展名找到所有符合条件的文件
     *
     * @param pDirName
     * @return
     */
    public static File[] listAll(String pDirName) {
        File dir = new File(pDirName);
        if (dir.exists()) {
            return dir.listFiles();
        }
        return new File[0];
    }

    /**
     * 删除目录下所有文件（如果dir是一个文件则删除，目录则是删除目录下所有文件）
     *
     * @param pDir
     * @return
     */
    public static boolean deleteDir(File pDir) {
        if (pDir.isFile()) {
            return pDir.delete();
        }
        String[] fileNames = pDir.list();
        for (String fileName : fileNames) {
            File file = null;
            try {
                file = new File(pDir.getCanonicalPath() + File.separator
                        + fileName);
            } catch (IOException e) {
                return false;
            }
            if (file.isFile()) {
                if (!file.delete()) {
                    return false;
                }
            } else if (!deleteDir(file)) {
                return false;
            }
        }
        return pDir.delete();
    }

    /**
     * 删除目录 文件
     *
     * @param pDir
     * @param pForce true为删除文件 ；fasle为删除文件和目录
     * @return
     */
    public static boolean deleteDir(File pDir, boolean pForce) {
        if (pForce) {
            return deleteDir(pDir);
        }
        if (pDir.isFile()) {
            return pDir.delete();
        }
        String[] fileNames = pDir.list();
        if (fileNames == null) {
            return true;
        }
        for (String fileName : fileNames) {
            File file = null;
            try {
                file = new File(pDir.getCanonicalPath() + File.separator
                        + fileName);
            } catch (IOException e) {
                return false;
            }
            if (file.isFile()) {
                if (!file.delete()) {
                    return false;
                }
            } else if (!deleteDir(file, false)) {
                return false;
            }
        }
        return pDir.delete();
    }

    /**
     * 读取一个文件返回所有字符串
     *
     * @param fileName
     * @return
     */
    public static String readFile(String fileName) {
        File f = new File(fileName);
        StringBuffer buf = new StringBuffer();
        try {
            BufferedReader fr = new BufferedReader(new FileReader(f));
            while (true) {
                String line = fr.readLine();
                if (line == null) {
                    break;
                }
                buf.append(line).append("\t\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 读取一个文件返回所有字符串
     *
     * @param pFileName    文件全路径
     * @param pCharsetName 编码
     * @return
     */
    public static String readFile(String pFileName, String pCharsetName) {
        File f = new File(pFileName);
        StringBuffer buf = new StringBuffer();
        BufferedReader fr = null;
        try {
            fr = new BufferedReader(new InputStreamReader(new FileInputStream(f), pCharsetName));
            while (true) {
                String line = fr.readLine();
                if (line == null) {
                    break;
                }
                buf.append(line).append(StringUtil.NEW_LINE);
            }
        } catch (Exception e) {
            LogHelper.error("", "获取文件内容失败；原因：" + e.toString(), "readFile", e);
        } finally {
            if (null != fr) {
                try {
                    fr.close();
                } catch (IOException e) {
                    LogHelper.error("", "close 失败；原因：" + e.toString(), "readFile", e);
                }
            }
        }
        return buf.toString();
    }

    /**
     * 获取文件名（不含扩展名）
     *
     * @param pFileName
     * @return
     */
    public static String getNoExtensionName(String pFileName) {
        int index = 0;
        if (!StringUtil.isNullOrEmpty(pFileName) && (index = pFileName.lastIndexOf(".")) >= 0) {
            return pFileName.substring(0, index);
        }
        return pFileName;


    }

    /**
     * 如果 pFileName 有扩展名则返回扩展名 否则 空
     *
     * @param pFileName
     * @return
     */
    public static String getExtensionName(String pFileName) {
        int index = 0;
        if (!StringUtil.isNullOrEmpty(pFileName) && (index = pFileName.lastIndexOf(".")) >= 0) {
            return pFileName.substring(index + 1);
        }
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 将文本写入到文件  字符集UTF-8
     *
     * @param pFileName 文件
     * @param pData     内容
     * @throws APPErrorException
     */
    public static void writeFile(String pFileName, String pData) throws APPErrorException {
        writeFile(pFileName, pData, "UTF-8");
    }

    /**
     * 将流写入到文件 (写入完成后关闭InputStream)
     *
     * @param pFileName 文件
     * @param pData     内容
     * @throws APPErrorException
     */
    public static void writeFile(String pFileName, InputStream pData) throws APPErrorException {
        FileOutputStream fop = null;
        try {
            File file = new File(pFileName);
            fop = new FileOutputStream(file);
            byte[] data = new byte[10 * 1024];
            int length = 0;
            while ((length = pData.read(data)) > 0) {
                fop.write(data, 0, length);
            }
        } catch (Exception e) {
            throw new APPErrorException("文件写入失败:" + pFileName, e);
        } finally {
            try {
                fop.close();
            } catch (IOException e) {
            }
            try {
                pData.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 将文本写入到文件 默认：字符集UTF-8
     *
     * @param pFileName 文件及全路径
     * @param pData     内容
     * @param pCharset  字符集UTF-8
     * @throws APPErrorException
     */
    public static void writeFile(String pFileName, String pData, String pCharset) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pCharset)) {
            pCharset = "UTF-8";
        }
        OutputStreamWriter write = null;
        try {
            File file = new File(pFileName);
            FileOutputStream fop = new FileOutputStream(file);
            write = new OutputStreamWriter(fop, pCharset);
            write.write(pData);
        } catch (Exception e) {
            throw new APPErrorException("文件写入失败:" + pFileName, e);
        } finally {
            if (null != write) {
                try {
                    write.flush();
                } catch (IOException e) {
                    LogHelper.error("", "flush 失败；原因：" + e.toString(), "writeFile", e);
                }
                try {
                    write.close();
                } catch (IOException e) {
                    LogHelper.error("", "close 失败；原因：" + e.toString(), "writeFile", e);

                }
            }
        }
    }

    /**
     * 写文件
     *
     * @param pFileName 文件全称 如：/home/tianjing/1.txt
     * @param pData     文件内容
     * @throws APPErrorException
     */
    public static void writeFile(String pFileName, byte[] pData) throws APPErrorException {
        FileOutputStream fop = null;
        try {
            File file = new File(pFileName);
            fop = new FileOutputStream(file);
            fop.write(pData);
        } catch (Exception e) {
            throw new APPErrorException("文件写入失败:" + pFileName, e);
        } finally {
            if (null != fop) {
                try {
                    fop.flush();
                } catch (IOException e) {
                    LogHelper.error("", "flush 失败；原因：" + e.toString(), "writeFile", e);
                }
                try {
                    fop.close();
                } catch (IOException e) {
                    LogHelper.error("", "close 失败；原因：" + e.toString(), "writeFile", e);
                }
            }
        }

    }

    /**
     * 读文件
     *
     * @param pFileName 文件全称 如：/home/tianjing/1.txt
     * @throws APPErrorException
     */
    public static byte[] readFileToByte(String pFileName) throws APPErrorException {
        File f = new File(pFileName);
        if (!f.exists()) {
            throw new APPErrorException("文件未找到：" + pFileName);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new APPErrorException("文件读取失败:" + pFileName, e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (Exception ee) {
            }
        }

    }

    /**
     * 获取文件字符集
     *
     * @param path 文件路径
     * @return
     */
    public static String getFileEncode(String path) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(path));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }
            if (first3Bytes[0] == StringUtil.UTF16LEBom[0] && first3Bytes[1] == StringUtil.UTF16LEBom[1]) {
                // UTF-16LE
                charset = "Unicode";
                checked = true;
            } else if (first3Bytes[0] == StringUtil.UTF16BEBom[0] && first3Bytes[1] == StringUtil.UTF16BEBom[1]) {
                // UTF-16BE
                charset = "Unicode";
                checked = true;
            } else if (first3Bytes[0] == StringUtil.UTF8Bom[0] && first3Bytes[1] == StringUtil.UTF8Bom[1]
                    && first3Bytes[2] == StringUtil.UTF8Bom[2]) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int len = 0;
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) {
                        break;
                    }
                    // 单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            // 双字节 (0xC0 - 0xDF) (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        } else {
                            break;
                        }
                    }
                    // 也有可能出错，但是几率较小
                    else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                }
            }
        }
        return charset;
    }

    public static void main(String[] args) {
        String ss = FileUtil.readFile("C:\\tianjing\\Desktop\\JS.变电站接线图目录.fac.pic.g");
        System.out.println(ss);
    }
}