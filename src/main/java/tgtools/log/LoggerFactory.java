package tgtools.log;

import java.util.HashMap;

/**
 * 通用日志工厂
 * 适合 不想在每个类中写 Loger的人
 * @author tianjing
 */
public class LoggerFactory {

    private static HashMap<String, ILoger> logers;
    private static String defaultName = "Default";

    private static HashMap<String, ILoger> getLogers() {
        if (null == logers) {
            logers = new HashMap(10);
            logers.put(defaultName, createLoger(defaultName));
        }
        return logers;
    }

    public static ILoger getDefault() {
        return get(defaultName);
    }

    /**
     * 获取指定名称的日志，如果不存在则创建
     *
     * @param pName
     *
     * @return
     */
    public static ILoger get(String pName) {
        if (!getLogers().containsKey(pName)) {
            ILoger loger = createLoger(pName);
            if (null != loger) {
                getLogers().put(pName, loger);
            } else {
                return null;
            }
        }
        return getLogers().get(pName);

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
     * @param pName
     *
     * @return
     */
    private static ILoger createLoger(String pName) {
        if (!getLogers().containsKey(pName)) {
//            if( org.slf4j.LoggerFactory.getLogger(StringUtil.EMPTY_STRING).getClass().getName().indexOf("org.slf4j.impl.Log4jLoggerAdapter")>=0)
//            {
//                return new Log4jLoger(pName);
//            }
            return new DefaultLoger(pName);
        }
        return null;
    }
}
