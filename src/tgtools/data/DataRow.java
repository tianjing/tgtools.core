package tgtools.data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

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
	    DataColumn column = this.table.getColumn(p_columnName.toUpperCase());
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
}
