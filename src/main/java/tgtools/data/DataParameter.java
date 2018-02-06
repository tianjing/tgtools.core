package tgtools.data;

import java.io.Serializable;


public class DataParameter
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = -3000305278740359158L;
  public static final String ELEMENT_PARAM = "DataParameter";
  public static final String ATTR_NAME = "name";
  public static final String ATTR_DBTYPE = "dbType";
  public static final String ATTR_DIRECTION = "direction";
  public static final String ATTR_SIZE = "size";
  public static final String ELEMENT_VALUE = "Value";
  private String name;
  private Object value;
  private int dataType;
  private DataParameterDirection direction;
  private int size;

  public DataParameter()
  {
    this(null, 0);
  }

  public DataParameter(Object p_value, int p_dataType)
  {
    setName(null);
    setValue(p_value);
    setDataType(p_dataType);
    setDirection(DataParameterDirection.In);
    setSize(-1);
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String p_name)
  {
    this.name = p_name;
  }

  public Object getValue()
  {
    return this.value;
  }

  public void setValue(Object p_value)
  {
    this.value = p_value;
  }

  public int getDataType()
  {
    return this.dataType;
  }

  public void setDataType(int p_dataType)
  {
    this.dataType = p_dataType;
  }

  public DataParameterDirection getDirection()
  {
    return this.direction;
  }

  public void setDirection(DataParameterDirection p_direction)
  {
    this.direction = p_direction;
  }

  public int getSize()
  {
    return this.size;
  }

  public void setSize(int p_size)
  {
    this.size = p_size;
  }

  public DataParameter clone()
  {
    DataParameter result = new DataParameter();
    result.setDataType(getDataType());
    result.setDirection(getDirection());
    result.setName(getName());
    result.setSize(getSize());
    result.setValue(getValue());
    return result;
  }

}
