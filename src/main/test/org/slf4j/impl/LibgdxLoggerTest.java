package org.slf4j.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Longri on 04.02.17.
 */
class LibgdxLoggerTest {

    @BeforeAll
    static void setGdx() {
        Gdx.files = new LwjglFiles();

        Gdx.app = new DummyLogApplication();
        Gdx.app.setApplicationLogger(new LwjglApplicationLogger());

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
    void init() {

        assertThat("Should be not initialized", !LibgdxLogger.INITIALIZED);

        Logger log = LoggerFactory.getLogger("INIT-TEST");

        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        assertThat("Logger instance must LibgdxLogger.class", log instanceof LibgdxLogger);

        LibgdxLogger libgdxLogger = (LibgdxLogger) log;

        // with default values
        assertThat("Wrong default value LOG_FILE", LibgdxLogger.LOG_FILE.equals("System.err"));
        assertThat("Wrong default value  DEFAULT_LOG_LEVEL ", LibgdxLogger.DEFAULT_LOG_LEVEL == libgdxLogger.LOG_LEVEL_DEBUG);
        assertThat("Wrong default value  SHOW_LOG_NAME ", LibgdxLogger.SHOW_LOG_NAME == true);
        assertThat("Wrong default value  SHOW_SHORT_LOG_NAME ", LibgdxLogger.SHOW_SHORT_LOG_NAME == false);
        assertThat("Wrong default value  SHOW_DATE_TIME ", LibgdxLogger.SHOW_DATE_TIME == true);
        assertThat("Wrong default value  SHOW_THREAD_NAME ", LibgdxLogger.SHOW_THREAD_NAME == true);
        assertThat("Wrong default value  DATE_TIME_FORMAT_STR ", LibgdxLogger.DATE_TIME_FORMAT_STR == null);
        assertThat("Wrong default value  LEVEL_IN_BRACKETS ", LibgdxLogger.LEVEL_IN_BRACKETS == false);
        assertThat("Wrong default value  WARN_LEVEL_STRING ", LibgdxLogger.WARN_LEVEL_STRING.equals("WARN"));


        //write a propertie file and init with this
        FileHandle propFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE);
        propFile.writeString(LibgdxLogger.DEFAULT_LOG_LEVEL_KEY + "=error  \n", false);
        propFile.writeString(LibgdxLogger.SHOW_DATE_TIME_KEY + "=false  \n", true);
        propFile.writeString(LibgdxLogger.DATE_TIME_FORMAT_KEY + "=dd.mm.yyyy   \n", true);
        propFile.writeString(LibgdxLogger.SHOW_THREAD_NAME_KEY + " = false   \n", true);
        propFile.writeString(LibgdxLogger.SHOW_LOG_NAME_KEY + "=false  \n", true);
        propFile.writeString(LibgdxLogger.SHOW_SHORT_LOG_NAME_KEY + "=true  \n", true);
        propFile.writeString(LibgdxLogger.LOG_FILE_KEY + "=testLog.log  \n", true);
        propFile.writeString(LibgdxLogger.LEVEL_IN_BRACKETS_KEY + "=true  \n", true);
        propFile.writeString(LibgdxLogger.WARN_LEVEL_STRING_KEY + "=war  \n", true);

        LibgdxLogger.INITIALIZED = false;
        assertThat("Should be not initialized", !LibgdxLogger.INITIALIZED);
        Logger log2 = LoggerFactory.getLogger("INIT-TEST2");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        assertThat("Wrong loaded value LOG_FILE", LibgdxLogger.LOG_FILE.equals("testLog.log"));
        assertThat("Wrong loaded value  DEFAULT_LOG_LEVEL ", LibgdxLogger.DEFAULT_LOG_LEVEL == libgdxLogger.LOG_LEVEL_ERROR);
        assertThat("Wrong loaded value  SHOW_LOG_NAME ", LibgdxLogger.SHOW_LOG_NAME == false);
        assertThat("Wrong loaded value  SHOW_SHORT_LOG_NAME ", LibgdxLogger.SHOW_SHORT_LOG_NAME == true);
        assertThat("Wrong loaded value  SHOW_DATE_TIME ", LibgdxLogger.SHOW_DATE_TIME == false);
        assertThat("Wrong loaded value  SHOW_THREAD_NAME ", LibgdxLogger.SHOW_THREAD_NAME == false);
        assertThat("Wrong loaded value  DATE_TIME_FORMAT_STR ", LibgdxLogger.DATE_TIME_FORMAT_STR.equals("dd.mm.yyyy"));
        assertThat("Wrong loaded value  LEVEL_IN_BRACKETS ", LibgdxLogger.LEVEL_IN_BRACKETS == true);
        assertThat("Wrong loaded value  WARN_LEVEL_STRING ", LibgdxLogger.WARN_LEVEL_STRING.equals("war"));

        // delete test files
        propFile.delete();


        //set direct over System properties
        System.setProperty(LibgdxLogger.DEFAULT_LOG_LEVEL_KEY, "trace");
        System.setProperty(LibgdxLogger.LOG_FILE_KEY, "test.log");
        LibgdxLogger.INITIALIZED = false;
        assertThat("Should be not initialized", !LibgdxLogger.INITIALIZED);
        Logger log3 = LoggerFactory.getLogger("INIT-TEST3");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        assertThat("Wrong loaded value LOG_FILE", LibgdxLogger.LOG_FILE.equals("test.log"));
        assertThat("Wrong loaded value  DEFAULT_LOG_LEVEL ", LibgdxLogger.DEFAULT_LOG_LEVEL == libgdxLogger.LOG_LEVEL_TRACE);


        //log and test log file
        log3.debug("Test Debug");

        System.setProperty(LibgdxLogger.LOG_FILE_KEY, "test.log");
        System.setProperty(LibgdxLogger.SHOW_DATE_TIME_KEY, "true");

        LibgdxLogger.INITIALIZED = false;
        assertThat("Should be not initialized", !LibgdxLogger.INITIALIZED);
        Logger log4 = LoggerFactory.getLogger("INIT-TEST4");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        log4.error("Error with date");

        // wait for writing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FileHandle logFile = Gdx.files.local("test.log");
        assertThat("LogFile must exist", logFile.exists());

        String logFileText = logFile.readString();

        String mustLogFileText = "[DEBUG] INIT-TEST3 - Test Debug\n" +
                "[DATE] [ERROR] INIT-TEST4 - Error with date\n";

        //replace date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("dd.mm.yyyy");
        String dateString = ft.format(date);

        mustLogFileText = mustLogFileText.replace("[DATE]", dateString);

        assertThat("LogFile text not correct", logFileText.equals(mustLogFileText));


        if (logFile.exists()) {
            logFile.delete();
        }

    }
}