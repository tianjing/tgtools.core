package tgtools.util;

import tgtools.log.LogEntity;
import tgtools.log.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
/**
 * @author tianjing
 */
public class LogHelper {
	private static boolean m_IsDebug=true;

	/**
	 * 获取日志是否是调试模式
	 * @return
     */
	public static boolean getIsDegug()
	{
		return m_IsDebug;
	}

	/**
	 * 设置日志是否是调试模式
	 * @param pIsDebug
     */
	public static void setIsDegug(boolean pIsDebug)
	{
		m_IsDebug=pIsDebug;
	}

	/**
	 * 打印信息 （无论是不是调试模式都会打印信息）
	 * @param name 名称 比如用户名
	 * @param message 信息
	 * @param biztype 业务类型
	 */
	public static void infoForce(String name, String message, String biztype)
	{
		LogEntity entity = new LogEntity();
		entity.setUsername(name);
		entity.setBiztype(biztype);
		entity.setLogtype("info");
		entity.setLogcontent(message);
		LoggerFactory.getDefault().info(entity);
	}
	/**
	 * 打印信息 （调试模式下才会打印）
	 * @param name 名称 比如用户名
	 * @param message 信息
	 * @param biztype 业务类型
     */
	public static void info(String name, String message, String biztype) {
		if(!m_IsDebug)
		{
			return ;
		}
		LogEntity entity = new LogEntity();
		entity.setUsername(name);
		entity.setBiztype(biztype);
		entity.setLogtype("info");
		entity.setLogcontent(message);
		LoggerFactory.getDefault().info(entity);
	}

	/**
	 * 警告信息
	 * @param name 名称 比如用户名
	 * @param message 信息
	 * @param biztype 业务类型
     */
	public static void warn(String name, String message, String biztype) {
		LogEntity entity = new LogEntity();
		entity.setUsername(name);
		entity.setBiztype(biztype);
		entity.setLogtype("warn");
		entity.setLogcontent(message);
		LoggerFactory.getDefault().warn(entity);
	}

	/**
	 * 警告信息
	 * @param name 名称 比如用户名
	 * @param message 信息
	 * @param biztype 业务类型
	 * @param pThrowable
     */
	public static void warn(String name, String message, String biztype,Throwable pThrowable) {
		LogEntity entity = new LogEntity();
		entity.setUsername(name);
		entity.setBiztype(biztype);
		entity.setLogtype("warn");
		entity.setLogcontent(message+":"+getFullErrorMessage(pThrowable));
		LoggerFactory.getDefault().warn(entity);
	}

	/**
	 * 错误信息
	 * @param name 名称 比如用户名
	 * @param message 信息
	 * @param biztype 业务类型
	 * @param pThrowable 错误信息
     */
	public static void error(String name, String message, String biztype,Throwable pThrowable) {
		LogEntity entity = new LogEntity();
		entity.setUsername(name);
		entity.setBiztype(biztype);
		entity.setLogtype("error");
		entity.setLogcontent(message+":"+getFullErrorMessage(pThrowable));
		LoggerFactory.getDefault().error(entity,pThrowable);
	}
	
	private static String getFullErrorMessage(Throwable pThrowable)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{ 
		java.io.PrintWriter ss=new PrintWriter(baos);
		pThrowable.printStackTrace(ss);
		ss.flush();
		return baos.toString();
		}finally{
			try{
			baos.close();
			baos=null;
			}
			catch(Exception e){}
		}
	}
}
