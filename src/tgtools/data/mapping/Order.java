package tgtools.data.mapping;

import tgtools.data.DataRow;
import tgtools.exceptions.APPRuntimeException;


public abstract class Order
{
  protected String fieldName;

  public Order(String p_fieldName)
  {
    this.fieldName = p_fieldName.toUpperCase();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
public int compare(DataRow p_row1, DataRow p_row2)
  {
    Object value1 = p_row1.getValue(this.fieldName);
    Object value2 = p_row2.getValue(this.fieldName);

    if (value1 == null) {
      return -1;
    }
    if (value2 == null) {
      return 1;
    }

    if (((value1 instanceof Comparable)) && ((value2 instanceof Comparable))) {
      return ((Comparable)value1).compareTo(value2);
    }
    throw new APPRuntimeException(String.format("字段[%1$s]所包含的对象未实现Comparable接口，无法进行排序。", new Object[] { this.fieldName }));
  }

  public abstract String toSQL();

  public static AscOrder asc(String p_fieldName)
  {
    return new AscOrder(p_fieldName);
  }

  public static DescOrder desc(String p_fieldName)
  {
    return new DescOrder(p_fieldName);
  }
}
