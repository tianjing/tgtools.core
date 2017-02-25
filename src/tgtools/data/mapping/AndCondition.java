package tgtools.data.mapping;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;


public class AndCondition extends CompositeCondition
{
  public boolean isValid(DataRow p_row)
  {
    for (Condition condition : this.conditions)
    {
      if (!condition.isValid(p_row)) return false;
    }
    return true;
  }

  public String toSQL(DataParameterCollection p_params)
  {
    if (this.conditions.size() == 0) return "";
    StringBuffer sql = new StringBuffer();
    sql.append("(");
    for (Condition condition : this.conditions)
    {
      if (!sql.toString().equals("("))
      {
        sql.append(" and ");
      }
      sql.append(condition.toSQL(p_params));
    }
    sql.append(")");
    return sql.toString();
  }
}
