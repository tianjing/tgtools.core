package tgtools.data;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgtools.json.JSONObject;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.xml.IXmlSerializable;
import tgtools.xml.XmlSerializeException;
import tgtools.xml.XmlSerializeHelper;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author tianjing
 */
public class DataRow implements IXmlSerializable, Cloneable, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -746855776800061785L;
    private static final DbNull CONST_DBDATA_NULL = new DbNull();
    protected DataTable table;
    private List<Object> data;

    protected DataRow(DataTable pTable) {
        this.table = pTable;
        this.data = Collections.synchronizedList(new ArrayList<Object>(this.table.getColumns().size()));

        for (int i = 0; i < this.table.getColumns().size(); i++) {
            this.data.add(CONST_DBDATA_NULL);
        }
    }

    public DataTable getTable() {
        return this.table;
    }

    public void addNullValue() {
        this.data.add(new DbNull());
    }

    public void removeData(int index) {
        this.data.remove(index);
    }

    public Object getValue(String pColumnName) {
        DataColumn column = this.table.getColumn(pColumnName);
        return DbTypeConverter.validateNullToDbNull(this.data.get(column.getIndexInColList()));
    }

    public Object getValue(int pColumnIndex) {
        return DbTypeConverter.validateNullToDbNull(this.data.get(pColumnIndex));
    }

    public boolean isNullValue(String pColumnName) {
        return getValue(pColumnName) instanceof DbNull;
    }

    public boolean isNullValue(int pColumnIndex) {
        return getValue(pColumnIndex) instanceof DbNull;
    }

    public void setValue(String pColumnName, Object pValue, boolean pIgnoreReadOnly) {
        DataColumn column = this.table.getColumn(pColumnName);
        if ((!pIgnoreReadOnly) && (column.isReadOnly())) {
            throw new DataAccessException(String.format("无法向只读字段[%1$s]设置数值。", new Object[]{pColumnName}));
        }
        Object obj = DbTypeConverter.toCommonType(pValue, column.getColumnType(), this.getTable().getBolbUseStream());
        this.data.set(column.getIndexInColList(), obj);
    }

    public void setValue(String pColumnName, Object pValue) {
        setValue(pColumnName, pValue, false);
    }

    public void setValue(int pColumnIndex, Object pValue, boolean pIgnoreReadOnly) {
        DataColumn column = this.table.getColumn(pColumnIndex);
        if ((!pIgnoreReadOnly) && (column.isReadOnly())) {
            throw new DataAccessException(String.format("无法向只读字段[%1$s]设置数值。", new Object[]{column.getColumnName()}));
        }

        this.data.set(pColumnIndex, DbTypeConverter.toCommonType(pValue, column.getColumnType()));
    }

    @Override
    public DataRow clone() {
        DataRow row = new DataRow(this.table.cloneTableStructure());
        for (int i = 0; i < this.data.size(); i++) {
            Object value = this.data.get(i);
            if (value != null) {
                if ((value instanceof DbNull)) {
                    value = null;
                } else if ((value instanceof String)) {
                    value = new String(value.toString());
                } else if ((value instanceof Date)) {
                    value = ((Date) value).clone();
                } else if ((value instanceof byte[])) {
                    byte[] org = (byte[]) value;
                    byte[] bytes = new byte[org.length];

                    System.arraycopy(org, 0, bytes, 0, org.length);
                    value = bytes;
                }
            }
            row.setValue(i, value, false);

            row.getTable().getRows().add(row);
        }

        return row;
    }

    @Override
    public boolean equals(Object pRow) {
        if ((pRow instanceof DataRow)) {
            DataRow row = (DataRow) pRow;
            if (row.table.getColumns().size() != this.table.getColumns().size()) {
                return false;
            }
            for (int i = 0; i < this.data.size(); i++) {
                if (!this.data.get(i).equals(row.data.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < this.data.size(); i++) {
            if (((this.data.get(i) instanceof Integer)) || ((this.data.get(i) instanceof String)) || ((this.data.get(i) instanceof Date))) {
                hashCode += this.data.get(i).toString().hashCode();
            }
        }
        return hashCode;
    }

    public int copyData(DataRow row) {
        for (int i = 0; i < row.getTable().getColumns().size(); i++) {
            this.table.appendColumn(row.getTable().getColumn(i));
        }
        for (int i = 0; i < row.getTable().getColumns().size(); i++) {
            boolean ro = this.table.getColumn(row.getTable().getColumnName(i)).readOnly;
            this.table.getColumn(row.getTable().getColumnName(i)).readOnly = false;
            setValue(row.getTable().getColumnName(i), row.getValue(i));
            this.table.getColumn(row.getTable().getColumnName(i)).readOnly = ro;
        }

        return this.data.size();
    }


    @Override
    public void readXml(XMLStreamReader pReader) {
        if (!StringUtil.equalsIgnoreCase(pReader.getLocalName(),"Row")) {
            throw new XmlSerializeException("无法反序列化 DataRow 对象，当前 XMLStreamReader 的游标位置有误。");
        }
        try {
            while (pReader.hasNext()) {
                pReader.nextTag();
                if (pReader.isEndElement()) {
                    break;
                }
                if (pReader.isStartElement()) {
                    String columnName = pReader.getLocalName().toUpperCase();
                    pReader.next();
                    Object value = DbTypeConverter.toCommonType(XmlSerializeHelper.readText(pReader), this.table.getColumn(columnName).getColumnType());

                    setValue(columnName, value, true);
                }
            }
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("DataRow 反序列化时发生异常。", e);
        }
    }

    @Override
    public void writeXml(XMLStreamWriter pWriter) {
        try {
            pWriter.writeStartElement("Row");
            for (int i = 0; i < this.table.getColumns().size(); i++) {
                DataColumn column = this.table.getColumn(i);

                if (!column.isCalculated()) {
                    Object value = DbTypeConverter.validateDbNullToNull(this.data.get(column.getIndexInColList()));

                    if (value != null) {
                        pWriter.writeStartElement(column.getColumnName());
                        pWriter.writeCData(XmlSerializeHelper.serializeObjectToString(value));

                        pWriter.writeEndElement();
                    }
                }
            }
            pWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XmlSerializeException("DataRow 序列化为 Xml 时发生异常。", e);
        }
    }

    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public String toJson() {
        return toJson(true);
    }

    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public String toJson(boolean pIgnoreNull) {
        return toJson(pIgnoreNull, false);
    }

    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public String toJson(boolean pIgnoreNull, boolean pUseLower) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        int ide = 0;
        for (int j = 0; j < this.getTable().getColumns().size(); j++) {
            DataColumn column = this.getTable().getColumn(j);
            int datatype = column.getColumnType();
            if (datatype == java.sql.Types.BLOB) {
                continue;
            }
            String name = this.getTable().getColumnName(column.getColumnName());
            Object value = this.getValue(column.getColumnName());
            try {
                if (pUseLower) {
                    name = name.toLowerCase();
                }
                if (ide > 0) {

                    sb.append(",\"" + name + "\":"
                            + getJsonValue(value, datatype, pIgnoreNull));
                } else {
                    sb.append("\"" + name + "\":"
                            + getJsonValue(value, datatype, pIgnoreNull));
                }
            } catch (Exception e) {
                LogHelper.error("", "第" + String.valueOf(this.getTable().indexOfRow(this)) + "行，第" + String.valueOf(ide + 1) + "列，列名：" + name + "类型：" + String.valueOf(datatype) + "出现错误！", "table.tojson", e);
            }
            ide++;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 将值转换成json的值类型
     *
     * @param pValue
     * @param pValueType
     * @param pIgnoreNull
     * @return
     */
    private String getJsonValue(Object pValue, int pValueType, boolean pIgnoreNull) {
        if (pValue == null || pValue instanceof DbNull) {
            if (pIgnoreNull) {
                return "\"\"";
            } else {
                return "null";
            }
        }
        switch (pValueType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.INTEGER:
            case Types.BOOLEAN:
                return pValue.toString();

            default:
                return "\"" + StringUtil.convertJson(pValue.toString()) + "\"";

        }
    }


    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public JSONObject toJSONObject() {
        return toJSONObject(true);
    }

    /**
     * 转换成JSONObject格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public JSONObject toJSONObject(boolean pIgnoreNull) {
        return toJSONObject(pIgnoreNull, false);
    }

    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public JSONObject toJSONObject(boolean pIgnoreNull, boolean pUseLower) {
        JSONObject json = new JSONObject();
        int ide = 0;
        for (int j = 0; j < this.getTable().getColumns().size(); j++) {
            DataColumn column = this.getTable().getColumn(j);
            int datatype = column.getColumnType();
            if (datatype == java.sql.Types.BLOB) {
                continue;
            }
            String name = this.getTable().getColumnName(column.getColumnName());
            Object value = this.getValue(column.getColumnName());
            try {
                if (pUseLower) {
                    name = name.toLowerCase();
                }
                json.put(name, getJSONObjectValue(value, datatype, pIgnoreNull));

            } catch (Exception e) {
                LogHelper.error("", "第" + String.valueOf(this.getTable().indexOfRow(this)) + "行，第" + String.valueOf(ide + 1) + "列，列名：" + name + "类型：" + String.valueOf(datatype) + "出现错误！", "table.tojson", e);
            }

        }

        return json;
    }

    /**
     * 将值转换成json的值类型
     *
     * @param pValue
     * @param pValueType
     * @param pIgnoreNull
     * @return
     */
    private Object getJSONObjectValue(Object pValue, int pValueType, boolean pIgnoreNull) {
        if (pValue == null || pValue instanceof DbNull) {
            if (pIgnoreNull) {
                return "";
            } else {
                return null;
            }
        }
        switch (pValueType) {
            case Types.NUMERIC:
            case Types.DECIMAL:
            case Types.INTEGER:
            case Types.BOOLEAN:
                return pValue;

            default:
                return pValue;
        }
    }



    /**
     * 转换成json格式,并忽略null 参看 toJson(true)
     *
     * @return
     */
    public ObjectNode toObjectNode() {
        return toObjectNode(true);
    }

    /**
     * 转换成JSONObject格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @return
     */
    public ObjectNode toObjectNode(boolean pIgnoreNull) {
        return toObjectNode(pIgnoreNull, false);
    }
    /**
     * 转换成json格式
     *
     * @param pIgnoreNull 为true时null为空字符串。为false时返回null
     * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
     * @return
     */
    public ObjectNode toObjectNode(boolean pIgnoreNull, boolean pUseLower) {
        ObjectNode json = tgtools.util.JsonParseHelper.createObjectNode();
        int ide = 0;
        for (int j = 0; j < this.getTable().getColumns().size(); j++) {
            DataColumn column = this.getTable().getColumn(j);
            int datatype = column.getColumnType();
            if (datatype == java.sql.Types.BLOB) {
                continue;
            }
            String name = this.getTable().getColumnName(column.getColumnName());
            Object value = this.getValue(column.getColumnName());
            try {
                if (pUseLower) {
                    name = name.toLowerCase();
                }
                json.putPOJO(name, getJSONObjectValue(value, datatype, pIgnoreNull));
            } catch (Exception e) {
                LogHelper.error("", "第" + String.valueOf(this.getTable().indexOfRow(this)) + "行，第" + String.valueOf(ide + 1) + "列，列名：" + name + "类型：" + String.valueOf(datatype) + "出现错误！", "table.tojson", e);
            }
        }
        return json;
    }


}
