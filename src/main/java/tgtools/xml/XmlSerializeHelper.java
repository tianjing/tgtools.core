package tgtools.xml;

import tgtools.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
/**
 * @author tianjing
 */
@SuppressWarnings("unchecked")
public class XmlSerializeHelper {
    private static XMLInputFactory inputFactory;
    private static XMLOutputFactory outFactory;

    public static String getSerializableClassElementName(Class<?> pClass) {
        String elementName = pClass.getSimpleName();
        XmlElement elementAnnotation = (XmlElement) pClass.getAnnotation(XmlElement.class);
        if (elementAnnotation != null) {
            elementName = elementAnnotation.name();
        }
        XmlArray arrayAnnotation = (XmlArray) pClass.getAnnotation(XmlArray.class);
        if (arrayAnnotation != null) {
            elementName = arrayAnnotation.name();
        }
        return elementName;
    }

    public static Field[] getFields(Class pClass) {
        Class cls = pClass;
        LinkedList fields = new LinkedList();
        while (cls != null) {
            Field[] f = cls.getDeclaredFields();
            for (int i = 0; i < f.length; i++) {
                int mod = f[i].getModifiers();
                if ((!Modifier.isFinal(mod)) && (!Modifier.isTransient(mod)) && (!Modifier.isStatic(mod))) {
                    fields.add(f[i]);
                }
            }
            cls = cls.getSuperclass();
        }
        return (Field[]) fields.toArray(new Field[fields.size()]);
    }

    public static Field getField(Class pClass, String pNodeName, boolean pIgnoreCase) {
        for (Field field : getFields(pClass)) {
            if (!field.isAnnotationPresent(XmlIgnore.class)) {
                if (StringUtil.equal(field.getName(), pNodeName, pIgnoreCase)) {
                    return field;
                }
                XmlElement elementAnnotation = (XmlElement) field.getAnnotation(XmlElement.class);

                if ((elementAnnotation != null) && (StringUtil.equal(elementAnnotation.name(), pNodeName, pIgnoreCase))) {
                    return field;
                }
                XmlAttribute attributeAnnotation = (XmlAttribute) field.getAnnotation(XmlAttribute.class);

                if ((attributeAnnotation != null) && (StringUtil.equal(attributeAnnotation.name(), pNodeName, pIgnoreCase))) {
                    return field;
                }
                XmlArray arrayAnnotation = (XmlArray) field.getAnnotation(XmlArray.class);
                if ((arrayAnnotation != null) && (StringUtil.equal(arrayAnnotation.name(), pNodeName, pIgnoreCase))) {
                    return field;
                }
            }
        }
        return null;
    }

    public static boolean isInterface(Class pClass, Class pInterface) {
        Class[] classes = pClass.getInterfaces();
        for (Class aClass : classes) {
            if (aClass.equals(pInterface)) {
                return true;
            }
            if (isInterface(aClass, pInterface)) {
                return true;
            }
        }
        Class superClass = pClass.getSuperclass();
        if (superClass != null) {
            return isInterface(superClass, pInterface);
        }
        return false;
    }

    public static boolean isGenericCollectionType(Object pObject) {
        return isGenericCollectionType(pObject.getClass());
    }

    public static boolean isGenericCollectionType(Class pClass) {
        Type gtype = pClass.getGenericSuperclass();
        if ((gtype instanceof ParameterizedType)) {
            ParameterizedType ptype = (ParameterizedType) gtype;
            Type rtype = ptype.getRawType();
            if (isInterface((Class) rtype, Collection.class)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSimpleClass(Class pClass) {
        return (pClass.isPrimitive()) || (pClass.equals(Boolean.class)) || (pClass.equals(Byte.class)) || (pClass.equals(Double.class)) || (pClass.equals(Float.class)) || (pClass.equals(Integer.class)) || (pClass.equals(Long.class)) || (pClass.equals(Short.class)) || (pClass.equals(String.class)) || (java.util.Date.class.isAssignableFrom(pClass));
    }

    public static XMLInputFactory createXMLInputFactory() {
        if (inputFactory == null) {
            inputFactory = XMLInputFactory.newInstance();
        }

        return inputFactory;
    }

    public static XMLOutputFactory createXmlOutputFactory() {
        if (outFactory == null) {
            outFactory = XMLOutputFactory.newInstance();
        }

        return outFactory;
    }

    public static String readText(XMLStreamReader pReader) {
        StringBuilder charactersBuilder = new StringBuilder("");

        while ((pReader.isCharacters()) || (pReader.getEventType() == 4)) {
            charactersBuilder.append(pReader.getText());
            try {
                pReader.next();
            } catch (XMLStreamException e) {
                throw new XmlSerializeException("从 XMLStreamReader 中读取Xml节点的文本内容时发生异常。", e);
            }
        }

        return charactersBuilder.toString();
    }

    public static void writeText(XMLStreamWriter pWriter, String pText) {
        try {
            if (pText.contains("]]>")) {
                pWriter.writeCharacters(pText);
            } else {
                pWriter.writeCData(pText);
            }
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("向 XMLStreamWriter 中写入文本内 容时发生异常。", e);
        }
    }

    public static void readToStartElement(XMLStreamReader pReader) {
        while (pReader.getEventType() != 1) {
            try {
                pReader.next();
            } catch (XMLStreamException e) {
                throw new XmlSerializeException("从 XMLStreamReader 中读取Xml StartElement时发生异常。", e);
            }
        }
    }

    public static Class getArrayItemClass(XmlArrayItem pXmlArrayItem, String pElementName) {
        if (pXmlArrayItem != null) {
            String[] classNames = pXmlArrayItem.classNames();
            for (String className : classNames) {
                try {
                    Class c = Thread.currentThread().getContextClassLoader().loadClass(className);

                    if (c.isAnnotationPresent(XmlElement.class)) {
                        if (((XmlElement) c.getAnnotation(XmlElement.class)).name().equals(pElementName)) {
                            return c;
                        }
                    } else if (c.getSimpleName().equals(pElementName)) {
                        return c;
                    }
                } catch (ClassNotFoundException e) {
                    throw new XmlSerializeException(String.format("Xml 反序列化元素标签到集合成员时无法加载 XmlArrayItem 标注中定义的对象类型[%1$s]。", new Object[]{className}));
                }

            }

        }

        return null;
    }

    public static String serializeObjectToString(Object pValue) {
        if ((pValue instanceof java.util.Date)) {
            String dateString = pValue.toString();
            if ((pValue instanceof java.sql.Date)) {
                dateString = dateString + " 00:00:00.000000";
            } else if ((pValue instanceof Time)) {
                dateString = "1900-01-01 " + dateString + ".000000";
            } else if (((pValue instanceof Timestamp)) &&
                    (dateString.length() < "yyyy-MM-dd HH:mm:ss.SSSSSS".length())) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                dateString = df.format(pValue) + "000";
            }

            return dateString.replace(" ", "T");
        }
        if ((pValue instanceof byte[])) {
            return com.sun.org.apache.xml.internal.security.utils.Base64.encode((byte[]) pValue);
        }
        return pValue.toString();
    }
}
