package tgtools.data;

import tgtools.xml.IXmlSerializable;
import tgtools.xml.XmlSerializeException;
import tgtools.xml.XmlSerializeHelper;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataColumn implements IXmlSerializable, Serializable {
    private static final long serialVersionUID = -1245393580144288826L;

    protected DataTable table;
    protected String columnName;
    protected String caption;
    protected int columnType;
    protected int precision = 0;

    protected int scale = 0;
    protected boolean nullable;
    protected boolean caseSensitive;
    protected boolean readOnly;
    protected boolean primaryKey;
    protected int indexInColList;
    protected Object defaultValue;
    protected String expression;
    private HashMap<String, String> extendedProperties;
    private boolean system;

    protected DataColumn(DataTable p_table, String p_columnName) {
        this.table = p_table;

        this.columnName = p_columnName;
        this.caption = null;
        this.nullable = true;
        this.caseSensitive = false;
        this.readOnly = false;
        this.primaryKey = false;
        setSystem(false);

        this.extendedProperties = new HashMap<String, String>();
        this.extendedProperties.put("visible", "true");
        this.defaultValue = null;
        this.expression = null;
        this.indexInColList = -1;
    }

    public DataTable getTable() {
        return this.table;
    }

    public String getColumnName() {
        if (this.caseSensitive) {
            return this.columnName;
        }
        return this.columnName.toUpperCase();
    }

    public void setColumnName(String p_columnName) {
        this.columnName = p_columnName;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String p_caption) {
        this.caption = p_caption;
    }

    public int getColumnType() {
        return this.columnType;
    }

    public void setColumnType(int p_columnType) {
        this.columnType = p_columnType;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int p_precision) {
        this.precision = p_precision;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int p_scale) {
        this.scale = p_scale;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean p_nullable) {
        this.nullable = p_nullable;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setCaseSensitive(boolean p_caseSensitive) {
        this.caseSensitive = p_caseSensitive;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean p_readOnly) {
        this.readOnly = p_readOnly;
    }

    public boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(boolean p_primaryKey) {
        this.primaryKey = p_primaryKey;
        if (this.primaryKey)
            this.nullable = false;
    }

    public HashMap<String, String> getExtendedProperties() {
        return this.extendedProperties;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(Object p_value) {
        this.defaultValue = p_value;
    }

    public boolean isCalculated() {
        return this.expression != null;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String p_expression) {
        this.expression = p_expression;
        this.readOnly = true;
    }

    public int getIndexInColList() {
        return this.indexInColList;
    }

    public void setIndexInColList(int indexInColList) {
        this.indexInColList = indexInColList;
    }

    public boolean equals(Object p_column) {
        if ((p_column instanceof DataColumn)) {
            DataColumn column = (DataColumn) p_column;
            return this.columnName.equals(column.columnName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.columnName.hashCode();
    }

    public boolean isSystem() {
        return this.system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    @Override
    public void readXml(XMLStreamReader p_reader) {
        if (!p_reader.getLocalName().equalsIgnoreCase("Column")) {
            throw new XmlSerializeException("无法反序列化 DataColumn 对象，当前 XMLStreamReader 的游标位置有误。");
        }

        for (int i = 0; i < p_reader.getAttributeCount(); i++) {
            String attrName = p_reader.getAttributeName(i).toString();
            String attrValue = p_reader.getAttributeValue(i);
            if (attrName.equalsIgnoreCase("columnName"))
                this.columnName = attrValue;
            else if (attrName.equalsIgnoreCase("caption"))
                this.caption = attrValue;
            else if (attrName.equalsIgnoreCase("primaryKey"))
                this.primaryKey = Boolean.valueOf(attrValue).booleanValue();
            else if (attrName.equalsIgnoreCase("dbType"))
                this.columnType = DbTypeConverter.getDbType(attrValue);
            else if (attrName.equalsIgnoreCase("expression"))
                this.expression = attrValue;
            else if (attrName.equalsIgnoreCase("nullable"))
                this.nullable = Boolean.valueOf(attrValue).booleanValue();
            else if (attrName.equalsIgnoreCase("precision"))
                this.precision = Integer.valueOf(attrValue).intValue();
            else if (attrName.equalsIgnoreCase("scale"))
                this.scale = Integer.valueOf(attrValue).intValue();
            else if (attrName.equalsIgnoreCase("readOnly"))
                this.readOnly = Boolean.valueOf(attrValue).booleanValue();
            else if (attrName.equalsIgnoreCase("indexInColList"))
                this.indexInColList = Integer.valueOf(attrValue).intValue();
            else if (attrName.equalsIgnoreCase("visible")) {
                getExtendedProperties().put("visible", attrValue);
            } else {
                getExtendedProperties().put(attrName, attrValue);
            }
        }

        for (int i = 0; i < p_reader.getAttributeCount(); i++) {
            String attrName = p_reader.getAttributeName(i).toString();
            String attrValue = p_reader.getAttributeValue(i);
            if (attrName.equalsIgnoreCase("defaultValue")) {
                this.defaultValue = DbTypeConverter.toCommonType(attrValue, getColumnType());

                break;
            }
        }
        try {
            p_reader.nextTag();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XmlSerializeException("DataColumn 反序列化时发生异常。", e);
        }
    }

    @Override
    public void writeXml(XMLStreamWriter p_writer) {
        writeXmlImpl(p_writer, true);
    }

    @SuppressWarnings("rawtypes")
    public void writeXmlImpl(XMLStreamWriter p_writer, boolean p_needMoreCol) {
        try {
            p_writer.writeStartElement("Column");
            p_writer.writeAttribute("columnName", this.columnName);
            if (this.caption != null)
                p_writer.writeAttribute("caption", this.caption);
            if (this.primaryKey)
                p_writer.writeAttribute("primaryKey", "true");
            p_writer.writeAttribute("dbType", DbTypeConverter.getDbTypeName(this.columnType));

            if (this.defaultValue != null) {
                p_writer.writeAttribute("defaultValue", XmlSerializeHelper.serializeObjectToString(this.defaultValue));
            }
            if (this.expression != null)
                p_writer.writeAttribute("expression", this.expression);
            if (!this.nullable)
                p_writer.writeAttribute("nullable", "false");
            if (this.precision != 0) {
                p_writer.writeAttribute("precision", String.valueOf(this.precision));
            }
            if (this.scale != 0)
                p_writer.writeAttribute("scale", String.valueOf(this.scale));
            if (this.readOnly)
                p_writer.writeAttribute("readOnly", "true");
            if (p_needMoreCol) {
                p_writer.writeAttribute("indexInColList", String.valueOf(this.indexInColList));
            }

            for (Map.Entry entry : getExtendedProperties().entrySet()) {
                String keyName = (String) entry.getKey();
                String keyValue = (String) entry.getValue();

                if (keyName.equalsIgnoreCase("visible")) {
                    if (!Boolean.valueOf(keyValue).booleanValue())
                        p_writer.writeAttribute("visible", keyValue);
                } else
                    p_writer.writeAttribute(keyName, keyValue);
            }
            p_writer.writeEndElement();
        } catch (Exception e) {
            throw new XmlSerializeException("DataColumn 序列化为 Xml 时发生异常。", e);
        }
    }


}
