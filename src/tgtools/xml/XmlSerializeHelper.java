package tgtools.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
//import org.apache.commons.codec.binary.Base64;

import tgtools.util.StringUtil;
@SuppressWarnings("unchecked")
public class XmlSerializeHelper {
	private static XMLInputFactory inputFactory;
	  private static XMLOutputFactory outFactory;

	  public static String getSerializableClassElementName(Class<?> p_class)
	  {
	    String elementName = p_class.getSimpleName();
	    XmlElement elementAnnotation = (XmlElement)p_class.getAnnotation(XmlElement.class);
	    if (elementAnnotation != null) {
	      elementName = elementAnnotation.name();
	    }
	    XmlArray arrayAnnotation = (XmlArray)p_class.getAnnotation(XmlArray.class);
	    if (arrayAnnotation != null) {
	      elementName = arrayAnnotation.name();
	    }
	    return elementName;
	  }

	  public static Field[] getFields(Class p_class)
	  {
	    Class cls = p_class;
	    LinkedList fields = new LinkedList();
	    while (cls != null) {
	      Field[] f = cls.getDeclaredFields();
	      for (int i = 0; i < f.length; i++) {
	        int mod = f[i].getModifiers();
	        if ((!Modifier.isFinal(mod)) && (!Modifier.isTransient(mod)) && (!Modifier.isStatic(mod)))
	        {
	          fields.add(f[i]);
	        }
	      }
	      cls = cls.getSuperclass();
	    }
	    return (Field[])fields.toArray(new Field[fields.size()]);
	  }

	  public static Field getField(Class p_class, String p_nodeName, boolean p_ignoreCase)
	  {
	    for (Field field : getFields(p_class))
	      if (!field.isAnnotationPresent(XmlIgnore.class))
	      {
	        if (StringUtil.equal(field.getName(), p_nodeName, p_ignoreCase)) {
	          return field;
	        }
	        XmlElement elementAnnotation = (XmlElement)field.getAnnotation(XmlElement.class);

	        if ((elementAnnotation != null) && (StringUtil.equal(elementAnnotation.name(), p_nodeName, p_ignoreCase)))
	        {
	          return field;
	        }
	        XmlAttribute attributeAnnotation = (XmlAttribute)field.getAnnotation(XmlAttribute.class);

	        if ((attributeAnnotation != null) && (StringUtil.equal(attributeAnnotation.name(), p_nodeName, p_ignoreCase)))
	        {
	          return field;
	        }
	        XmlArray arrayAnnotation = (XmlArray)field.getAnnotation(XmlArray.class);
	        if ((arrayAnnotation != null) && (StringUtil.equal(arrayAnnotation.name(), p_nodeName, p_ignoreCase)))
	        {
	          return field;
	        }
	      }
	    return null;
	  }

	  public static boolean isInterface(Class p_class, Class p_interface)
	  {
	    Class[] classes = p_class.getInterfaces();
	    for (Class aClass : classes) {
	      if (aClass.equals(p_interface))
	        return true;
	      if (isInterface(aClass, p_interface))
	        return true;
	    }
	    Class superClass = p_class.getSuperclass();
	    if (superClass != null)
	      return isInterface(superClass, p_interface);
	    return false;
	  }

	  public static boolean isGenericCollectionType(Object p_object)
	  {
	    return isGenericCollectionType(p_object.getClass());
	  }

	  public static boolean isGenericCollectionType(Class p_class)
	  {
	    Type gtype = p_class.getGenericSuperclass();
	    if ((gtype instanceof ParameterizedType)) {
	      ParameterizedType ptype = (ParameterizedType)gtype;
	      Type rtype = ptype.getRawType();
	      if (isInterface((Class)rtype, Collection.class))
	        return true;
	    }
	    return false;
	  }

	  public static boolean isSimpleClass(Class p_class)
	  {
	    return (p_class.isPrimitive()) || (p_class.equals(Boolean.class)) || (p_class.equals(Byte.class)) || (p_class.equals(Double.class)) || (p_class.equals(Float.class)) || (p_class.equals(Integer.class)) || (p_class.equals(Long.class)) || (p_class.equals(Short.class)) || (p_class.equals(String.class)) || (java.util.Date.class.isAssignableFrom(p_class));
	  }

	  public static XMLInputFactory createXMLInputFactory()
	  {
	    if (inputFactory == null)
	    {
	        inputFactory = XMLInputFactory.newInstance();
	    }

	    return inputFactory;
	  }

	  public static XMLOutputFactory createXmlOutputFactory()
	  {
	    if (outFactory == null)
	    {
	        outFactory = XMLOutputFactory.newInstance();
	    }

	    return outFactory;
	  }

	  public static String readText(XMLStreamReader p_reader)
	  {
	    StringBuilder charactersBuilder = new StringBuilder("");

	    while ((p_reader.isCharacters()) || (p_reader.getEventType() == 4)) {
	      charactersBuilder.append(p_reader.getText());
	      try {
	        p_reader.next();
	      } catch (XMLStreamException e) {
	        throw new XmlSerializeException("从 XMLStreamReader 中读取Xml节点的文本内容时发生异常。", e);
	      }
	    }

	    return charactersBuilder.toString();
	  }

	  public static void writeText(XMLStreamWriter p_writer, String p_text)
	  {
	    try
	    {
	      if (p_text.contains("]]>"))
	        p_writer.writeCharacters(p_text);
	      else
	        p_writer.writeCData(p_text);
	    } catch (XMLStreamException e) {
	      throw new XmlSerializeException("向 XMLStreamWriter 中写入文本内 容时发生异常。", e);
	    }
	  }

	  public static void readToStartElement(XMLStreamReader p_reader)
	  {
	    while (p_reader.getEventType() != 1)
	      try {
	        p_reader.next();
	      } catch (XMLStreamException e) {
	        throw new XmlSerializeException("从 XMLStreamReader 中读取Xml StartElement时发生异常。", e);
	      }
	  }

	  public static Class getArrayItemClass(XmlArrayItem p_xmlArrayItem, String p_elementName)
	  {
	    if (p_xmlArrayItem != null) {
	      String[] classNames = p_xmlArrayItem.classNames();
	      for (String className : classNames) {
	        try {
	          Class c = Thread.currentThread().getContextClassLoader().loadClass(className);

	          if (c.isAnnotationPresent(XmlElement.class)) {
	            if (((XmlElement)c.getAnnotation(XmlElement.class)).name().equals(p_elementName))
	            {
	              return c;
	            }
	          } else if (c.getSimpleName().equals(p_elementName))
	            return c;
	        }
	        catch (ClassNotFoundException e) {
	          throw new XmlSerializeException(String.format("Xml 反序列化元素标签到集合成员时无法加载 XmlArrayItem 标注中定义的对象类型[%1$s]。", new Object[] { className }));
	        }

	      }

	    }

	    return null;
	  }

	  public static String serializeObjectToString(Object p_value)
	  {
	    if ((p_value instanceof java.util.Date)) {
	      String dateString = p_value.toString();
	      if ((p_value instanceof java.sql.Date)) {
	        dateString = dateString + " 00:00:00.000000";
	      } else if ((p_value instanceof Time)) {
	        dateString = "1900-01-01 " + dateString + ".000000";
	      } else if (((p_value instanceof Timestamp)) && 
	        (dateString.length() < "yyyy-MM-dd HH:mm:ss.SSSSSS".length())) {
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	        dateString = df.format(p_value) + "000";
	      }

	      return dateString.replace(" ", "T");
	    }if ((p_value instanceof byte[])) {
	    	return com.sun.org.apache.xml.internal.security.utils.Base64.encode((byte[])p_value);
	      //return new String(Base64.encode((byte[])p_value));
	    }
	    return p_value.toString();
	  }
}
