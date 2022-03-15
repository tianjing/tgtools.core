package tgtools.db;

import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * @author tianjing
 */
public class DataBaseFactory {
    public static final String DBTYPE_DM7 = "dm";
    public static final String DBTYPE_DM6 = "dm6";
    public static final String DBTYPE_MYSQL = "mysql";
    public static final String DBTYPE_ORACLE = "oracle";
    public static String Default = "DefaultDB";
    /**
     * 存储连接器的对象池
     */
    private static LinkedHashMap<String, IDataAccess> m_Connections;

    static {
        m_Connections = new LinkedHashMap<String, IDataAccess>();
    }

    private static HashMap<String, IDataAccess> getConnections() {
        return m_Connections;
    }

    /**
     * 获取默认的数据连接器 如果 名称为DefaultDB的连接器存在即返回 否则返回第一个连接器
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static IDataAccess getDefault() {
        if (getConnections().containsKey(Default)) {
            return getConnections().get(Default);
        }

        for (Iterator it = m_Connections.entrySet().iterator(); it.hasNext(); ) {
            Entry<String, IDataAccess> entry = (Entry<String, IDataAccess>) it.next();
            if (null != entry.getValue()) {

                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 根据名称获取连接器
     *
     * @param pName
     * @return
     */
    public static IDataAccess get(String pName) {
        if (getConnections().containsKey(pName)) {
            return getConnections().get(pName);
        }
        return null;
    }

    /**
     * 移除一个连接器
     *
     * @param pName
     */
    public static void remove(String pName) {
        if (getConnections().containsKey(pName)) {
            IDataAccess vDataAccess = getConnections().get(pName);
            getConnections().remove(pName);
            try {
                vDataAccess.close();
            } catch (Exception e) {
            }
        }

    }

    /**
     * 添加一个连接器
     *
     * @param pName
     * @param pParams
     * @throws APPErrorException
     */
    public static void add(String pName, Object... pParams) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pName)) {
            throw new APPErrorException("数据源名称不能为空");
        }
        IDataAccess dataacc = null;
        if (getConnections().containsKey(pName)) {
            return;
        }

        if (pName.toUpperCase().startsWith("DM6") || pName.toUpperCase().startsWith("DAMENG6")) {
            dataacc = new DM6DataAccess();
            dataacc.init(pParams);
        } else if (pName.toUpperCase().startsWith("DM") || pName.toUpperCase().startsWith("DAMENG")) {
            dataacc = new DMDataAccess();
            dataacc.init(pParams);
        } else if (pName.toUpperCase().startsWith("DBCP")) {
            dataacc = new DBCPDataAccess();
            dataacc.init(pParams);
        } else if (pName.toUpperCase().startsWith("SPRING")) {
            dataacc = new SpringDataAccess();
            dataacc.init(pParams);
        } else if (pName.toUpperCase().startsWith("PI3000SECONDDATA")) {
            dataacc = new tgtools.db.pi3000.RPCDataAccess();
            dataacc.init(pParams);
        } else if (pName.toUpperCase().contains("RPCSOURCE")) {
            Object obj = pParams[0];
            if (obj instanceof IDataAccess) {
                dataacc = (IDataAccess) obj;
            }
        } else if (pName.toUpperCase().contains("JNDISOURCE")) {
            LogHelper.info("", "加载JNDISOURCE：" + pParams[0], "DataBaseFactory");
            Object obj = pParams[0];
            if (obj instanceof IDataAccess) {
                dataacc = (IDataAccess) obj;
            }
        } else if (pName.toUpperCase().contains("DATAACCESS")) {
            LogHelper.info("", "加载DATAACCESS：" + pParams[0], "DataBaseFactory");
            Object obj = pParams[0];
            if (obj instanceof IDataAccess) {
                dataacc = (IDataAccess) obj;
            }
        }
        if (null != dataacc) {
            getConnections().put(pName, dataacc);
        }

    }


}
