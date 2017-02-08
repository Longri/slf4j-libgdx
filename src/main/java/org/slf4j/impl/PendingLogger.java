package org.slf4j.impl;

import org.slf4j.helpers.MarkerIgnoringBase;

import java.util.Date;

/**
 * Created by Longri on 08.02.2017.
 */
public class PendingLogger extends LibgdxLogger {


    LibgdxLogger logger = null;

    /**
     * Package access allows only {@link LibgdxLoggerFactory} to instantiate
     * SimpleLogger instances.
     *
     * @param name
     */
    PendingLogger(String name) {
        super(name);
    }

    @Override
    protected void init() {
        // can not initial if Gdx.App is not initial
    }

    protected void log(int level, String message, Throwable throwable) {
        if (!INITIALIZED) {
            store.add(new LogStructure(this, level, message, throwable));
        } else {
            Date now = new Date();
            long miils = System.currentTimeMillis();
            if (!storeWrited.get()) {
                writeStore();
            }
            logger.finalLog(level, message, throwable, now, miils);
        }
    }
}
