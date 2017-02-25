package tgtools.rpc;


import org.apache.tools.ant.filters.StringInputStream;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.xml.IXmlSerializable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

/**
 * 接收内容的对象
 * 注：该类为抽象类
 * 通过重写 readXml 或 readBody 来转换xml内容
 */
public abstract class ResponseBody implements IXmlSerializable {


    private String m_FaultCode;
    private String m_FaultString;
    private InputStream m_InputStream;

    public String getFaultCode() {
        return m_FaultCode;
    }

    public String getFaultString() {
        return m_FaultString;
    }

    @Override
    public void writeXml(XMLStreamWriter xmlStreamWriter) {

    }

    public void init(String p_Result) {
        m_InputStream = new java.io.ByteArrayInputStream(p_Result.getBytes());
    }

    public void init(InputStream p_Result) {
        m_InputStream = p_Result;
    }

    public void parse() throws APPErrorException {

        try {
            XMLStreamReader reader = tgtools.xml.XmlSerializeHelper.createXMLInputFactory().createXMLStreamReader(m_InputStream);
            readXml(reader);

        } catch (XMLStreamException e) {
            throw new APPErrorException("数据转换出错", e);
        }

    }

    protected String getText(String p_LocalName, XMLStreamReader xmlStreamReader) throws XMLStreamException {
        do {

            while (xmlStreamReader.getEventType() != XMLEvent.START_ELEMENT) {
                if (xmlStreamReader.getEventType() == XMLEvent.END_DOCUMENT) {
                    return null;
                }
                xmlStreamReader.next();
            }
            String name = xmlStreamReader.getLocalName().toLowerCase();
            if (!StringUtil.isNullOrEmpty(name) && name.indexOf(p_LocalName) > -1) {
                xmlStreamReader.next();

                StringBuilder sb=new StringBuilder();
                while(xmlStreamReader.getEventType() == XMLEvent.CHARACTERS) {
                    sb.append(xmlStreamReader.getText());
                    xmlStreamReader.next();
                    }
                return sb.toString();
            }
        } while (xmlStreamReader.next() > 0 );
        return null;
    }

    @Override
    public void readXml(XMLStreamReader xmlStreamReader) {
        try {
            while (xmlStreamReader.next() > 0) {
                if (xmlStreamReader.getEventType() == XMLEvent.END_DOCUMENT) {
                    break;
                }
                if (xmlStreamReader.getEventType() == XMLEvent.START_ELEMENT) {
                    String name = xmlStreamReader.getLocalName().toLowerCase();

                    if (!"envelope".equals(name) && !"body".equals(name)) {
                        String localname = xmlStreamReader.getLocalName().toLowerCase();

                        if ("faultcode".equals(localname)) {
                            while (xmlStreamReader.next() > 0 && xmlStreamReader.getEventType() == XMLEvent.CHARACTERS) {
                                m_FaultCode = xmlStreamReader.getText();
                                break;
                            }
                        } else if ("faultstring".equals(localname)) {
                            while (xmlStreamReader.next() > 0 && xmlStreamReader.getEventType() == XMLEvent.CHARACTERS) {
                                m_FaultString = xmlStreamReader.getText();
                                break;
                            }
                        } else {
                            readBody(xmlStreamReader);
                        }
                    }
                }

            }

        } catch (XMLStreamException e) {
            LogHelper.error("","解析XML出错","ResponseBody.readXml",e);
        }
    }


    protected void readBody(XMLStreamReader read) {

    }

    public static void main(String[] args) throws XMLStreamException {
        String aa = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><soap:Fault><faultcode>soap:Client</faultcode><faultstring>没有有效的操作参数，无法处理请求。请提供有效的 SOAP 操作。</faultstring><detail /></soap:Fault></soap:Body></soap:Envelope>";
        String res = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><getSupportCityResponse><getSupportCityResult><String>南京</String><String>南京</String></getSupportCityResult></getSupportCityResponse></soap:Body></soap:Envelope>";
        StringInputStream input = new StringInputStream(res);
        XMLStreamReader read = tgtools.xml.XmlSerializeHelper.createXMLInputFactory().createXMLStreamReader(input);
       // ResponseBody dd = new ResponseBody();
        //dd.readXml(read);


    }


}
