package tgtools.data.mapping;

import java.util.regex.Pattern;

import tgtools.data.DataParameterCollection;
import tgtools.data.DataRow;
/**
 * @author tianjing
 */
public class LikeCondition extends Condition
{
  private String fieldName;
  private String likeExpression;
  private Pattern likeExpressionPattern;

  private Pattern getLikeExpressionPattern(String pLikeExpression)
  {
    if (this.likeExpressionPattern == null)
    {
      this.likeExpressionPattern = Pattern.compile(pLikeExpression.replaceAll("%", ".*").replaceAll("_", "."));
    }
    return this.likeExpressionPattern;
  }

  public LikeCondition(String pFieldName, String pExpression)
  {
    this.fieldName = pFieldName;
    this.likeExpression = pExpression;
    this.likeExpressionPattern = null;
  }
  @Override
  public boolean isValid(DataRow pRow)
  {
    Object fv = pRow.getValue(this.fieldName);
    if (fv == null) {
      return false;
    }
    return getLikeExpressionPattern(this.likeExpression).matcher(fv.toString()).matches();
  }
  @Override
  public String toSQL(DataParameterCollection pParams)
  {
    return String.format("%1$s like '%2$s'", new Object[] { this.fieldName, this.likeExpression });
  }
}
