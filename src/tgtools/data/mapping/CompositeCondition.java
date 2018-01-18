package tgtools.data.mapping;

import java.util.Collections;
import java.util.List;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;

public abstract class CompositeCondition extends Condition
{
  protected ConditionCollection conditions;

  public CompositeCondition()
  {
    this.conditions = new ConditionCollection();
  }

  public void add(Condition p_condition)
  {
    this.conditions.add(p_condition);
  }

  public List<Condition> getConditions()
  {
    return Collections.unmodifiableList(this.conditions);
  }
  @Override
  public abstract boolean isValid(DataRow paramDataRow);
  @Override
  public abstract String toSQL(DataParameterCollection paramDataParameterCollection);
}
