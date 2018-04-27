package tgtools.log;

import tgtools.util.StringUtil;

import java.util.HashMap;

/**
 * 通用日志工厂
 * 适合 不想在每个类中写 Loger的人
 */
public class LoggerFactory {

    private static HashMap<String, ILoger> m_Logers;
    private static String m_DefaultName = "Default";

    private static HashMap<String, ILoger> getLogers() {
        if (null == m_Logers) {
            m_Logers = new HashMap<String, ILoger>();
            m_Logers.put(m_DefaultName, createLoger(m_DefaultName));
        }
        return m_Logers;
    }

    public static ILoger getDefault() {
        return get(m_DefaultName);
    }

    /**
     * 获取指定名称的日志，如果不存在则创建
     *
     * @param p_Name
     *
     * @return
     */
    public static ILoger get(String p_Name) {
        if (!getLogers().containsKey(p_Name)) {
            ILoger loger = createLoger(p_Name);
            if (null != loger) {
                getLogers().put(p_Name, loger);
            } else {
                return null;
            }
        }
        return getLogers().get(p_Name);

    }

    /**
     * 是否包含logger
     * @param pName
     * @return
     */
    public static boolean hasLoger(String pName) {
        return getLogers().containsKey(pName);
    }

    /**
     * 添加logger
     * @param pName
     * @param pLoger
     */
    public static void addLoger(String pName, ILoger pLoger) {
        if (!getLogers().containsKey(pName)) {
            getLogers().put(pName, pLoger);
        }
    }

    /**
     * 移除日志对象
     * @param pName
     */
    public static void removeLoger(String pName) {
        if (hasLoger(pName)) {
            getLogers().remove(pName);
        }
    }

    /**
     * 创建日志
     * @param p_Name
     *
     * @return
     */
    private static ILoger createLoger(String p_Name) {
        if (!getLogers().containsKey(p_Name)) {
            if( org.slf4j.LoggerFactory.getLogger(StringUtil.EMPTY_STRING).getClass().getName().indexOf("org.slf4j.impl.Log4jLoggerAdapter")>=0)
            {
                return new Log4jLoger(p_Name);
            }
            return new DefaultLoger(p_Name);
        }
        return null;
    }
}
