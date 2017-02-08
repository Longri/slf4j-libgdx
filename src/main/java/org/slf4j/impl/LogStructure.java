package org.slf4j.impl;

import java.util.Date;

/**
 * Created by Longri on 08.02.2017.
 */
public class LogStructure {
    final PendingLogger logger;
    final int level;
    final String massage;
    final Throwable t;
    final Date now = new Date();
    final long mills = System.currentTimeMillis();

    protected LogStructure(PendingLogger logger, int level, String massage, Throwable t) {
        this.logger= logger;
        this.level = level;
        this.massage = massage;
        this.t = t;
    }
}
