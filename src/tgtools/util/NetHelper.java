package tgtools.util;

import java.net.InetAddress;

public class NetHelper {

	/**
	 * 获取当前IP
	 * @author tian.jing
	 * @date 2015年12月30日
	 * @return
	 */
	public static String getIP()
	{
		try {
			return  InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return StringUtil.EMPTY_STRING;
		}
		
	}
	/**
	 * 获取当前HostName
	 * @author tian.jing
	 * @date 2015年12月30日
	 * @return
	 */
	public static String getHostName()
	{

		try {
			return  InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return StringUtil.EMPTY_STRING;
		}
	}
	/**
	 * 获取当前电脑的所有IP
	 * @author tian.jing
	 * @date 2015年12月30日
	 * @return
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
	
}
