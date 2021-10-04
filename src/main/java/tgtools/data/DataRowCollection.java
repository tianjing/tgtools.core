package tgtools.data;

import java.util.ArrayList;

/**
 *
 * @author tianjing
 */
public class DataRowCollection extends ArrayList<DataRow>{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 4921538670021716105L;

	public DataRow elementAt(int index)
	  {
	    return (DataRow)super.get(index);
	  }

	/**
	 * 转换成json格式
	 *
	 * @param pIgnoreNull 为true时null为空字符串。为false时返回null
	 * @return
	 */
	public String toJson(boolean pIgnoreNull) {
		return toJson(pIgnoreNull, false);
	}
	/**
	 * 转换成json格式
	 *
	 * @param pIgnoreNull 为true时null为空字符串。为false时返回null
	 * @param pUseLower   列名是否小写，true:列名大写转小写，false：保持列名
	 * @return
	 */
	public String toJson(boolean pIgnoreNull, boolean pUseLower) {
		StringBuilder sb = new StringBuilder();
		int count = this.size();
		for (int i = 0; i < count; i++) {
			DataRow row = this.get(i);
			sb.append(row.toJson(pIgnoreNull,pUseLower));
			if (i != count - 1) {
				sb.append(",");
			}
		}
		return "[" + sb.toString() + "]";
	}

	/**
	 * 转换成json格式,并忽略null 参看 toJson(true)
	 *
	 * @return
	 */
	public String toJson() {
		return toJson(true);
	}
}
