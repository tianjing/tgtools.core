package tgtools.xml;

import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.ReflectionUtil;
import tgtools.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 名  称：
 *
 * @author tianjing
 * 功  能：
 * 时  间：17:20
 */
@SuppressWarnings("unchecked")
public class XmlSerializer {
    public static String serialize(Object pObj) throws APPErrorException {
        try {
            XMLOutputFactory xmlOutputFactory = XmlSerializeHelper.createXmlOutputFactory();

            StringWriter stringWriter = new StringWriter();
            XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
            try {
                serialize(pObj, xmlStreamWriter);
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

    public static void serialize(Object pObj, XMLStreamWriter pWriter) throws APPErrorException {
        Class objClass = pObj.getClass();

        if (!objClass.isAnnotationPresent(XmlSerializable.class)) {
            throw new XmlSerializeException(String.format("类型[%1$s]未标记XmlSerializable，无法进行Xml序列化。", new Object[]{objClass.getName()}));
        }

        if ((pObj instanceof IXmlSerializable)) {
            ((IXmlSerializable) pObj).writeXml(pWriter);
            return;
        }

        String elementName = XmlSerializeHelper.getSerializableClassElementName(objClass);
        try {
            pWriter.writeStartElement(elementName);

            if ((pObj instanceof Collection)) {
                serializeCollection((Collection) pObj, pWriter);
            } else {
                Field[] fields = XmlSerializeHelper.getFields(pObj.getClass());
                for (Field field : fields) {
                    if (field.isAnnotationPresent(XmlAttribute.class)) {
                        serializeField(pObj, field, pWriter);
                    }
                }
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(XmlAttribute.class)) {
                        serializeField(pObj, field, pWriter);
                    }
                }
            }
            pWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException(e);
        }
    }

    private static void serializeCollection(Collection pCollection, XMLStreamWriter pWriter) throws APPErrorException {
        for (Iterator item = pCollection.iterator(); item.hasNext(); ) {
            Object itemValue = item.next();
            serializeSingleObjToElement(itemValue, pWriter);
        }
    }

    @SuppressWarnings("unchecked")
    private static void serializeField(Object pObj, Field pField, XMLStreamWriter pWriter) throws APPErrorException {
        try {
            if (pField.isAnnotationPresent(XmlIgnore.class)) {
                return;
            }

            Object value = null;
            try {
                pField.setAccessible(true);
                value = pField.get(pObj);
                if (value == null) {
                    return;
                }
            } catch (Exception e) {
                throw new XmlSerializeException("序列化过程中反射成员值失败。", e);
            }

            XmlAttribute attrAnnotation = (XmlAttribute) pField.getAnnotation(XmlAttribute.class);

            if (attrAnnotation != null) {
                pWriter.writeAttribute(attrAnnotation.name(), value.toString());
                return;
            }

            String elementName = pField.getName();

            XmlElement elementAnnotation = (XmlElement) pField.getAnnotation(XmlElement.class);

            if (elementAnnotation != null) {
                elementName = elementAnnotation.name();
            }
            XmlArray arrayAnnotation = (XmlArray) pField.getAnnotation(XmlArray.class);
            if ((arrayAnnotation != null) &&
                    (!"".equals(arrayAnnotation.name()))) {
                elementName = arrayAnnotation.name();
            }

            pWriter.writeStartElement(elementName);
            if (value.getClass().isAnnotationPresent(XmlSerializable.class)) {
                serialize(value, pWriter);
            } else if (value.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(value); i++) {
                    serializeSingleObjToElement(Array.get(value, i), pWriter);
                }
            } else if ((value instanceof Collection)) {
                serializeCollection((Collection) value, pWriter);
            } else {
                pWriter.writeCharacters(value.toString());
            }
            pWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException(e);
        }
    }

    public static void serializeSingleObjToElement(Object pObj, XMLStreamWriter pWriter) throws APPErrorException {
        try {
            if (pObj.getClass().isAnnotationPresent(XmlSerializable.class)) {
                serialize(pObj, pWriter);
            } else if (XmlSerializeHelper.isSimpleClass(pObj.getClass())) {
                pWriter.writeStartElement(pObj.getClass().getSimpleName());

                pWriter.writeCharacters(pObj.toString());
                pWriter.writeEndElement();
            } else {
                throw new XmlSerializeException(String.format("XmlSerializer正向序列化到Xml元素标签时发生异常，类型[%1$s]无法自动序列化。", new Object[]{pObj.getClass().getName()}));
            }

        } catch (XMLStreamException e) {
            throw new XmlSerializeException("XmlSerializer正向序列化到Xml元素标签时发生异常。", e);
        }
    }

    public static <T> T deserialize(String pXml, Class<T> pClass) throws APPErrorException {
        try {
            XMLInputFactory xmlInputFactory = XmlSerializeHelper.createXMLInputFactory();

            StringReader stringReader = new StringReader(pXml);
            XMLStreamReader xmlReader = xmlInputFactory.createFilteredReader(xmlInputFactory.createXMLStreamReader(stringReader), new XmlSerializeStreamFilter());
            try {
                return deserialize(xmlReader, pClass);
            } finally {
                xmlReader.close();
                stringReader.close();
            }
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("XmlSerializer正向序列化发生异常。", e);
        }
    }

    private static void setFieldValue(Object pObj, Map<String, Object> pFilelds) {
        if (null == pFilelds) {
            return;
        }
        for (Map.Entry<String, Object> item : pFilelds.entrySet()) {
            try {
                Field field = ReflectionUtil.findField(pObj.getClass(), item.getKey());
                if (null != field) {
                    field.setAccessible(true);
                    field.set(pObj, item.getValue());
                } else {
                    LogHelper.error("", "无法获取对象属性,对象：" + pObj.getClass() + ",属性：" + item.getKey(), "XmlSerializer.setFieldValue", new APPErrorException(""));
                }
            } catch (IllegalAccessException e) {
                LogHelper.error("", "无法给对象属性赋值,对象：" + pObj.getClass() + ",属性：" + item.getKey() + ",值：" + item.getValue(), "XmlSerializer.setFieldValue", e);
            }
        }

    }

    /**
     * 反序列化
     *
     * @param pXmlReader
     * @param pClass
     * @param <T>
     * @return
     * @throws APPErrorException
     */
    public static <T> T deserialize(XMLStreamReader pXmlReader, Class<T> pClass) throws APPErrorException {
        return deserialize(pXmlReader, pClass, new HashMap<String, Object>(10));
    }

    /**
     * 反序列化
     *
     * @param pXmlReader
     * @param pClass
     * @param pFilelds   对象需要初始化的参数
     * @param <T>
     * @return
     * @throws APPErrorException
     */
    public static <T> T deserialize(XMLStreamReader pXmlReader, Class<T> pClass, Map<String, Object> pFilelds) throws APPErrorException {
        Object result = null;
        try {
            while ((pXmlReader.hasNext()) &&
                    (!pXmlReader.isStartElement())) {
                pXmlReader.next();
            }
            if (!pXmlReader.isStartElement()) {
                throw new XmlSerializeException("Xml 反序列化时无法定位起始元素。");
            }

            if (XmlSerializeHelper.isSimpleClass(pClass)) {
                pXmlReader.next();
                return (T) ReflectionUtil.instanceSimpleClass(pClass, XmlSerializeHelper.readText(pXmlReader));
            }

            result = pClass.newInstance();
            setFieldValue(result, pFilelds);
            if ((result instanceof IXmlSerializable)) {
                ((IXmlSerializable) result).readXml(pXmlReader);
                return (T) result;
            }

            String elementName = XmlSerializeHelper.getSerializableClassElementName(pClass);

            if (!StringUtil.equal(elementName, pXmlReader.getLocalName(), true)) {
                throw new XmlSerializeException(String.format("Xml 反序列化时，Xml 文档的元素标签名称[%1$s]和对象映射名称[%2$s]不匹配。", new Object[]{pXmlReader.getLocalName(), elementName}));
            }

            if ((result instanceof Collection)) {
                deserializeCollection((Collection) result, pClass, (XmlArrayItem) pClass.getAnnotation(XmlArrayItem.class), pXmlReader);
            } else {
                for (int i = 0; i < pXmlReader.getAttributeCount(); i++) {
                    String attrName = pXmlReader.getAttributeName(i).toString();

                    String attrValue = pXmlReader.getAttributeValue(i);
                    Field f = XmlSerializeHelper.getField(pClass, attrName, true);

                    if (f == null) {
                        throw new XmlSerializeException(String.format("Xml 反序列化时无法映射属性节点名称为[%1$s]的成员。", new Object[]{attrName}));
                    }

                    f.setAccessible(true);
                    f.set(result, ReflectionUtil.instanceSimpleClass(f.getType(), attrValue));
                }

                while (pXmlReader.hasNext()) {
                    pXmlReader.next();
                    if (pXmlReader.isEndElement()) {
                        break;
                    }
                    if (pXmlReader.isStartElement()) {
                        elementName = pXmlReader.getLocalName();
                        Field f = XmlSerializeHelper.getField(pClass, elementName, true);

                        if (f == null) {
                            throw new XmlSerializeException(String.format("Xml 反序列化时，无法定位节点名称为[%1$s]的成员。", new Object[]{elementName}));
                        }

                        f.setAccessible(true);
                        deserializeField(result, f, pXmlReader);
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

    private static void deserializeCollection(Collection pCollection, Class pCollectionClass, XmlArrayItem pXmlArrayItem, XMLStreamReader pXmlReader) throws APPErrorException {
        try {
            while (pXmlReader.hasNext()) {
                pXmlReader.next();
                if (pXmlReader.isEndElement()) {
                    break;
                }
                if (pXmlReader.isStartElement()) {
                    String elementName = pXmlReader.getLocalName();
                    Class arrayItemClass = XmlSerializeHelper.getArrayItemClass(pXmlArrayItem, elementName);

                    if (arrayItemClass == null) {
                        arrayItemClass = pCollectionClass.getComponentType();
                    }
                    if (arrayItemClass == null) {
                        throw new XmlSerializeException(String.format("数组或集合类型的对象[%1$s]必须使用 XmlArrayItem 标记其元素类型。", new Object[]{pCollectionClass.getName()}));
                    }

                    pCollection.add(deserialize(pXmlReader, arrayItemClass));
                }
            }
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("Xml 反序列化到集合对象时发生异常。", e);
        } catch (XmlSerializeException e) {
            throw new XmlSerializeException("Xml 反序列化到集合对象时发生异常。可能是集合定义的元素类型不是Xml文档中对应的具体类型，而是其父类。", e);
        }
    }

    private static void deserializeField(Object pObj, Field pField, XMLStreamReader pXmlReader) throws APPErrorException {
        try {
            Class fieldClass = pField.getType();
            if (XmlSerializeHelper.isSimpleClass(fieldClass)) {
                pXmlReader.next();
                pField.set(pObj, ReflectionUtil.instanceSimpleClass(fieldClass, XmlSerializeHelper.readText(pXmlReader)));
            } else if (fieldClass.isAnnotationPresent(XmlSerializable.class)) {
                pField.set(pObj, deserialize(pXmlReader, fieldClass));
            } else if (fieldClass.isArray()) {
                ArrayList list = new ArrayList();
                deserializeCollection(list, fieldClass, (XmlArrayItem) pField.getAnnotation(XmlArrayItem.class), pXmlReader);

                Object objArray = Array.newInstance(fieldClass.getComponentType(), list.size());

                if (list.size() > 0) {
                    Object[] tempArray = new Object[list.size()];
                    list.toArray(tempArray);
                    for (int i = 0; i < tempArray.length; i++) {
                        Array.set(objArray, i, tempArray[i]);
                    }
                }
                pField.set(pObj, objArray);
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Collection collection = (Collection) fieldClass.newInstance();
                deserializeCollection(collection, fieldClass, (XmlArrayItem) pField.getAnnotation(XmlArrayItem.class), pXmlReader);

                pField.set(pObj, collection);
            } else {
                throw new XmlSerializeException(String.format("Xml 反序列化元素标签到对象成员时遇到无法解析的数据类型[%1$s]。", new Object[]{fieldClass.getName()}));
            }
        } catch (XmlSerializeException e) {
            throw new XmlSerializeException(String.format("反序列化对象成员[%1$s]时发生异常。", new Object[]{pField.getName()}), e);
        } catch (InstantiationException e) {
            throw new XmlSerializeException(e);
        } catch (IllegalAccessException e) {
            throw new XmlSerializeException(e);
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("Xml 反序列化到对象成员时发生异常。", e);
        }
    }
}
