package tgtools.xml;
/**
 * @author tianjing
 */
public interface XMLStreamWriter {

	/**
	 * writeStartElement
	 * @param localName
	 * @throws XMLStreamException
	 */
	void writeStartElement(String localName) throws XMLStreamException;

	/**
	 * writeEndElement
	 * @throws XMLStreamException
	 */
	void writeEndElement() throws XMLStreamException;

	/**
	 * writeEndDocument
	 * @throws XMLStreamException
	 */
	void writeEndDocument() throws XMLStreamException;

	/**
	 * close
	 * @throws XMLStreamException
	 */
	void close() throws XMLStreamException;

	/**
	 * flush
	 * @throws XMLStreamException
	 */
	void flush() throws XMLStreamException;

	/**
	 * writeNamespace
	 * @param prefix
	 * @param pNamespaceURI
	 * @throws XMLStreamException
	 */
	void writeNamespace(String prefix, String pNamespaceURI) throws XMLStreamException;

	/**
	 * writeStartDocument
	 * @throws XMLStreamException
	 */
	void writeStartDocument() throws XMLStreamException;

	/**
	 * writeStartDocument
	 * @param pEncoding
	 * @param pStandlone
	 * @throws XMLStreamException
	 */
	void writeStartDocument(String pEncoding, boolean pStandlone)
			throws XMLStreamException;

	/**
	 * writeCharacters
	 * @param pText
	 * @throws XMLStreamException
	 */
	void writeCharacters(String pText) throws XMLStreamException;

	/**
	 * writeCData
	 * @param pData
	 * @throws XMLStreamException
	 */
	void writeCData(String pData) throws XMLStreamException;

	/**
	 * writeAttribute
	 * @param pNamespaceURI
	 * @param pLocalName
	 * @param pValue
	 * @throws XMLStreamException
	 */
	void writeAttribute(String pNamespaceURI,String pLocalName,String pValue) throws XMLStreamException;

	/**
	 *  writeAttribute
	 * @param pLocalName
	 * @param pValue
	 * @throws XMLStreamException
	 */
	void writeAttribute(String pLocalName, String pValue) throws XMLStreamException;
}
