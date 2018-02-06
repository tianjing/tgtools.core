package tgtools.data;

import java.io.Serializable;

import tgtools.util.StringUtil;

public class DbNull implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1828151024542016621L;

	@Override
	public boolean equals(Object p_value) {
		return p_value instanceof DbNull;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	@Override
	public String toString()
	{
		return StringUtil.EMPTY_STRING;
	}
}
