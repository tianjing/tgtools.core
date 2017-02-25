package tgtools.util;

import tgtools.exceptions.APPErrorException;
import tgtools.exceptions.APPRuntimeException;

import java.io.*;
import java.util.ArrayList;

public final class FileUtil {
    /**
     * 根据目录和文件扩展名找到所有符合条件的文件
     *
     * @param p_dirName
     * @param p_extName
     * @return
     */
    public static String[] listFiles(String p_dirName, String[] p_extName) {
        ArrayList<String> fileNames = new ArrayList<String>();
        File dir = new File(p_dirName);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String[] s = file.getCanonicalPath().split("\\.");
                        if (s.length > 0) {
                            if (file.isFile()) {
                                String fileExtName = s[(s.length - 1)];
                                if (null == p_extName || p_extName.length == 0) {
                                    fileNames.add(file.getCanonicalPath());
                                    continue;
                                }
                                for (String extName : p_extName)
                                    if (fileExtName.equalsIgnoreCase(extName)) {
                                        fileNames.add(file.getCanonicalPath());
                                        break;
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
     * @param p_dirName
     * @return
     */
    public static File[] listAll(String p_dirName) {
        File dir = new File(p_dirName);
        if (dir.exists()) {
            return dir.listFiles();
        }
        return new File[0];
    }

    /**
     * 删除目录下所有文件（如果dir是一个文件则删除，目录则是删除目录下所有文件）
     *
     * @param p_dir
     * @return
     */
    public static boolean deleteDir(File p_dir) {
        if (p_dir.isFile()) {
            return p_dir.delete();
        }
        String[] fileNames = p_dir.list();
        for (String fileName : fileNames) {
            File file = null;
            try {
                file = new File(p_dir.getCanonicalPath() + File.separator
                        + fileName);
            } catch (IOException e) {
                return false;
            }
            if (file.isFile()) {
                if (!file.delete())
                    return false;
            } else if (!deleteDir(file)) {
                return false;
            }
        }
        return p_dir.delete();
    }

    /**
     * 删除目录 文件
     *
     * @param p_dir
     * @param p_force true为删除文件 ；fasle为删除文件和目录
     * @return
     */
    public static boolean deleteDir(File p_dir, boolean p_force) {
        if (p_force) {
            return deleteDir(p_dir);
        }
        if (p_dir.isFile()) {
            return p_dir.delete();
        }
        String[] fileNames = p_dir.list();
        if (fileNames == null)
            return true;
        for (String fileName : fileNames) {
            File file = null;
            try {
                file = new File(p_dir.getCanonicalPath() + File.separator
                        + fileName);
            } catch (IOException e) {
                return false;
            }
            if (file.isFile()) {
                if (!file.delete())
                    return false;
            } else if (!deleteDir(file, false)) {
                return false;
            }
        }
        return p_dir.delete();
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
     * @param p_FileName    文件全路径
     * @param p_CharsetName 编码
     * @return
     */
    public static String readFile(String p_FileName, String p_CharsetName) {
        File f = new File(p_FileName);
        StringBuffer buf = new StringBuffer();
        try {
            BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(f), p_CharsetName));
            while (true) {
                String line = fr.readLine();
                if (line == null) {
                    break;
                }
                buf.append(line).append(StringUtil.NEW_LINE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 获取文件名（不含扩展名）
     *
     * @param p_FileName
     * @return
     */
    public static String getNoExtensionName(String p_FileName) {
        int index = 0;
        if (!StringUtil.isNullOrEmpty(p_FileName) && (index = p_FileName.lastIndexOf(".")) >= 0) {
            return p_FileName.substring(0, index);
        }
        return p_FileName;


    }

    /**
     * 如果 p_FileName 有扩展名则返回扩展名 否则 空
     *
     * @param p_FileName
     * @return
     */
    public static String getExtensionName(String p_FileName) {
        int index = 0;
        if (!StringUtil.isNullOrEmpty(p_FileName) && (index = p_FileName.lastIndexOf(".")) >= 0) {
            return p_FileName.substring(index + 1);
        }
        return StringUtil.EMPTY_STRING;
    }

    /**
     * 将文本写入到文件  字符集UTF-8
     *
     * @param p_FileName 文件
     * @param p_Data     内容
     * @throws APPErrorException
     */
    public static void writeFile(String p_FileName, String p_Data) throws APPErrorException {
        writeFile(p_FileName, p_Data, "UTF-8");
    }

    /**
     * 将文本写入到文件 默认：字符集UTF-8
     * @param p_FileName 文件及全路径
     * @param p_Data 内容
     * @param p_Charset 字符集UTF-8
     * @throws APPErrorException
     */
    public static void writeFile(String p_FileName, String p_Data, String p_Charset) throws APPErrorException {
        if(StringUtil.isNullOrEmpty(p_Charset))
        {
            p_Charset="UTF-8";
        }
        OutputStreamWriter write = null;
        try {
            File file = new File(p_FileName);
            FileOutputStream fop = new FileOutputStream(file);
            write = new OutputStreamWriter(fop, p_Charset);
            write.write(p_Data);
        } catch (Exception e) {
            throw new APPErrorException("文件写入失败:" + p_FileName, e);
        } finally {
            if (null != write) {
                try {
                    write.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    write.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写文件
     *
     * @param p_FileName 文件全称 如：/home/tianjing/1.txt
     * @param p_Data     文件内容
     * @throws APPErrorException
     */
    public static void writeFile(String p_FileName, byte[] p_Data) throws APPErrorException {
        FileOutputStream fop = null;
        try {
            File file = new File(p_FileName);
            fop = new FileOutputStream(file);
            fop.write(p_Data);
        } catch (Exception e) {
            throw new APPErrorException("文件写入失败:" + p_FileName, e);
        } finally {
            if (null != fop) {
                try {
                    fop.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fop.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 读文件
     *
     * @param p_FileName 文件全称 如：/home/tianjing/1.txt
     * @throws APPErrorException
     */
    public static byte[] readFileToByte(String p_FileName) throws APPErrorException {
        File f = new File(p_FileName);
        if (!f.exists()) {
            throw new APPErrorException("文件未找到：" + p_FileName);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new APPErrorException("文件读取失败:" + p_FileName, e);
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
            if (read == -1)
                return charset;
            if (first3Bytes[0] ==StringUtil.UTF16LEBom[0] && first3Bytes[1] == StringUtil.UTF16LEBom[1]) {
                charset = "Unicode";// UTF-16LE
                checked = true;
            } else if (first3Bytes[0] == StringUtil.UTF16BEBom[0] && first3Bytes[1] == StringUtil.UTF16BEBom[1]) {
                charset = "Unicode";// UTF-16BE
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
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            // 双字节 (0xC0 - 0xDF) (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
                // TextLogger.getLogger().info(loc + " " +
                // Integer.toHexString(read));
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