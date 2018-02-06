package tgtools.data.mapping;

import tgtools.data.DataRow;


public class DescOrder extends Order
{
  public DescOrder(String p_fieldName)
  {
    super(p_fieldName);
  }
  @Override
  public int compare(DataRow p_row1, DataRow p_row2)
  {
    return -super.compare(p_row1, p_row2);
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