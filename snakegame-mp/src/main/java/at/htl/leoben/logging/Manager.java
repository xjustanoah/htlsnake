package at.htl.leoben.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Manager {
    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }

    public static Logger getLogger(Object obj) {
        String loggerName = getReadableName(obj);
        return getLogger(loggerName);
    }

    public static Logger getLogger(Class<?> obj) {
        String loggerName = obj.getSimpleName();
        return getLogger(loggerName);
    }

    public static String getReadableName(Object obj) {
        return obj.getClass().getSimpleName() + "@" + obj.hashCode();
    }
}
