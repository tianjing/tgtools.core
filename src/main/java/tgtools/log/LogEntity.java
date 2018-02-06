package tgtools.log;

import tgtools.util.StringUtil;

public class LogEntity {
	private String username;
	private String logtime;
	private String logtype;
	private String biztype;
	private String logcontent;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLogtime() {
		return logtime;
	}

	public void setLogtime(String logtime) {
		this.logtime = logtime;
	}

	public String getLogtype() {
		return logtype;
	}

	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}

	public String getBiztype() {
		return biztype;
	}

	public void setBiztype(String biztype) {
		this.biztype = biztype;
	}

	public String getLogcontent() {
		return logcontent;
	}

	public void setLogcontent(String logcontent) {
		this.logcontent = logcontent;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (StringUtil.isNotEmpty(username)) {
			sb.append("用户:" + username + ";");
		}
		if (StringUtil.isNotEmpty(biztype)) {
			sb.append("业务类型:" + biztype + ";");

		}
		if (StringUtil.isNotEmpty(logcontent)) {
			sb.append("描述:" + logcontent + ";");

		}
		return sb.toString();

	}
}
