package tgtools.xml;

import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.ReflectionUtil;
import tgtools.util.StringUtil;

import javax.xml.stream.*;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：17:20
 */
@SuppressWarnings("unchecked")
public class XmlSerializer {
    public static String serialize(Object p_obj) throws APPErrorException {
        try {
            XMLOutputFactory xmlOutputFactory = XmlSerializeHelper.createXmlOutputFactory();

            StringWriter stringWriter = new StringWriter();
            XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            try {
                serialize(p_obj, xmlStreamWriter);
                xmlStreamWriter.flush();
                stringWriter.flush();
                String s = stringWriter.toString();
                xmlStreamWriter.close();
                stringWriter.close();
                return s;
            } finally {
                stringWriter.close();
                xmlStreamWriter.close();
            }
        } catch (IOException e) {
            throw new XmlSerializeException("XmlSerializer正向序列化结束，释放资源时发生异常。", e);
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("XmlSerializer正向序列化发生异常。", e);
        }
    }

    public static void serialize(Object p_obj, XMLStreamWriter p_writer) throws APPErrorException {
        Class objClass = p_obj.getClass();

        if (!objClass.isAnnotationPresent(XmlSerializable.class)) {
            throw new XmlSerializeException(String.format("类型[%1$s]未标记XmlSerializable，无法进行Xml序列化。", new Object[]{objClass.getName()}));
        }

        if ((p_obj instanceof IXmlSerializable)) {
            ((IXmlSerializable) p_obj).writeXml(p_writer);
            return;
        }

        String elementName = XmlSerializeHelper.getSerializableClassElementName(objClass);
        try {
            p_writer.writeStartElement(elementName);

            if ((p_obj instanceof Collection)) {
                serializeCollection((Collection) p_obj, p_writer);
            } else {
                Field[] fields = XmlSerializeHelper.getFields(p_obj.getClass());
                for (Field field : fields) {
                    if (field.isAnnotationPresent(XmlAttribute.class))
                        serializeField(p_obj, field, p_writer);
                }
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(XmlAttribute.class))
                        serializeField(p_obj, field, p_writer);
                }
            }
            p_writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException(e);
        }
    }

    private static void serializeCollection(Collection p_collection, XMLStreamWriter p_writer) throws APPErrorException {
        for (Iterator i$ = p_collection.iterator(); i$.hasNext(); ) {
            Object itemValue = i$.next();
            serializeSingleObjToElement(itemValue, p_writer);
        }
    }

    @SuppressWarnings("unchecked")
    private static void serializeField(Object p_obj, Field p_field, XMLStreamWriter p_writer) throws APPErrorException {
        try {
            if (p_field.isAnnotationPresent(XmlIgnore.class)) {
                return;
            }

            Object value = null;
            try {
                p_field.setAccessible(true);
                value = p_field.get(p_obj);
                if (value == null)
                    return;
            } catch (Exception e) {
                throw new XmlSerializeException("序列化过程中反射成员值失败。", e);
            }

            XmlAttribute attrAnnotation = (XmlAttribute) p_field.getAnnotation(XmlAttribute.class);

            if (attrAnnotation != null) {
                p_writer.writeAttribute(attrAnnotation.name(), value.toString());

                return;
            }

            String elementName = p_field.getName();

            XmlElement elementAnnotation = (XmlElement) p_field.getAnnotation(XmlElement.class);

            if (elementAnnotation != null) {
                elementName = elementAnnotation.name();
            }
            XmlArray arrayAnnotation = (XmlArray) p_field.getAnnotation(XmlArray.class);
            if ((arrayAnnotation != null) &&
                    (!arrayAnnotation.name().equals(""))) {
                elementName = arrayAnnotation.name();
            }

            p_writer.writeStartElement(elementName);
            if (value.getClass().isAnnotationPresent(XmlSerializable.class))
                serialize(value, p_writer);
            else if (value.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(value); i++)
                    serializeSingleObjToElement(Array.get(value, i), p_writer);
            } else if ((value instanceof Collection)) {
                serializeCollection((Collection) value, p_writer);
            } else {
                p_writer.writeCharacters(value.toString());
            }
            p_writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException(e);
        }
    }

    public static void serializeSingleObjToElement(Object p_obj, XMLStreamWriter p_writer) throws APPErrorException {
        try {
            if (p_obj.getClass().isAnnotationPresent(XmlSerializable.class)) {
                serialize(p_obj, p_writer);
            } else if (XmlSerializeHelper.isSimpleClass(p_obj.getClass())) {
                p_writer.writeStartElement(p_obj.getClass().getSimpleName());

                p_writer.writeCharacters(p_obj.toString());
                p_writer.writeEndElement();
            } else {
                throw new XmlSerializeException(String.format("XmlSerializer正向序列化到Xml元素标签时发生异常，类型[%1$s]无法自动序列化。", new Object[]{p_obj.getClass().getName()}));
            }

        } catch (XMLStreamException e) {
            throw new XmlSerializeException("XmlSerializer正向序列化到Xml元素标签时发生异常。", e);
        }
    }

    public static <T> T deserialize(String p_xml, Class<T> p_class) throws APPErrorException {
        try {
            XMLInputFactory xmlInputFactory = XmlSerializeHelper.createXMLInputFactory();

            StringReader stringReader = new StringReader(p_xml);
            XMLStreamReader xmlReader = xmlInputFactory.createFilteredReader(xmlInputFactory.createXMLStreamReader(stringReader), new XmlSerializeStreamFilter());
            try {
                return deserialize(xmlReader, p_class);
            } finally {
                xmlReader.close();
                stringReader.close();
            }
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("XmlSerializer正向序列化发生异常。", e);
        }
    }

    private static void setFieldValue(Object p_Obj, Map<String, Object> p_Filelds) {
        if (null == p_Filelds) return;
        for (Map.Entry<String, Object> item : p_Filelds.entrySet()) {
            try {
                Field field = ReflectionUtil.findField(p_Obj.getClass(),item.getKey());
                if (null != field) {
                    field.setAccessible(true);
                    field.set(p_Obj, item.getValue());
                }
                else
                {
                    LogHelper.error("", "无法获取对象属性,对象：" + p_Obj.getClass() + ",属性：" + item.getKey(), "XmlSerializer.setFieldValue", new APPErrorException(""));
                }
            } catch (IllegalAccessException e) {
                LogHelper.error("", "无法给对象属性赋值,对象：" + p_Obj.getClass() + ",属性：" + item.getKey() + ",值：" + item.getValue(), "XmlSerializer.setFieldValue", e);
            }
        }

    }

        /**
         * 反序列化
         * @param p_xmlReader
         * @param p_class
         * @param <T>
         * @return
         * @throws APPErrorException
         */
    public static <T> T deserialize(XMLStreamReader p_xmlReader, Class<T> p_class) throws APPErrorException {
        return deserialize(p_xmlReader, p_class, new HashMap<String, Object>());
    }

    /**
     * 反序列化
     * @param p_xmlReader
     * @param p_class
     * @param p_Filelds 对象需要初始化的参数
     * @param <T>
     * @return
     * @throws APPErrorException
     */
    public static <T> T deserialize(XMLStreamReader p_xmlReader, Class<T> p_class, Map<String, Object> p_Filelds) throws APPErrorException {
        Object result = null;
        try {
            while ((p_xmlReader.hasNext()) &&
                    (!p_xmlReader.isStartElement())) {
                p_xmlReader.next();
            }
            if (!p_xmlReader.isStartElement()) {
                throw new XmlSerializeException("Xml 反序列化时无法定位起始元素。");
            }

            if (XmlSerializeHelper.isSimpleClass(p_class)) {
                p_xmlReader.next();
                return (T) ReflectionUtil.instanceSimpleClass(p_class, XmlSerializeHelper.readText(p_xmlReader));
            }

            result = p_class.newInstance();
            setFieldValue(result, p_Filelds);
            if ((result instanceof IXmlSerializable)) {
                ((IXmlSerializable) result).readXml(p_xmlReader);
                return (T) result;
            }

            String elementName = XmlSerializeHelper.getSerializableClassElementName(p_class);

            if (!StringUtil.equal(elementName, p_xmlReader.getLocalName(), true)) {
                throw new XmlSerializeException(String.format("Xml 反序列化时，Xml 文档的元素标签名称[%1$s]和对象映射名称[%2$s]不匹配。", new Object[]{p_xmlReader.getLocalName(), elementName}));
            }

            if ((result instanceof Collection)) {
                deserializeCollection((Collection) result, p_class, (XmlArrayItem) p_class.getAnnotation(XmlArrayItem.class), p_xmlReader);
            } else {
                for (int i = 0; i < p_xmlReader.getAttributeCount(); i++) {
                    String attrName = p_xmlReader.getAttributeName(i).toString();

                    String attrValue = p_xmlReader.getAttributeValue(i);
                    Field f = XmlSerializeHelper.getField(p_class, attrName, true);

                    if (f == null) {
                        throw new XmlSerializeException(String.format("Xml 反序列化时无法映射属性节点名称为[%1$s]的成员。", new Object[]{attrName}));
                    }

                    f.setAccessible(true);
                    f.set(result, ReflectionUtil.instanceSimpleClass(f.getType(), attrValue));
                }

                while (p_xmlReader.hasNext()) {
                    p_xmlReader.next();
                    if (p_xmlReader.isEndElement())
                        break;
                    if (p_xmlReader.isStartElement()) {
                        elementName = p_xmlReader.getLocalName();
                        Field f = XmlSerializeHelper.getField(p_class, elementName, true);

                        if (f == null) {
                            throw new XmlSerializeException(String.format("Xml 反序列化时，无法定位节点名称为[%1$s]的成员。", new Object[]{elementName}));
                        }

                        f.setAccessible(true);
                        deserializeField(result, f, p_xmlReader);
                    }
                }
            }
            return (T) result;
        } catch (InstantiationException e) {
            throw new XmlSerializeException(e);
        } catch (IllegalAccessException e) {
            throw new XmlSerializeException(e);
        } catch (XMLStreamException e) {
            throw new XmlSerializeException(e);
        }
    }

    private static void deserializeCollection(Collection p_collection, Class p_collectionClass, XmlArrayItem p_xmlArrayItem, XMLStreamReader p_xmlReader) throws APPErrorException {
        try {
            while (p_xmlReader.hasNext()) {
                p_xmlReader.next();
                if (p_xmlReader.isEndElement())
                    break;
                if (p_xmlReader.isStartElement()) {
                    String elementName = p_xmlReader.getLocalName();
                    Class arrayItemClass = XmlSerializeHelper.getArrayItemClass(p_xmlArrayItem, elementName);

                    if (arrayItemClass == null) {
                        arrayItemClass = p_collectionClass.getComponentType();
                    }
                    if (arrayItemClass == null) {
                        throw new XmlSerializeException(String.format("数组或集合类型的对象[%1$s]必须使用 XmlArrayItem 标记其元素类型。", new Object[]{p_collectionClass.getName()}));
                    }

                    p_collection.add(deserialize(p_xmlReader, arrayItemClass));
                }
            }
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("Xml 反序列化到集合对象时发生异常。", e);
        } catch (XmlSerializeException e) {
            throw new XmlSerializeException("Xml 反序列化到集合对象时发生异常。可能是集合定义的元素类型不是Xml文档中对应的具体类型，而是其父类。", e);
        }
    }

    private static void deserializeField(Object p_obj, Field p_field, XMLStreamReader p_xmlReader) throws APPErrorException {
        try {
            Class fieldClass = p_field.getType();
            if (XmlSerializeHelper.isSimpleClass(fieldClass)) {
                p_xmlReader.next();
                p_field.set(p_obj, ReflectionUtil.instanceSimpleClass(fieldClass, XmlSerializeHelper.readText(p_xmlReader)));
            } else if (fieldClass.isAnnotationPresent(XmlSerializable.class)) {
                p_field.set(p_obj, deserialize(p_xmlReader, fieldClass));
            } else if (fieldClass.isArray()) {
                ArrayList list = new ArrayList();
                deserializeCollection(list, fieldClass, (XmlArrayItem) p_field.getAnnotation(XmlArrayItem.class), p_xmlReader);

                Object objArray = Array.newInstance(fieldClass.getComponentType(), list.size());

                if (list.size() > 0) {
                    Object[] tempArray = new Object[list.size()];
                    list.toArray(tempArray);
                    for (int i = 0; i < tempArray.length; i++) {
                        Array.set(objArray, i, tempArray[i]);
                    }
                }
                p_field.set(p_obj, objArray);
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Collection collection = (Collection) fieldClass.newInstance();
                deserializeCollection(collection, fieldClass, (XmlArrayItem) p_field.getAnnotation(XmlArrayItem.class), p_xmlReader);

                p_field.set(p_obj, collection);
            } else {
                throw new XmlSerializeException(String.format("Xml 反序列化元素标签到对象成员时遇到无法解析的数据类型[%1$s]。", new Object[]{fieldClass.getName()}));
            }
        } catch (XmlSerializeException e) {
            throw new XmlSerializeException(String.format("反序列化对象成员[%1$s]时发生异常。", new Object[]{p_field.getName()}), e);
        } catch (InstantiationException e) {
            throw new XmlSerializeException(e);
        } catch (IllegalAccessException e) {
            throw new XmlSerializeException(e);
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("Xml 反序列化到对象成员时发生异常。", e);
        }
    }
}
