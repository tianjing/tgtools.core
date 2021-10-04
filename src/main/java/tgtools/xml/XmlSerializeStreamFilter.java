package tgtools.xml;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamReader;
/**
 * 名  称：
 * @author tianjing
 * 功  能：
 * 时  间：17:24
 */
public class XmlSerializeStreamFilter  implements StreamFilter
{
    @Override
    public boolean accept(XMLStreamReader pReader)
    {
        return ((pReader.getEventType() != 6) && (pReader.getEventType() != 3) && (pReader.getEventType() != 5) && (!pReader.isWhiteSpace())) || (pReader.getEventType() == 12);
    }
}