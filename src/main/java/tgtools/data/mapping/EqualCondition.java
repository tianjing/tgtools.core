package tgtools.data.mapping;

import tgtools.data.DataParameter;
import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;
import tgtools.data.DbNull;
import tgtools.data.DbTypeConverter;

/**
 * @author tianjing
 */
public class EqualCondition extends Condition
{
  private String fieldName;
  private Object fieldValue;
  private int dataType;

  public EqualCondition(String pFieldName, Object pFieldValue, int pDataType)
  {
    this.fieldName = pFieldName;
    this.dataType = pDataType;
    this.fieldValue = DbTypeConverter.toCommonType(pFieldValue, this.dataType);
  }
  @Override
  public boolean isValid(DataRow pRow)
  {
    if ((this.fieldValue == null) || ((this.fieldValue instanceof DbNull)))
    {
      return pRow.getValue(this.fieldName) == null;
    }
    return this.fieldValue.equals(pRow.getValue(this.fieldName));
  }
  @Override
  public String toSQL(DataParameterCollection pParams)
  {
    if ((this.fieldValue == null) || ((this.fieldValue instanceof DbNull)))
    {
      return String.format("%1$s is null", new Object[] { this.fieldName });
    }

    DataParameter param = new DataParameter();
    param.setValue(this.fieldValue);
    param.setDataType(this.dataType);
    pParams.add(param);
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