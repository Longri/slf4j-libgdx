package org.slf4j.impl;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Longri on 05.02.17.
 */
public class DummyLogApplication implements Application {


    public DummyLogApplication() {
        initialize();
    }

    // implementation for logging only

    protected int logLevel = Integer.MAX_VALUE;
    private ApplicationLogger applicationLogger;


    @Override
    public void debug(String tag, String message) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
    }

    @Override
    public void log(String tag, String message) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return applicationLogger;
    }


    //####################################################################


    @Override
    public ApplicationListener getApplicationListener() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Audio getAudio() {
        return null;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public Net getNet() {
        return null;
    }

    @Override
    public ApplicationType getType() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return 0;
    }

    @Override
    public long getNativeHeap() {
        return 0;
    }

    @Override
    public Preferences getPreferences(String name) {
        return null;
    }

    @Override
    public Clipboard getClipboard() {
        return null;
    }

    protected Thread mainLoopThread;

    private void initialize() {
        mainLoopThread = new Thread("HeadlessApplication") {
            @Override
            public void run() {
                try {
                    DummyLogApplication.this.mainLoop();
                } catch (Throwable t) {
                    if (t instanceof RuntimeException)
                        throw (RuntimeException) t;
                    else
                        throw new GdxRuntimeException(t);
                }
            }
        };
        mainLoopThread.start();
    }


    protected boolean running = true;
    private final long renderInterval = 0;

    void mainLoop() {

        long t = TimeUtils.nanoTime() + renderInterval;
        if (renderInterval >= 0f) {
            while (running) {
                final long n = TimeUtils.nanoTime();
                if (t > n) {
                    try {
                        Thread.sleep((t - n) / 1000000);
                    } catch (InterruptedException e) {
                    }
                    t = TimeUtils.nanoTime() + renderInterval;
                } else
                    t = n + renderInterval;

                executeRunnables();

                // If one of the runnables set running to false, for example after an exit().
                if (!running) break;
            }
        }
    }


    protected final Array<Runnable> runnables = new Array<Runnable>();
    protected final Array<Runnable> executedRunnables = new Array<Runnable>();

    public boolean executeRunnables() {
        synchronized (runnables) {
            for (int i = runnables.size - 1; i >= 0; i--)
                executedRunnables.add(runnables.get(i));
            runnables.clear();
        }
        if (executedRunnables.size == 0) return false;
        for (int i = executedRunnables.size - 1; i >= 0; i--)
            executedRunnables.removeIndex(i).run();
        return true;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }

    @Override
    public void exit() {

    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }
}
