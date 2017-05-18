package tgtools.data;
import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.xml.IXmlSerializable;
import tgtools.xml.XmlSerializeException;
import tgtools.xml.XmlSerializeHelper;
public class DataRow  implements IXmlSerializable, Cloneable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -746855776800061785L;
	protected DataTable table;
	  private List<Object> data;
	  private static final DbNull CONST_DBDATA_NULL = new DbNull();

	  protected DataRow(DataTable p_table)
	  {
	    this.table = p_table;
	    this.data = Collections.synchronizedList(new ArrayList<Object>(this.table.getColumns().size()));

	    for (int i = 0; i < this.table.getColumns().size(); i++)
	      this.data.add(CONST_DBDATA_NULL);
	  }

	  public DataTable getTable()
	  {
	    return this.table;
	  }

	  public void addNullValue() {
	    this.data.add(new DbNull());
	  }

	  public void removeData(int index) {
	    this.data.remove(index);
	  }

	  public Object getValue(String p_columnName)
	  {
	    DataColumn column = this.table.getColumn(p_columnName);
	    return DbTypeConverter.validateNullToDbNull(this.data.get(column.getIndexInColList()));
	  }
	  public Object getValue(int p_columnIndex)
	  {
	   // DataColumn column = this.table.getColumn(p_columnIndex);
	    return DbTypeConverter.validateNullToDbNull(this.data.get(p_columnIndex));
	  }
	  public boolean isNullValue(String p_columnName)
	  {
		   return getValue(p_columnName) instanceof DbNull;
	  }
	  public boolean isNullValue(int p_columnIndex)
	  {
		  return getValue(p_columnIndex) instanceof DbNull;
	  }
	  public void setValue(String p_columnName, Object p_value, boolean p_ignoreReadOnly)
	  {
	    DataColumn column = this.table.getColumn(p_columnName);
	    if ((!p_ignoreReadOnly) && (column.isReadOnly())) {
	      throw new DataAccessException(String.format("无法向只读字段[%1$s]设置数值。", new Object[] { p_columnName }));
	    }
	    Object obj=DbTypeConverter.toCommonType(p_value, column.getColumnType());
	    this.data.set(column.getIndexInColList(), obj);
	  }

	  public void setValue(String p_columnName, Object p_value)
	  {
	    setValue(p_columnName, p_value, false);
	  }

	  public void setValue(int p_columnIndex, Object p_value, boolean p_ignoreReadOnly)
	  {
	    DataColumn column = this.table.getColumn(p_columnIndex);
	    if ((!p_ignoreReadOnly) && (column.isReadOnly())) {
	      throw new DataAccessException(String.format("无法向只读字段[%1$s]设置数值。", new Object[] { column.getColumnName() }));
	    }

	    this.data.set(p_columnIndex, DbTypeConverter.toCommonType(p_value, column.getColumnType()));
	  }

	  public DataRow clone()
	  {
	    DataRow row = new DataRow(this.table.cloneTableStructure());
	    for (int i = 0; i < this.data.size(); i++) {
	      Object value = this.data.get(i);
	      if (value != null) {
	        if ((value instanceof DbNull)) {
	          value = null;
	        } else if ((value instanceof String)) {
	          value = new String(value.toString());
	        } else if ((value instanceof Date)) {
	          value = ((Date)value).clone();
	        } else if ((value instanceof byte[])) {
	          byte[] org = (byte[])value;
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

	  public boolean equals(Object p_row)
	  {
	    if ((p_row instanceof DataRow)) {
	      DataRow row = (DataRow)p_row;
	      if (row.table.getColumns().size() != this.table.getColumns().size()) {
	        return false;
	      }
	      for (int i = 0; i < this.data.size(); i++) {
	        if (!this.data.get(i).equals(row.data.get(i)))
	          return false;
	      }
	      return true;
	    }
	    return false;
	  }

	  public int hashCode()
	  {
	    int hashCode = 0;
	    for (int i = 0; i < this.data.size(); i++) {
	      if (((this.data.get(i) instanceof Integer)) || ((this.data.get(i) instanceof String)) || ((this.data.get(i) instanceof Date)))
	      {
	        hashCode += this.data.get(i).toString().hashCode();
	      }
	    }
	    return hashCode;
	  }

	  public int copyData(DataRow row)
	  {
	    for (int i = 0; i < row.getTable().getColumns().size(); i++)
	      this.table.appendColumn(row.getTable().getColumn(i));
	    for (int i = 0; i < row.getTable().getColumns().size(); i++)
	    {
	      boolean ro = this.table.getColumn(row.getTable().getColumnName(i)).readOnly;
	      this.table.getColumn(row.getTable().getColumnName(i)).readOnly = false;
	      setValue(row.getTable().getColumnName(i), row.getValue(i));
	      this.table.getColumn(row.getTable().getColumnName(i)).readOnly = ro;
	    }

	    return this.data.size();
	  }
	  
	  
	  
	  
	  
	  
	  public void readXml(XMLStreamReader p_reader)
	  {
	    if (!p_reader.getLocalName().equalsIgnoreCase("Row")) {
	      throw new XmlSerializeException("无法反序列化 DataRow 对象，当前 XMLStreamReader 的游标位置有误。");
	    }
	    try
	    {
	      while (p_reader.hasNext()) {
	        p_reader.nextTag();
	        if (p_reader.isEndElement())
	          break;
	        if (p_reader.isStartElement())
	        {
	          String columnName = p_reader.getLocalName().toUpperCase();
	          p_reader.next();
	          Object value = DbTypeConverter.toCommonType(XmlSerializeHelper.readText(p_reader), this.table.getColumn(columnName).getColumnType());

	          setValue(columnName, value, true);
	        }
	      }
	    } catch (XMLStreamException e) {
	      throw new XmlSerializeException("DataRow 反序列化时发生异常。", e);
	    }
	  }

	  public void writeXml(XMLStreamWriter p_writer)
	  {
	    try
	    {
	      p_writer.writeStartElement("Row");
	      for (int i = 0; i < this.table.getColumns().size(); i++) {
	        DataColumn column = this.table.getColumn(i);

	        if (!column.isCalculated())
	        {
	          Object value = DbTypeConverter.validateDbNullToNull(this.data.get(column.getIndexInColList()));

	          if (value != null)
	          {
	            p_writer.writeStartElement(column.getColumnName());
	            p_writer.writeCData(XmlSerializeHelper.serializeObjectToString(value));

	            p_writer.writeEndElement();
	          }
	        }
	      }
	      p_writer.writeEndElement();
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
	 * @param p_IgnoreNull 为true时null为空字符串。为false时返回null
	 * @return
	 */
	public String toJson(boolean p_IgnoreNull) {
		return toJson(p_IgnoreNull, false);
	}

	/**
	 * 转换成json格式
	 *
	 * @param p_IgnoreNull 为true时null为空字符串。为false时返回null
	 * @param p_UseLower   列名是否小写，true:列名大写转小写，false：保持列名
	 * @return
	 */
	public String toJson(boolean p_IgnoreNull, boolean p_UseLower)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		int ide = 0;
		for (int j=0;j<this.getTable().getColumns().size();j++) {
			DataColumn column =this.getTable().getColumn(j);
			int datatype = column.getColumnType();
			if (datatype == java.sql.Types.BLOB) {
				continue;
			}
			String name = this.getTable().getColumnName(column.getColumnName());
			Object value = this.getValue(column.getColumnName());
			try {
				if (p_UseLower) {
					name = name.toLowerCase();
				}
				if (ide > 0) {

					sb.append(",\"" + name + "\":"
							+ getJsonValue(value, datatype, p_IgnoreNull));
				} else {
					sb.append("\"" + name + "\":"
							+ getJsonValue(value, datatype, p_IgnoreNull));
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
	 * @param p_Value
	 * @param p_ValueType
	 * @param p_IgnoreNull
	 * @return
	 */
	private String getJsonValue(Object p_Value, int p_ValueType, boolean p_IgnoreNull) {
		if (p_Value == null || p_Value instanceof DbNull) {
			if (p_IgnoreNull) {
				return "\"\"";
			} else
				return "null";
		}
		switch (p_ValueType) {
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.INTEGER:
			case Types.BOOLEAN:
				return p_Value.toString();

			default:
				return "\"" + StringUtil.convertJson(p_Value.toString()) + "\"";

		}
	}
}
