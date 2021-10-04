package tgtools.data.mapping;

import tgtools.data.DataRow;
import tgtools.exceptions.APPRuntimeException;

/**
 * @author tianjing
 */
public abstract class Order
{
  protected String fieldName;

  public Order(String pFieldName)
  {
    this.fieldName = pFieldName.toUpperCase();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
public int compare(DataRow pRow1, DataRow pRow2)
  {
    Object value1 = pRow1.getValue(this.fieldName);
    Object value2 = pRow2.getValue(this.fieldName);

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

  /**
   *  toSQL
   * @return
   */
  public abstract String toSQL();

  /**
   *  asc
   * @param pFieldName
   * @return
   */
  public static AscOrder asc(String pFieldName)
  {
    return new AscOrder(pFieldName);
  }

  /**
   * desc
   * @param pFieldName
   * @return
   */
  public static DescOrder desc(String pFieldName)
  {
    return new DescOrder(pFieldName);
  }
}
