package org.slf4j.impl.libgdx;

import com.badlogic.gdx.utils.StringBuilder;
import org.slf4j.impl.LibgdxLogger;

/**
 * Created by Longri on 06.02.17.
 */
public class LoggerConfig {
    public int DEFAULT_LOG_LEVEL = LibgdxLogger.LOG_LEVEL_DEBUG;
    public boolean SHOW_DATE_TIME = true;
    public String DATE_TIME_FORMAT_STR = "dd-MM-yy hh:mm:ss-SSS";
    public boolean SHOW_THREAD_NAME = true;
    public boolean SHOW_LOG_NAME = false;
    public boolean SHOW_SHORT_LOG_NAME = true;
    public String LOG_FILE = "System.err";
    public boolean LEVEL_IN_BRACKETS = true;

    public void setFrom(final LoggerConfig config) {
        if (config == null) {
            //set back to default
            setFrom(new LoggerConfig());
        } else {
            this.DEFAULT_LOG_LEVEL = config.DEFAULT_LOG_LEVEL;
            this.SHOW_DATE_TIME = config.SHOW_DATE_TIME;
            this.DATE_TIME_FORMAT_STR = config.DATE_TIME_FORMAT_STR;
            this.SHOW_THREAD_NAME = config.SHOW_THREAD_NAME;
            this.SHOW_LOG_NAME = config.SHOW_LOG_NAME;
            this.SHOW_SHORT_LOG_NAME = config.SHOW_SHORT_LOG_NAME;
            this.LOG_FILE = config.LOG_FILE;
            this.LEVEL_IN_BRACKETS = config.LEVEL_IN_BRACKETS;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof LoggerConfig)) return false;

        LoggerConfig config = (LoggerConfig) obj;

        if (this.DEFAULT_LOG_LEVEL != config.DEFAULT_LOG_LEVEL) return false;
        if (this.SHOW_DATE_TIME != config.SHOW_DATE_TIME) return false;
        if (this.DATE_TIME_FORMAT_STR == null) {
            if (config.DATE_TIME_FORMAT_STR != null) return false;
        } else {
            if (!this.DATE_TIME_FORMAT_STR.equals(config.DATE_TIME_FORMAT_STR)) return false;
        }
        if (this.SHOW_THREAD_NAME != config.SHOW_THREAD_NAME) return false;
        if (this.SHOW_LOG_NAME != config.SHOW_LOG_NAME) return false;
        if (this.SHOW_SHORT_LOG_NAME != config.SHOW_SHORT_LOG_NAME) return false;
        if (this.LOG_FILE == null) {
            if (config.LOG_FILE != null) return false;
        } else {
            if (!this.LOG_FILE.equals(config.LOG_FILE)) return false;
        }
        if (this.LEVEL_IN_BRACKETS != config.LEVEL_IN_BRACKETS) return false;

        return true;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LoggerConfig: \n");
        sb.append("  DEFAULT_LOG_LEVEL = " + this.DEFAULT_LOG_LEVEL + "\n");
        sb.append("  SHOW_DATE_TIME = " + this.SHOW_DATE_TIME + "\n");
        sb.append("  DATE_TIME_FORMAT_STR = " + this.DATE_TIME_FORMAT_STR + "\n");
        sb.append("  SHOW_THREAD_NAME = " + this.SHOW_THREAD_NAME + "\n");
        sb.append("  SHOW_LOG_NAME = " + this.SHOW_LOG_NAME + "\n");
        sb.append("  SHOW_SHORT_LOG_NAME = " + this.SHOW_SHORT_LOG_NAME + "\n");
        sb.append("  LOG_FILE = " + this.LOG_FILE + "\n");
        sb.append("  LEVEL_IN_BRACKETS = " + this.LEVEL_IN_BRACKETS + "\n");
        return sb.toString();
    }
}
