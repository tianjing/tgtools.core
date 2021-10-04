package tgtools.data;

import tgtools.util.StringUtil;
import tgtools.xml.IXmlSerializable;
import tgtools.xml.XmlSerializeException;
import tgtools.xml.XmlSerializeHelper;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tianjing
 */
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

    protected DataColumn(DataTable pTable, String pColumnName) {
        this.table = pTable;

        this.columnName = pColumnName;
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

    public void setColumnName(String pColumnName) {
        this.columnName = pColumnName;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String pCaption) {
        this.caption = pCaption;
    }

    public int getColumnType() {
        return this.columnType;
    }

    public void setColumnType(int pColumnType) {
        this.columnType = pColumnType;
    }

    public int getPrecision() {
        return this.precision;
    }

    public void setPrecision(int pPrecision) {
        this.precision = pPrecision;
    }

    public int getScale() {
        return this.scale;
    }

    public void setScale(int pScale) {
        this.scale = pScale;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean pNullable) {
        this.nullable = pNullable;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setCaseSensitive(boolean pIndex) {
        this.caseSensitive = pIndex;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean pReadOnly) {
        this.readOnly = pReadOnly;
    }

    public boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(boolean pPrimaryKey) {
        this.primaryKey = pPrimaryKey;
        if (this.primaryKey) {
            this.nullable = false;
        }
    }

    public HashMap<String, String> getExtendedProperties() {
        return this.extendedProperties;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(Object pValue) {
        this.defaultValue = pValue;
    }

    public boolean isCalculated() {
        return this.expression != null;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String pExpression) {
        this.expression = pExpression;
        this.readOnly = true;
    }

    public int getIndexInColList() {
        return this.indexInColList;
    }

    public void setIndexInColList(int indexInColList) {
        this.indexInColList = indexInColList;
    }

    @Override
    public boolean equals(Object pColumn) {
        if ((pColumn instanceof DataColumn)) {
            DataColumn column = (DataColumn) pColumn;
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
    public void readXml(XMLStreamReader pReader) {
        if (!StringUtil.equalsIgnoreCase(pReader.getLocalName(), "Column")) {
            throw new XmlSerializeException("无法反序列化 DataColumn 对象，当前 XMLStreamReader 的游标位置有误。");
        }

        for (int i = 0; i < pReader.getAttributeCount(); i++) {
            String attrName = pReader.getAttributeName(i).toString();
            String attrValue = pReader.getAttributeValue(i);
            if (StringUtil.equalsIgnoreCase(attrName, "columnName")) {
                this.columnName = attrValue;
            } else if (StringUtil.equalsIgnoreCase(attrName, "caption")) {
                this.caption = attrValue;
            } else if (StringUtil.equalsIgnoreCase(attrName, "primaryKey")) {
                this.primaryKey = Boolean.valueOf(attrValue).booleanValue();
            } else if (StringUtil.equalsIgnoreCase(attrName, "dbType")) {
                this.columnType = DbTypeConverter.getDbType(attrValue);
            } else if (StringUtil.equalsIgnoreCase(attrName, "expression")) {
                this.expression = attrValue;
            } else if (StringUtil.equalsIgnoreCase(attrName, "nullable")) {
                this.nullable = Boolean.valueOf(attrValue).booleanValue();
            } else if (StringUtil.equalsIgnoreCase(attrName, "precision")) {
                this.precision = Integer.valueOf(attrValue).intValue();
            } else if (StringUtil.equalsIgnoreCase(attrName, "scale")) {
                this.scale = Integer.valueOf(attrValue).intValue();
            } else if (StringUtil.equalsIgnoreCase(attrName, "readOnly")) {
                this.readOnly = Boolean.valueOf(attrValue).booleanValue();
            } else if (StringUtil.equalsIgnoreCase(attrName, "indexInColList")) {
                this.indexInColList = Integer.valueOf(attrValue).intValue();
            } else if (StringUtil.equalsIgnoreCase(attrName, "visible")) {
                getExtendedProperties().put("visible", attrValue);
            } else {
                getExtendedProperties().put(attrName, attrValue);
            }
        }

        for (int i = 0; i < pReader.getAttributeCount(); i++) {
            String attrName = pReader.getAttributeName(i).toString();
            String attrValue = pReader.getAttributeValue(i);
            if (StringUtil.equalsIgnoreCase(attrName, "defaultValue")) {
                this.defaultValue = DbTypeConverter.toCommonType(attrValue, getColumnType());

                break;
            }
        }
        try {
            pReader.nextTag();
        } catch (javax.xml.stream.XMLStreamException e) {
            throw new XmlSerializeException("DataColumn 反序列化时发生异常。", e);
        }
    }

    @Override
    public void writeXml(XMLStreamWriter pWriter) {
        writeXmlImpl(pWriter, true);
    }

    @SuppressWarnings("rawtypes")
    public void writeXmlImpl(XMLStreamWriter pWriter, boolean pNeedMoreCol) {
        try {
            pWriter.writeStartElement("Column");
            pWriter.writeAttribute("columnName", this.columnName);
            if (this.caption != null) {
                pWriter.writeAttribute("caption", this.caption);
            }
            if (this.primaryKey) {
                pWriter.writeAttribute("primaryKey", "true");
                pWriter.writeAttribute("dbType", DbTypeConverter.getDbTypeName(this.columnType));
            }
            if (this.defaultValue != null) {
                pWriter.writeAttribute("defaultValue", XmlSerializeHelper.serializeObjectToString(this.defaultValue));
            }
            if (this.expression != null) {
                pWriter.writeAttribute("expression", this.expression);
            }
            if (!this.nullable) {
                pWriter.writeAttribute("nullable", "false");
            }
            if (this.precision != 0) {
                pWriter.writeAttribute("precision", String.valueOf(this.precision));
            }
            if (this.scale != 0) {
                pWriter.writeAttribute("scale", String.valueOf(this.scale));
            }
            if (this.readOnly) {
                pWriter.writeAttribute("readOnly", "true");
            }
            if (pNeedMoreCol) {
                pWriter.writeAttribute("indexInColList", String.valueOf(this.indexInColList));
            }

            for (Map.Entry entry : getExtendedProperties().entrySet()) {
                String keyName = (String) entry.getKey();
                String keyValue = (String) entry.getValue();

                if (StringUtil.equalsIgnoreCase(keyName,"visible")) {
                    if (!Boolean.valueOf(keyValue).booleanValue()) {
                        pWriter.writeAttribute("visible", keyValue);
                    }
                } else {
                    pWriter.writeAttribute(keyName, keyValue);
                }
            }
            pWriter.writeEndElement();
        } catch (Exception e) {
            throw new XmlSerializeException("DataColumn 序列化为 Xml 时发生异常。", e);
        }
    }


}
