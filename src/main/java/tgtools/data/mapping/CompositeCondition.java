package tgtools.data.mapping;

import java.util.Collections;
import java.util.List;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;

/**
 *
 * @author tianjing
 */
public abstract class CompositeCondition extends Condition
{
  protected ConditionCollection conditions;

  public CompositeCondition()
  {
    this.conditions = new ConditionCollection();
  }

  public void add(Condition pCondition)
  {
    this.conditions.add(pCondition);
  }

  public List<Condition> getConditions()
  {
    return Collections.unmodifiableList(this.conditions);
  }

  /**
   * isValid
   * @param pParamDataRow
   * @return
   */
  @Override
  public abstract boolean isValid(DataRow pParamDataRow);

  /**
   * toSQL
   * @param pParamDataParameterCollection
   * @return
   */
  @Override
  public abstract String toSQL(DataParameterCollection pParamDataParameterCollection);
}
