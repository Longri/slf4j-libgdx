package org.slf4j.impl;

import java.util.Date;

/**
 * Created by Longri on 06.02.2017.
 */
public class WaitForInitalisationLogger extends LibgdxLogger {


    /**
     * Package access allows only {@link LibgdxLoggerFactory} to instantiate
     * SimpleLogger instances.
     *
     * @param name
     */
    WaitForInitalisationLogger(String name) {
        super(name);
    }

    @Override
    protected void init() {
        // can not initial if Gdx.App is not initial
    }

    protected void log(int level, String message, Throwable throwable) {
        if (!INITIALIZED) {
            store.add(new LogStructure(level, message, throwable));
        } else {
            Date now = new Date();
            long miils = System.currentTimeMillis();
            if (!storeWrited.get()) {
                writeStore();
            }
            finalLog(level, message, throwable, now, miils);
        }
    }
}
