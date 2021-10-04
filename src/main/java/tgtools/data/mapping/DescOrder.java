package tgtools.data.mapping;

import tgtools.data.DataRow;

/**
 * @author tianjing
 */
public class DescOrder extends Order
{
  public DescOrder(String pFieldName)
  {
    super(pFieldName);
  }
  @Override
  public int compare(DataRow pRow1, DataRow pRow2)
  {
    return -super.compare(pRow1, pRow2);
  }
  @Override
  public String toSQL()
  {
    return this.fieldName + " desc";
  }
}

/* Location:           C:\Users\TianJing\Desktop\pi3000\nariis.pi3000.framework.jar
 * Qualified Name:     nariis.pi3000.framework.ormapping.DescOrder
 * JD-Core Version:    0.6.2
 */