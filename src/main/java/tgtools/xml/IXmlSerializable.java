package tgtools.xml;

import tgtools.exceptions.APPErrorException;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * @author tianjing
 */
public interface IXmlSerializable
{
  /**
   * writeXml
   * @param pParamXmlStreamWriter
   * @throws APPErrorException
   */
  void writeXml(XMLStreamWriter pParamXmlStreamWriter) throws APPErrorException;

  /**
   * readXml
   * @param pParamXmlStreamWriter
   * @throws APPErrorException
   */
  void readXml(XMLStreamReader pParamXmlStreamWriter) throws APPErrorException;
}
