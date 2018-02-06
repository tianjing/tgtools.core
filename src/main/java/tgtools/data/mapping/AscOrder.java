package tgtools.data.mapping;

public class AscOrder extends Order
{
  public AscOrder(String p_fieldName)
  {
    super(p_fieldName);
  }
  @Override
  public String toSQL()
  {
    return this.fieldName + " asc";
  }
}

/* Location:           C:\Users\TianJing\Desktop\pi3000\nariis.pi3000.framework.jar
 * Qualified Name:     nariis.pi3000.framework.ormapping.AscOrder
 * JD-Core Version:    0.6.2
 */