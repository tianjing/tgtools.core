package tgtools.xml;

/**
 * 表示xml读取的通用接口
 * @author tianjing
 */
public interface XMLStreamReader {
        /**
         * next
         * @return
         * @throws XMLStreamException
         */
        int next()
            throws XMLStreamException;

        /**
         * getElementText
         * @return
         * @throws XMLStreamException
         */
         String getElementText()
            throws XMLStreamException;

        /**
         * close
         * @throws XMLStreamException
         */
        void close()
            throws XMLStreamException;

        /**
         * getAttributeCount
         * @return
         */
        int getAttributeCount();

        /**
         * getAttributeName
         * @param i
         * @return
         */
        String getAttributeName(int i);

        /**
         * getAttributeValue
         * @param i
         * @return
         */
        String getAttributeValue(int i);

        /**
         * getEventType
         * @return
         */
        int getEventType();

        /**
         * getLocalName
         * @return
         */
        String getLocalName();

        /**
         * isStartElement
         * @return
         */
        boolean isStartElement();

        /**
         * isEndElement
         * @return
         */
        boolean isEndElement();

        /**
         * nextTag
         * @return
         * @throws XMLStreamException
         */
        int nextTag()throws XMLStreamException;

        /**
         * hasNext
         * @return
         * @throws XMLStreamException
         */
        boolean hasNext()  throws XMLStreamException;
}
