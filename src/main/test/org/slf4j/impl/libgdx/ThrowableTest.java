package org.slf4j.impl.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.DummyLogApplication;
import org.slf4j.impl.LibgdxLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Longri on 27.03.2017.
 */
public class ThrowableTest {

    static Logger staticlog = LoggerFactory.getLogger("staticTest");

    static {
        staticlog.debug("Before initial");
    }

    @BeforeAll
    static void setGdx() {

        Gdx.files = new LwjglFiles();
        Gdx.app = new DummyLogApplication();
        Gdx.app.setApplicationLogger(new LwjglApplicationLogger());

        LibgdxLogger.PROPERTIES_FILE_HANDLE = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE);

        //delete alt properties file
        FileHandle propFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE);
        if (propFile.exists()) {
            propFile.delete();
        }

        //delete alt log file
        FileHandle logFile = Gdx.files.local("test.log");
        if (logFile.exists()) {
            logFile.delete();
        }

        LibgdxLogger.INITIALIZED = false;
    }


    @Test
    void throwableTest() {


        LoggerConfig config = new LoggerConfig();
        config.LOG_FILE = "exceptionLogFile.log";
        config.DATE_TIME_FORMAT_STR = "dd.mm.yyyy";
        LibgdxLogger.initial(config);


        Logger log = LoggerFactory.getLogger("exceptionLogger");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        FileHandle logFile = LibgdxLogger.getLogFileHandle();
        if (logFile.exists()) {
            logFile.delete();
        }

        try {
            float test = 25 / 0;
        } catch (Exception e) {
            log.error("Test Error", e);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat("LogFile must exist", logFile.exists());

        String logFileText = logFile.readString("utf-8");

        String mustLogFileText = "[DATE] [main] [ERROR] exceptionLogger - Test Error\n" +
                "java.lang.ArithmeticException: / by zero\n" +
                "\tat org.slf4j.impl.libgdx.ThrowableTest.throwableTest(ThrowableTest.java:";

        //replace date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("dd.mm.yyyy");
        String dateString = ft.format(date);

        mustLogFileText = mustLogFileText.replace("[DATE]", dateString);

        logFileText = logFileText.replace("\r", "").replace("\n", "").replace("\t", "");
        mustLogFileText = mustLogFileText.replace("\r", "").replace("\n", "").replace("\t", "");

        logFileText = logFileText.replace(dateString + " [main] [DEBUG] staticTest - Before initial", "");


        for (int i = 0, n = mustLogFileText.length() - 1; i < n; i++) {
            char c1 = logFileText.charAt(i);
            char c2 = mustLogFileText.charAt(i);

            assertThat("LogFile text not correct at index " + i, c1 == c2);
        }


        if (logFile.exists()) {
            logFile.delete();
        }

    }

    @Test
    void throwableTestFormattedString() {

        LoggerConfig config = new LoggerConfig();
        config.LOG_FILE = "exceptionLogFile.log";
        config.DATE_TIME_FORMAT_STR = "dd.mm.yyyy";
        LibgdxLogger.initial(config);


        Logger log = LoggerFactory.getLogger("exceptionLogger");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        FileHandle logFile = LibgdxLogger.getLogFileHandle();
        if (logFile.exists()) {
            logFile.delete();
        }

        try {
            float test = 25 / 0;
        } catch (Exception e) {
            log.error("Test Error  value {}", 123, e);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat("LogFile must exist", logFile.exists());

        String logFileText = logFile.readString("utf-8");

        String mustLogFileText = "[DATE] [main] [ERROR] exceptionLogger - Test Error  value 123\n" +
                "java.lang.ArithmeticException: / by zero\n" +
                "\tat org.slf4j.impl.libgdx.ThrowableTest.throwableTestFormattedString(ThrowableTest.java:";

        //replace date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("dd.mm.yyyy");
        String dateString = ft.format(date);

        mustLogFileText = mustLogFileText.replace("[DATE]", dateString);

        logFileText = logFileText.replace("\r", "").replace("\n", "").replace("\t", "");
        mustLogFileText = mustLogFileText.replace("\r", "").replace("\n", "").replace("\t", "");

        logFileText = logFileText.replace(dateString + " [main] [DEBUG] staticTest - Before initial", "");


        for (int i = 0, n = mustLogFileText.length() - 1; i < n; i++) {
            char c1 = logFileText.charAt(i);
            char c2 = mustLogFileText.charAt(i);

            assertThat("LogFile text not correct at index " + i, c1 == c2);
        }

//        assertEquals(mustLogFileText,logFileText);

        if (logFile.exists()) {
            logFile.delete();
        }

    }
}
