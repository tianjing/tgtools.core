package tgtools.xml;

import tgtools.exceptions.APPErrorException;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * 描述一个class是否具有序列化xml的能力
 */
public abstract interface IXmlSerializable
{
  public abstract void writeXml(XMLStreamWriter paramXMLStreamWriter) throws APPErrorException;

  public abstract void readXml(XMLStreamReader paramXMLStreamReader) throws APPErrorException;
}
