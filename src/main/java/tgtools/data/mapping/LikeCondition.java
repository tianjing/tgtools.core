package tgtools.data.mapping;

import java.util.regex.Pattern;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;

public class LikeCondition extends Condition
{
  private String fieldName;
  private String likeExpression;
  private Pattern likeExpressionPattern;

  private Pattern getLikeExpressionPattern(String p_likeExpression)
  {
    if (this.likeExpressionPattern == null)
    {
      this.likeExpressionPattern = Pattern.compile(p_likeExpression.replaceAll("%", ".*").replaceAll("_", "."));
    }
    return this.likeExpressionPattern;
  }

  public LikeCondition(String p_fieldName, String p_expression)
  {
    this.fieldName = p_fieldName;
    this.likeExpression = p_expression;
    this.likeExpressionPattern = null;
  }
  @Override
  public boolean isValid(DataRow p_row)
  {
    Object fv = p_row.getValue(this.fieldName);
    if (fv == null) return false;
    return getLikeExpressionPattern(this.likeExpression).matcher(fv.toString()).matches();
  }
  @Override
  public String toSQL(DataParameterCollection p_params)
  {
    return String.format("%1$s like '%2$s'", new Object[] { this.fieldName, this.likeExpression });
  }
}
