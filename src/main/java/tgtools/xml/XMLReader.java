package tgtools.xml;

import tgtools.exceptions.APPErrorException;

import java.io.InputStream;

/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：16:23
 */
public class XMLReader  implements XMLStreamReader  {
    private javax.xml.stream.XMLStreamReader xmlStreamReader;
    public static XMLReader createNewXMLReader(InputStream pInputStream) throws APPErrorException {
        try {
            XMLReader reader = new XMLReader();
            reader.xmlStreamReader = XmlSerializeHelper.createXMLInputFactory().createXMLStreamReader(pInputStream);
            return reader;
        }
        catch (Exception ex)
        {
            throw new APPErrorException("创建XMLReader出错",ex);
        }
    }
    @Override
    public int next() throws XMLStreamException {
        try {
            return xmlStreamReader.next();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("next出错",e);
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        try {
            return xmlStreamReader.getElementText();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("getElementText出错",e);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            xmlStreamReader.close();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("close出错",e);
        }
    }

    @Override
    public int getAttributeCount() {
        return xmlStreamReader.getAttributeCount();
    }

    @Override
    public String getAttributeName(int i) {
        return xmlStreamReader.getAttributeName(i).getLocalPart();
    }

    @Override
    public String getAttributeValue(int i) {
        return xmlStreamReader.getAttributeValue(i);
    }

    @Override
    public int getEventType() {
        return xmlStreamReader.getEventType();
    }

    @Override
    public String getLocalName() {
        return xmlStreamReader.getLocalName();
    }

    @Override
    public boolean isStartElement() {
        return xmlStreamReader.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return xmlStreamReader.isEndElement();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        try {
            return xmlStreamReader.nextTag();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("nextTag出错",e);
        }
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        try {
            return xmlStreamReader.hasNext();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("hasNext出错",e);
        }
    }
}
