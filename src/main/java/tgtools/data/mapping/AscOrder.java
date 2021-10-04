package tgtools.data.mapping;

/**
 *
 * @author tianjing
 */
public class AscOrder extends Order
{
  public AscOrder(String pFieldName)
  {
    super(pFieldName);
  }
  @Override
  public String toSQL()
  {
    return this.fieldName + " asc";
  }
}