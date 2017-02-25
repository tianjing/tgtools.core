package tgtools.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

public class DataBaseFactory {
	static {

		m_Connections = new LinkedHashMap<String, IDataAccess>();

	}

	/**
	 * 存储连接器的对象池
	 */
	private static LinkedHashMap<String, IDataAccess> m_Connections;

	private static HashMap<String, IDataAccess> getConnections() {
		return m_Connections;
	}

	public static String Default = "DefaultDB";

	/**
	 * 获取默认的数据连接器 如果 名称为DefaultDB的连接器存在即返回 否则返回第一个连接器
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IDataAccess getDefault() {
		if (getConnections().containsKey(Default)) {
			return getConnections().get(Default);
		}

		for (Iterator it = m_Connections.entrySet().iterator();it.hasNext();) {
			Entry<String, IDataAccess> entry = (Entry<String, IDataAccess>)it.next();  
			if (null != entry.getValue()) {
				
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 根据名称获取连接器
	 * 
	 * @param p_Name
	 * @return
	 */
	public static IDataAccess get(String p_Name) {
		if (getConnections().containsKey(p_Name)) {
			return getConnections().get(p_Name);
		}
		return null;
	}

	/**
	 * 移除一个连接器
	 * 
	 * @param p_Name
	 */
	public static void remove(String p_Name) {
		if (getConnections().containsKey(p_Name)) {
			try {
				getConnections().get(p_Name).close();
			} catch (Exception e) {
			}
		}

	}
	/**
	 * 添加一个连接器
	 * @param p_Name
	 * @param params
	 * @throws APPErrorException
	 */
	public static void add(String p_Name, Object... params)
			throws APPErrorException {
		if(StringUtil.isNullOrEmpty(p_Name))
		{
			throw new APPErrorException("数据源名称不能为空");
		}
		IDataAccess dataacc = null;
		if (getConnections().containsKey(p_Name)) {
			return;
		}
		if (p_Name.toUpperCase().startsWith("DM")
				|| p_Name.toUpperCase().startsWith("DAMENG")) {
			dataacc = new DMDataAccess();
			dataacc.init(params);
		} else if (p_Name.toUpperCase().startsWith("DBCP")) {
			dataacc = new DBCPDataAccess();
			dataacc.init(params);
		} else if (p_Name.toUpperCase().startsWith("SPRING")) {
			dataacc = new SpringDataAccess();
			dataacc.init(params);
		}else if (p_Name.toUpperCase().startsWith("PI3000SECONDDATA")) {
			dataacc = new tgtools.db.pi3000.RPCDataAccess();
			dataacc.init(params);
		}
		else if(p_Name.toUpperCase().contains("RPCSOURCE")) {
			Object obj =params[0];
			if(obj instanceof  IDataAccess)
			dataacc = (IDataAccess)obj;
		}
		else if(p_Name.toUpperCase().contains("JNDISOURCE")) {
			LogHelper.info("","加载JNDISOURCE："+params[0],"DataBaseFactory");
			Object obj =params[0];
			if(obj instanceof  IDataAccess)
				dataacc = (IDataAccess)obj;
		}
		else if(p_Name.toUpperCase().contains("DATAACCESS")) {
			LogHelper.info("","加载DATAACCESS："+params[0],"DataBaseFactory");
			Object obj =params[0];
			if(obj instanceof  IDataAccess)
				dataacc = (IDataAccess)obj;
		}
		if (null != dataacc) {
			getConnections().put(p_Name, dataacc);
		}

	}

	public static void main(String[] args) throws APPErrorException {
		String str1[]={"jdbc:dm://172.17.3.66:5236","SYSDBA","SYSDBA"};
		DataBaseFactory.add("DM1",(Object[])str1);
		DataBaseFactory.add("DM2",(Object[])str1);

		IDataAccess dm1= DataBaseFactory.get("DM1");
		IDataAccess dm2= DataBaseFactory.get("DM2");
		System.out.println(""+dm1+dm2);
	}
}
