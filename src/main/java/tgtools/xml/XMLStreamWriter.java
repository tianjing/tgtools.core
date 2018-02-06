package tgtools.xml;

public interface XMLStreamWriter {

	void writeStartElement(String localName) throws XMLStreamException;

	void writeEndElement() throws XMLStreamException;

	void writeEndDocument() throws XMLStreamException;

	void close() throws XMLStreamException;
	
	void flush() throws XMLStreamException;

	void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException;

	void writeStartDocument() throws XMLStreamException;

	void writeStartDocument(String encoding, boolean standlone)
			throws XMLStreamException;

	void writeCharacters(String text) throws XMLStreamException;
	
	void writeCData(String data) throws XMLStreamException;
	
	void writeAttribute(String namespaceURI,String localName,String value) throws XMLStreamException;
	
	void writeAttribute(String localName, String value) throws XMLStreamException;
}
