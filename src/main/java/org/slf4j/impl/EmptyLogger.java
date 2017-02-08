package org.slf4j.impl;

import org.slf4j.helpers.MarkerIgnoringBase;

import java.util.Date;

/**
 * Created by Longri on 07.02.2017.
 */
public class EmptyLogger extends LibgdxLogger {

    EmptyLogger() {
        super("emptyLogger");
    }

    @Override
    protected void init() {
        // a empty logger will never initial
    }

    protected void finalLog(int level, String message, Throwable t, Date logtime, long mills) {
        // do nothing
    }


    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String s) {
// do nothing
    }

    @Override
    public void trace(String s, Object o) {
// do nothing
    }

    @Override
    public void trace(String s, Object o, Object o1) {
// do nothing
    }

    @Override
    public void trace(String s, Object... objects) {
// do nothing
    }

    @Override
    public void trace(String s, Throwable throwable) {
// do nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String s) {
// do nothing
    }

    @Override
    public void debug(String s, Object o) {
// do nothing
    }

    @Override
    public void debug(String s, Object o, Object o1) {
// do nothing
    }

    @Override
    public void debug(String s, Object... objects) {
// do nothing
    }

    @Override
    public void debug(String s, Throwable throwable) {
// do nothing
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String s) {
// do nothing
    }

    @Override
    public void info(String s, Object o) {
// do nothing
    }

    @Override
    public void info(String s, Object o, Object o1) {
// do nothing
    }

    @Override
    public void info(String s, Object... objects) {
// do nothing
    }

    @Override
    public void info(String s, Throwable throwable) {
// do nothing
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String s) {
// do nothing
    }

    @Override
    public void warn(String s, Object o) {
// do nothing
    }

    @Override
    public void warn(String s, Object... objects) {
// do nothing
    }

    @Override
    public void warn(String s, Object o, Object o1) {
// do nothing
    }

    @Override
    public void warn(String s, Throwable throwable) {
// do nothing
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String s) {
// do nothing
    }

    @Override
    public void error(String s, Object o) {
// do nothing
    }

    @Override
    public void error(String s, Object o, Object o1) {
// do nothing
    }

    @Override
    public void error(String s, Object... objects) {
// do nothing
    }

    @Override
    public void error(String s, Throwable throwable) {
// do nothing
    }
}
