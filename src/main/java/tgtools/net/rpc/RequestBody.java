package tgtools.net.rpc;



import tgtools.exceptions.APPErrorException;
import tgtools.xml.IXmlSerializable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;

/**
 * 请求内容对象
 * 注：该类为抽象类
 * 通过重写 readXml 或 readBody 来转换xml内容
 */
public class RequestBody implements IXmlSerializable {

	public RequestBody() {

	}

	protected String m_Method;
	protected Object[] m_Param;

	public String getMethod() {
		return m_Method;
	}

	public void setMethod(String p_Method) {
		this.m_Method = p_Method;
	}

	public Object[] getParam() {
		return m_Param;
	}

	public void setParam(Object[] p_Param) {
		this.m_Param = p_Param;
	}

	protected  void writeMethod(XMLStreamWriter write) throws XMLStreamException {
		write.writeStartElement("", m_Method);

	}
	protected  void writeParam (XMLStreamWriter write,String p_ParamName,String p_ParamValue) throws XMLStreamException
	{
		write.writeStartElement(
				"", p_ParamName );

		write.writeCharacters(p_ParamValue);


	}
	public static void main(String[] args) throws APPErrorException {
		StringWriter ss = new StringWriter();
		XMLStreamWriter write;
		try {
			write = tgtools.xml.XmlSerializeHelper.createXmlOutputFactory()
					.createXMLStreamWriter(ss);

			RequestBody body = new RequestBody();
			body.setMethod("test");
			body.setParam(new Object[] { "田径", 1 });
			body.writeXml(write);
			write.close();
			System.out.println(ss.toString());
		} catch (XMLStreamException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public void writeXml(XMLStreamWriter write) throws APPErrorException {
		try {
			write.writeStartDocument();


		write.writeStartElement("", "s:Envelope", "");
		write.writeNamespace("s", "http://schemas.xmlsoap.org/soap/envelope/");

		write.writeStartElement("",
				"s:Body");
		writeMethod(write);
		//write.writeStartElement("", m_Method);
		//write.writeAttribute("xmlns","http://WebXml.com.cn/");
		if (null != m_Param) {
			for (int i = 0; i < m_Param.length; i++) {
				writeParam(write,"in"+ i,m_Param[i].toString());
				write.writeEndElement();
			}

		}
		write.writeEndElement();
		write.writeEndElement();
		write.writeEndElement();
		write.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new APPErrorException("写XML时出错",e);
		}
	}

	@Override
	public void readXml(XMLStreamReader paramXMLStreamReader) {

	}
}
