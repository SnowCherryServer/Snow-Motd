package mc233.fun.snowmotd.util;

import java.util.logging.Logger;

public class PluginUtil {
    private static Logger logger = Logger.getLogger("Snow-Motd");

    private PluginUtil() {
    }

    public static void check() {
        pluginHotReloadCheck();
    }

    public static void pluginHotReloadCheck() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement[] var1 = stackTrace;
        int var2 = stackTrace.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            StackTraceElement stackTraceElement = var1[var3];
            String methodName = stackTraceElement.getMethodName();
            if (methodName.equalsIgnoreCase("performCommand") || methodName.equalsIgnoreCase("dispatchCommand")) {
                logger.info("");
                logger.severe("检测到插件被热重载 自动禁用 Snow-Motd");
                logger.info("----------------------------");
                throw new PluginCheckException();
            }
        }

    }
}
