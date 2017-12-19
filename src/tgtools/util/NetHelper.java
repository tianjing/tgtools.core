package tgtools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetHelper {


    /**
     * 获取当前IP
     *
     * @return
     *
     * @author tian.jing
     * @date 2015年12月30日
     */
    public static String getIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return StringUtil.EMPTY_STRING;
        }

    }

    /**
     * 获取当前HostName
     *
     * @return
     *
     * @author tian.jing
     * @date 2015年12月30日
     */
    public static String getHostName() {

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return StringUtil.EMPTY_STRING;
        }
    }

    /**
     * 获取当前电脑的所有IP
     *
     * @return
     *
     * @author tian.jing
     * @date 2015年12月30日
     */
    public static String[] getAllLocalHostIP() {
        String[] ret = null;
        try {
            String hostName = getHostName();
            if (hostName.length() > 0) {
                InetAddress[] addrs = InetAddress.getAllByName(hostName);
                if (addrs.length > 0) {
                    ret = new String[addrs.length];
                    for (int i = 0; i < addrs.length; i++) {
                        ret[i] = addrs[i].getHostAddress();
                    }
                }
            }

        } catch (Exception ex) {
            ret = new String[0];
        }
        return ret;
    }




    private static final String[] windowsCommand = {"ipconfig", "/all"};
    private static final String[] linuxCommand = {"/sbin/ifconfig", "-a"};
    private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*",
            Pattern.CASE_INSENSITIVE);
    /**
     * 获取多个网卡MAC地址
     *
     * @return
     *
     * @throws IOException
     */
    public final static List<String> getMacAddressList() throws IOException {
        final ArrayList<String> macAddressList = new ArrayList<String>();
        final String os = System.getProperty("os.name");
        final String command[];

        if (os.startsWith("Windows")) {
            command = windowsCommand;
        } else if (os.startsWith("Linux")) {
            command = linuxCommand;
        } else {
            throw new IOException("Unknow operating system:" + os);
        }
        // 执行命令
        final Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        for (String line = null; (line = bufReader.readLine()) != null; ) {
            Matcher matcher = macPattern.matcher(line);
            if (matcher.matches()) {
                macAddressList.add(matcher.group(1));
                // macAddressList.add(matcher.group(1).replaceAll("[-:]",
                // ""));//去掉MAC中的“-”
            }
        }

        process.destroy();
        bufReader.close();
        return macAddressList;
    }


    private static String mAllMacAddressStr = null;
    /**
     * 获取个网卡MAC地址（多个网卡时从中获取一个）
     *
     * @return
     */
    public static String getAllMacAddress() {
        if (StringUtil.isNullOrEmpty(mAllMacAddressStr)) {
            StringBuffer sb = new StringBuffer(); // 存放多个网卡地址用，目前只取一个非0000000000E0隧道的值
            try {
                List<String> macList = getMacAddressList();
                for (Iterator<String> iter = macList.iterator(); iter.hasNext(); ) {
                    String amac = iter.next();
                    if (!amac.equals("0000000000E0")) {
                        sb.append(amac);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mAllMacAddressStr = sb.toString();

        }

        return mAllMacAddressStr;
    }
}
