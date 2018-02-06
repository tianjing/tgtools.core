package tgtools.util;

import java.io.OutputStream;
import java.io.Writer;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
/**
 * 实体类 与 xml  序列化与反序列化
 * 使用 工具 simple-xml
 * 实体类 使用注释写法 注释 引用 org.simpleframework.xml.Element
 * @author tian.jing
 * @date 2016年3月11日
 */
public class XmlSerialize {
	public XmlSerialize() {
	}

	/**
	 *序列化 将对象序列化为 xml
	 * @param writer 将xml内容保存到writer中
	 * @param obj 需要序列化的实体
	 * @throws Exception
     */
	public static void serialize(Writer writer, Object obj) throws Exception // 序列化单个java对象
	{
		Serializer serializer = new Persister();
		serializer.write(obj,writer);
	}
/**
 * 序列化 将对象序列化为 xml
 * @author tian.jing
 * @date 2016年3月11日
 * @param os 将xml内容保存到 os中
 * @param obj 需要序列化的实体
 * @throws Exception
 */
	public static void serialize(OutputStream os, Object obj) throws Exception // 序列化单个java对象
	{
		Serializer serializer = new Persister();
		serializer.write(obj, os);
	}
	/**
	 * 反序列化 将XML转换为对应实体
	 * @author tian.jing
	 * @date 2016年3月11日
	 * @param xml xml的内容
	 * @param p_class 需要转换的实体类
	 * @return
	 * @throws Exception
	 */
	public static Object deserialize(String xml, String name, Class<?> p_class) throws Exception // 反序列化单个Java对象
	{
		Serializer serializer = new Persister();	
		return serializer.read(p_class, xml);
	}
	/**
	 * 反序列化 将XML转换为对应实体
	 * @author tian.jing
	 * @date 2016年3月11日
	 * @param xml xml的内容
	 * @param p_class 需要转换的实体类
	 * @return
	 * @throws Exception
	 */
	public static <T>T deserialize(String xml,  Class<? extends T> p_class) throws Exception // 反序列化单个Java对象
	{
		Serializer serializer = new Persister();	
		return serializer.read(p_class, xml);
	}

}