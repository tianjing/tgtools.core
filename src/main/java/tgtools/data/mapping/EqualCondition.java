package tgtools.data.mapping;

import tgtools.data.DataParameter;
import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;
import tgtools.data.DbNull;
import tgtools.data.DbTypeConverter;


public class EqualCondition extends Condition
{
  private String fieldName;
  private Object fieldValue;
  private int dataType;

  public EqualCondition(String p_fieldName, Object p_fieldValue, int p_dataType)
  {
    this.fieldName = p_fieldName;
    this.dataType = p_dataType;
    this.fieldValue = DbTypeConverter.toCommonType(p_fieldValue, this.dataType);
  }
  @Override
  public boolean isValid(DataRow p_row)
  {
    if ((this.fieldValue == null) || ((this.fieldValue instanceof DbNull)))
    {
      return p_row.getValue(this.fieldName) == null;
    }
    return this.fieldValue.equals(p_row.getValue(this.fieldName));
  }
  @Override
  public String toSQL(DataParameterCollection p_params)
  {
    if ((this.fieldValue == null) || ((this.fieldValue instanceof DbNull)))
    {
      return String.format("%1$s is null", new Object[] { this.fieldName });
    }

    DataParameter param = new DataParameter();
    param.setValue(this.fieldValue);
    param.setDataType(this.dataType);
    p_params.add(param);
    return String.format("%1$s = ?", new Object[] { this.fieldName });
  }

  public String getFieldName()
  {
    return this.fieldName;
  }

  public int getDataType()
  {
    return this.dataType;
  }

  public Object getFieldValue()
  {
    return this.fieldValue;
  }
}