package com.letv.netty.rpc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Title:系统时钟<br>
 * <p/>
 * Description: <br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class SystemClock {

    private final long precision;
    private final AtomicLong now;
    private static final Logger logger = LoggerFactory.getLogger(SystemClock.class);
    private ScheduledService scheduler = null;


    public SystemClock(long precision) {
        this.precision = precision <= 0 ? 0 : precision;
        now = new AtomicLong(System.currentTimeMillis());
        if (this.precision > 0) {
            scheduleClockUpdating();
        }
    }

    private void scheduleClockUpdating() {
        InTimer iTimer = new InTimer(now);
        scheduler = new ScheduledService("System Clock", ScheduledService.MODE_FIXEDRATE,
                iTimer, precision, precision, TimeUnit.MILLISECONDS).start();
    }

    public long now() {
        return precision == 0 ? System.currentTimeMillis() : now.get();
    }

    public long precision() {
        return precision;
    }

    class InTimer implements Runnable{
        private final AtomicLong inNow;

        private Integer count = 0;

        public InTimer(AtomicLong nowLong){
             inNow = nowLong;
        }

        public void run() {
            try {
                inNow.set(System.currentTimeMillis());
            } catch (Throwable e) {
                count++;
                if(count % 100 == 1){
                    logger.error(e.getMessage(),e);
                }
                if(count>10000) count = 0;
            }
        }
    }

    /**
     * 关闭
     */
    public void close() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (Exception e) {
                logger.warn("Failed to shutdown system clock", e);
            }
        }
    }
}
