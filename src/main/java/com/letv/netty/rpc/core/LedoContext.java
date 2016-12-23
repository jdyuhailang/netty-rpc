package com.letv.netty.rpc.core;

import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/22
 * Time: 15:09
 */
public class LedoContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(LedoContext.class);
    private final static ConcurrentHashMap context = new ConcurrentHashMap();
    public static final SystemClock systemClock = new SystemClock(0);
    /**
     * 当前进程Id
     */
    public final static String PID = ManagementFactory.getRuntimeMXBean()
            .getName().split("@")[0];
    static {
        LOGGER.info("::Loading Ledo SDK::[{}]", Constants.LEDO_BUILD_VERSION);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                LOGGER.info("Ledo catch JVM shutdown event,release ledo resources");
                //destroy(false);
            }
        }, "LedoShutdownHook"));
    }
    /**
     * 是否单元测试模式
     *
     * @return 单元测试模式不加载
     */
    public static boolean isUnitTestMode() {
        return false;
    }

    public static int getVersion() {
        return 1000;
    }
}
