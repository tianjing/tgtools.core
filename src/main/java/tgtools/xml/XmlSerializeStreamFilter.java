package tgtools.xml;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;
/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：17:24
 */
public class XmlSerializeStreamFilter  implements StreamFilter
{
    public boolean accept(XMLStreamReader p_reader)
    {
        return ((p_reader.getEventType() != 6) && (p_reader.getEventType() != 3) && (p_reader.getEventType() != 5) && (!p_reader.isWhiteSpace())) || (p_reader.getEventType() == 12);
    }
}