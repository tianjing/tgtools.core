package tgtools.data.mapping;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;


public class NotCondition extends Condition
{
  private Condition innerCondition;

  public NotCondition(Condition p_condition)
  {
    this.innerCondition = p_condition;
  }
  @Override
  public boolean isValid(DataRow p_row)
  {
    return !this.innerCondition.isValid(p_row);
  }
  @Override
  public String toSQL(DataParameterCollection p_params)
  {
    String sql = this.innerCondition.toSQL(p_params);
    if (sql.equals("")) return "";
    return "not " + sql;
  }
}
