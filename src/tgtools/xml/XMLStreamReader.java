package tgtools.xml;

/**
 * 表示xml读取的通用接口
 */
public interface XMLStreamReader {
        public  int next()
            throws XMLStreamException;

        public  String getElementText()
            throws XMLStreamException;
        
        public  void close()
            throws XMLStreamException;
        
        public  int getAttributeCount();
        
        public  String getAttributeName(int i);
        
        public String getAttributeValue(int i);
        
        public  int getEventType();
        
        public  String getLocalName();
        
        boolean isStartElement();
        
        boolean isEndElement();

        int nextTag()throws XMLStreamException;

        boolean hasNext()  throws XMLStreamException;
}
