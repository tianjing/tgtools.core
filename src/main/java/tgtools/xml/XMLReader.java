package tgtools.xml;

import tgtools.exceptions.APPErrorException;

import java.io.InputStream;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：16:23
 */
public class XMLReader  implements XMLStreamReader  {
    private javax.xml.stream.XMLStreamReader m_Reader;
    public static XMLReader createNewXMLReader(InputStream p_InputStream) throws APPErrorException {
        try {
            XMLReader reader = new XMLReader();
            reader.m_Reader = XmlSerializeHelper.createXMLInputFactory().createXMLStreamReader(p_InputStream);
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
            return m_Reader.next();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("next出错",e);
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        try {
            return m_Reader.getElementText();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("getElementText出错",e);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            m_Reader.close();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("close出错",e);
        }
    }

    @Override
    public int getAttributeCount() {
        return m_Reader.getAttributeCount();
    }

    @Override
    public String getAttributeName(int i) {
        return m_Reader.getAttributeName(i).getLocalPart();
    }

    @Override
    public String getAttributeValue(int i) {
        return m_Reader.getAttributeValue(i);
    }

    @Override
    public int getEventType() {
        return m_Reader.getEventType();
    }

    @Override
    public String getLocalName() {
        return m_Reader.getLocalName();
    }

    @Override
    public boolean isStartElement() {
        return m_Reader.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return m_Reader.isEndElement();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        try {
            return m_Reader.nextTag();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("nextTag出错",e);
        }
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        try {
            return m_Reader.hasNext();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("hasNext出错",e);
        }
    }
}
