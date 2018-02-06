package tgtools.db.pi3000;

import tgtools.data.DataTable;
import tgtools.net.rpc.ResponseBody;
import tgtools.util.StringUtil;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringReader;

public class WsDsResult extends ResponseBody {
	private String resultHint;
	private int rows;
	private boolean successful;
	private String xmlDataTable;
	private DataTable table;
	public String getResultHint() {
		return resultHint;
	}

	public void setResultHint(String resultHint) {
		this.resultHint = resultHint;
	}

	public int getRows() {
		return rows;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getXmlDataTable() {
		return xmlDataTable;
	}

	public void setXmlDataTable(String xmlDataTable) {
		this.xmlDataTable = xmlDataTable;
	}

	public DataTable getTable()
	{
		return table;
	}
	@Override
	public void writeXml(XMLStreamWriter paramXMLStreamWriter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readXml(XMLStreamReader reader) {

		try {
			while (reader.hasNext()) {
				int type = reader.getEventType();
				if (XMLStreamReader.START_ELEMENT == type) {
					String name = reader.getLocalName();
					if (StringUtil.equals("resultHint", name)) {
						reader.next();
						if (XMLStreamReader.CHARACTERS == reader.getEventType()) {

							System.out.println(reader.hasText() ? "11:"
									+ reader.getText() : "");

							setResultHint(reader.getText());
						}
					} else if (StringUtil.equals("rows", name)) {
						reader.next();
						if (XMLStreamReader.CHARACTERS == reader.getEventType()) {

							System.out.println(reader.hasText() ? "11:"
									+ reader.getText() : "");

							setRows(Integer.valueOf(reader.getText()));
						}
					} else if (StringUtil.equals("successful", name)) {
						reader.next();
						if (XMLStreamReader.CHARACTERS == reader.getEventType()) {

							System.out.println(reader.hasText() ? "11:"
									+ reader.getText() : "");

							setSuccessful(Boolean.valueOf(reader.getText()));
						}
					} else if (StringUtil.equals("xmlDataTable", name)) {
						StringBuilder sb=new StringBuilder();
						
						while (reader.hasNext()) {
							reader.next();
							if (XMLStreamReader.CHARACTERS == reader
									.getEventType()) {
								sb.append(reader.getText());
							} else {
								setXmlDataTable(sb.toString());
								if(getXmlDataTable().indexOf("<?xml")>=0){
								System.out.println(sb.toString());
								table=new DataTable();
								StringReader dd=new StringReader(getXmlDataTable());
								XMLStreamReader reader1= tgtools.xml.XmlSerializeHelper.createXMLInputFactory().createXMLStreamReader(dd);
								reader1.next();
								table.readXml(reader1);
								System.out.println(table.getRows().size());
								}
								break;
							}
						}
					}
				}
				reader.next();
			}
		} catch (XMLStreamException e) {
			setSuccessful(false);
			setResultHint("读取XML内容出错！"+e.getMessage());
		}
	}
}
