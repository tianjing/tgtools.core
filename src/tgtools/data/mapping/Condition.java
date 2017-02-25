package tgtools.data.mapping;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;


public abstract class Condition
{
  public abstract boolean isValid(DataRow paramDataRow);

  public abstract String toSQL(DataParameterCollection paramDataParameterCollection);

  public static AndCondition and(Condition[] p_conditions)
  {
    AndCondition andCondition = new AndCondition();
    for (Condition condition : p_conditions)
    {
      andCondition.add(condition);
    }
    return andCondition;
  }

  public static OrCondition or(Condition[] p_conditions)
  {
    OrCondition orCondition = new OrCondition();
    for (Condition condition : p_conditions)
    {
      orCondition.add(condition);
    }
    return orCondition;
  }

  public static NotCondition not(Condition p_condition)
  {
    return new NotCondition(p_condition);
  }

  public static EqualCondition equal(String p_fieldName, Object p_fieldValue, int p_dataType)
  {
    return new EqualCondition(p_fieldName, p_fieldValue, p_dataType);
  }

  public static LikeCondition like(String p_fieldName, String p_expression)
  {
    return new LikeCondition(p_fieldName, p_expression);
  }
}

