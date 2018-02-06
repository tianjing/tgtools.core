package tgtools.xml;

import tgtools.exceptions.APPErrorException;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：16:44
 */
public class XMLWriter implements XMLStreamWriter {
    private javax.xml.stream.XMLStreamWriter m_Writer;
    public static XMLWriter createNewXMLReader(OutputStream p_OutputStream) throws APPErrorException {
        try {
            XMLWriter writer = new XMLWriter();
            writer.m_Writer = XmlSerializeHelper.createXmlOutputFactory().createXMLStreamWriter(p_OutputStream);
            return writer;
        }
        catch (Exception ex)
        {
            throw new APPErrorException("创建XMLReader出错",ex);
        }
    }

    @Override
    public void writeStartElement(String s) throws XMLStreamException {
        try {
            m_Writer.writeStartElement(s);
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeStartElement出错",e);
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        try {
            m_Writer.writeEndElement();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeEndElement出错",e);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            m_Writer.writeEndDocument();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeEndDocument出错",e);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            m_Writer.close();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("close出错",e);
        }
    }

    @Override
    public void flush() throws XMLStreamException {
        try {
            m_Writer.flush();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("flush出错",e);
        }
    }

    @Override
    public void writeNamespace(String s, String s1) throws XMLStreamException {
        try {
            m_Writer.writeNamespace(s,s1);
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeNamespace出错",e);
        }
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        try {
            m_Writer.writeStartDocument();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeStartDocument出错",e);
        }
    }

    @Override
    public void writeStartDocument(String encoding, boolean standlone) throws XMLStreamException {
        try {
            m_Writer.writeStartDocument(encoding,"1.0");
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeStartDocument出错",e);
        }
    }

    @Override
    public void writeCharacters(String s) throws XMLStreamException {
        try {
            m_Writer.writeCharacters(s);
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeCharacters出错",e);
        }
    }

    @Override
    public void writeCData(String s) throws XMLStreamException {
        try {
            m_Writer.writeCData(s);
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeCData出错",e);
        }
    }

    @Override
    public void writeAttribute(String namespace, String name, String value) throws XMLStreamException {
        try {
            m_Writer.writeAttribute(namespace,name,value);
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeAttribute出错",e);
        }
    }

    @Override
    public void writeAttribute(String name, String value) throws XMLStreamException {
        try {
            m_Writer.writeAttribute(name,value);
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XMLStreamException("writeAttribute出错",e);
        }
    }
}
