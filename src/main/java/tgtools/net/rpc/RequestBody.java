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
 * @author tianjing
 */
public class RequestBody implements IXmlSerializable {

	public RequestBody() {

	}

	protected String method;
	protected Object[] param;

	public String getMethod() {
		return method;
	}

	public void setMethod(String pMethod) {
		this.method = pMethod;
	}

	public Object[] getParam() {
		return param;
	}

	public void setParam(Object[] pParam) {
		this.param = pParam;
	}

	protected  void writeMethod(XMLStreamWriter write) throws XMLStreamException {
		write.writeStartElement("", method);

	}
	protected  void writeParam (XMLStreamWriter write,String pParamName,String pParamValue) throws XMLStreamException
	{
		write.writeStartElement(
				"", pParamName );

		write.writeCharacters(pParamValue);


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
		if (null != param) {
			for (int i = 0; i < param.length; i++) {
				writeParam(write,"in"+ i,param[i].toString());
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
	public void readXml(XMLStreamReader pParamXMLStreamReader) {

	}
}
