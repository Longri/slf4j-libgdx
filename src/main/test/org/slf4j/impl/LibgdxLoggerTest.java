package org.slf4j.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.libgdx.LoggerConfig;
import org.slf4j.impl.libgdx.XmlParser;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Longri on 04.02.17.
 */
class LibgdxLoggerTest {

    static Logger staticlog = LoggerFactory.getLogger("staticTest");

    @BeforeAll
    static void setGdx() {

        staticlog.debug("Before initial");

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
    void init() throws IOException {

        Logger log = LoggerFactory.getLogger("INIT-TEST");

        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);
        staticlog.debug("After initial");
        assertThat("Logger instance must LibgdxLogger.class", log instanceof LibgdxLogger);

        LibgdxLogger libgdxLogger = (LibgdxLogger) log;

        // with default values
        assertThat("Wrong default value LOG_FILE", LibgdxLogger.CONFIG.LOG_FILE.equals("System.err"));
        assertThat("Wrong default value  DEFAULT_LOG_LEVEL ", LibgdxLogger.CONFIG.DEFAULT_LOG_LEVEL == libgdxLogger.LOG_LEVEL_DEBUG);
        assertThat("Wrong default value  SHOW_LOG_NAME ", LibgdxLogger.CONFIG.SHOW_LOG_NAME == true);
        assertThat("Wrong default value  SHOW_SHORT_LOG_NAME ", LibgdxLogger.CONFIG.SHOW_SHORT_LOG_NAME == false);
        assertThat("Wrong default value  SHOW_DATE_TIME ", LibgdxLogger.CONFIG.SHOW_DATE_TIME == true);
        assertThat("Wrong default value  SHOW_THREAD_NAME ", LibgdxLogger.CONFIG.SHOW_THREAD_NAME == true);
        assertThat("Wrong default value  DATE_TIME_FORMAT_STR ", LibgdxLogger.CONFIG.DATE_TIME_FORMAT_STR == null);
        assertThat("Wrong default value  LEVEL_IN_BRACKETS ", LibgdxLogger.CONFIG.LEVEL_IN_BRACKETS == false);
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

        assertThat("Wrong loaded value LOG_FILE", LibgdxLogger.CONFIG.LOG_FILE.equals("testLog.log"));
        assertThat("Wrong loaded value  DEFAULT_LOG_LEVEL ", LibgdxLogger.CONFIG.DEFAULT_LOG_LEVEL == libgdxLogger.LOG_LEVEL_ERROR);
        assertThat("Wrong loaded value  SHOW_LOG_NAME ", LibgdxLogger.CONFIG.SHOW_LOG_NAME == false);
        assertThat("Wrong loaded value  SHOW_SHORT_LOG_NAME ", LibgdxLogger.CONFIG.SHOW_SHORT_LOG_NAME == true);
        assertThat("Wrong loaded value  SHOW_DATE_TIME ", LibgdxLogger.CONFIG.SHOW_DATE_TIME == false);
        assertThat("Wrong loaded value  SHOW_THREAD_NAME ", LibgdxLogger.CONFIG.SHOW_THREAD_NAME == false);
        assertThat("Wrong loaded value  DATE_TIME_FORMAT_STR ", LibgdxLogger.CONFIG.DATE_TIME_FORMAT_STR.equals("dd.mm.yyyy"));
        assertThat("Wrong loaded value  LEVEL_IN_BRACKETS ", LibgdxLogger.CONFIG.LEVEL_IN_BRACKETS == true);
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

        assertThat("Wrong loaded value LOG_FILE", LibgdxLogger.CONFIG.LOG_FILE.equals("test.log"));
        assertThat("Wrong loaded value  DEFAULT_LOG_LEVEL ", LibgdxLogger.CONFIG.DEFAULT_LOG_LEVEL == libgdxLogger.LOG_LEVEL_TRACE);


        //log and test log file
        log3.debug("Test Debug");

        System.setProperty(LibgdxLogger.LOG_FILE_KEY, "test.log");
        System.setProperty(LibgdxLogger.SHOW_DATE_TIME_KEY, "true");

        LibgdxLogger.INITIALIZED = false;
        assertThat("Should be not initialized", !LibgdxLogger.INITIALIZED);
        Logger log4 = LoggerFactory.getLogger("INIT-TEST4");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        log4.error("Error with date");

        staticlog.debug("static debug log");


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
                "[DATE] [ERROR] INIT-TEST4 - Error with date\n" +
                "[DATE] [DEBUG] staticTest - static debug log\n";

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


        //###################################################################################

        // check initialisation with config

        LoggerConfig config = new LoggerConfig();

        LibgdxLogger.initial(config);
        assertThat("Settings FileHandle must be NULL", LibgdxLogger.PROPERTIES_FILE_HANDLE == null);
        assertThat("Config must set to default", LibgdxLogger.CONFIG.equals(new LoggerConfig()));
        assertThat("Must not initialized", !LibgdxLogger.INITIALIZED);
        Logger configLog = LoggerFactory.getLogger("configLog");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);


        LoggerConfig config2 = new LoggerConfig();
        config2.SHOW_DATE_TIME = false;

        LibgdxLogger.initial(config2);
        assertThat("Settings FileHandle must be NULL", LibgdxLogger.PROPERTIES_FILE_HANDLE == null);
        assertThat("Config must not default", !LibgdxLogger.CONFIG.equals(new LoggerConfig()));
        assertThat("Config must equals Config2", LibgdxLogger.CONFIG.equals(config2));
        assertThat("Must not initialized", !LibgdxLogger.INITIALIZED);
        Logger configLog2 = LoggerFactory.getLogger("configLog2");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);


        // initial with config.xml
        FileHandle xmlFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE_XML);
        Writer fileWriter = xmlFile.writer(false);
        XmlWriter xmlWriter = new XmlWriter(fileWriter);
        xmlWriter.element(XmlParser.CONFIG_NAME)
                .attribute(LibgdxLogger.DEFAULT_LOG_LEVEL_KEY, 50)
                .attribute(LibgdxLogger.SHOW_DATE_TIME_KEY, false)
                .attribute(LibgdxLogger.LEVEL_IN_BRACKETS_KEY, true)
                .attribute(LibgdxLogger.SHOW_THREAD_NAME_KEY, false)
                .attribute(LibgdxLogger.SHOW_LOG_NAME_KEY, false)
                .attribute(LibgdxLogger.SHOW_SHORT_LOG_NAME_KEY, true)
                .attribute(LibgdxLogger.LOG_FILE_KEY, "logFile.log")
                .attribute(LibgdxLogger.DATE_TIME_FORMAT_KEY, "dd-mm-yy")
                .pop();

        xmlWriter.flush();
        xmlWriter.close();

        LoggerConfig expected = new LoggerConfig();
        expected.DEFAULT_LOG_LEVEL = 50;
        expected.SHOW_DATE_TIME = false;
        expected.LEVEL_IN_BRACKETS = true;
        expected.SHOW_THREAD_NAME = false;
        expected.SHOW_LOG_NAME = false;
        expected.SHOW_SHORT_LOG_NAME = true;
        expected.LOG_FILE = "logFile.log";
        expected.DATE_TIME_FORMAT_STR = "dd-mm-yy";

        LibgdxLogger.initial(xmlFile);
        assertThat("Must not initialized", !LibgdxLogger.INITIALIZED);
        Logger configLog3 = LoggerFactory.getLogger("configLog3");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

        assertEquals(LibgdxLogger.PROPERTIES_FILE_HANDLE, xmlFile, "Settings FileHandle must be NULL");
        assertThat("Config must not default", !LibgdxLogger.CONFIG.equals(new LoggerConfig()));
        assertEquals(LibgdxLogger.CONFIG, expected, "Config must equals Config2");


        assertThat("Wrong loaded value LOG_FILE", LibgdxLogger.CONFIG.LOG_FILE.equals("logFile.log"));
        assertThat("Wrong loaded value  DEFAULT_LOG_LEVEL ", LibgdxLogger.CONFIG.DEFAULT_LOG_LEVEL == 50);
        assertThat("Wrong loaded value  SHOW_LOG_NAME ", LibgdxLogger.CONFIG.SHOW_LOG_NAME == false);
        assertThat("Wrong loaded value  SHOW_SHORT_LOG_NAME ", LibgdxLogger.CONFIG.SHOW_SHORT_LOG_NAME == true);
        assertThat("Wrong loaded value  SHOW_DATE_TIME ", LibgdxLogger.CONFIG.SHOW_DATE_TIME == false);
        assertThat("Wrong loaded value  SHOW_THREAD_NAME ", LibgdxLogger.CONFIG.SHOW_THREAD_NAME == false);
        assertThat("Wrong loaded value  DATE_TIME_FORMAT_STR ", LibgdxLogger.CONFIG.DATE_TIME_FORMAT_STR.equals("dd-mm-yy"));
        assertThat("Wrong loaded value  LEVEL_IN_BRACKETS ", LibgdxLogger.CONFIG.LEVEL_IN_BRACKETS == true);

        assertThat("Include list must empty", LibgdxLoggerFactory.INCLUDE_LIST.isEmpty());
        assertThat("Exclude list must empty", LibgdxLoggerFactory.EXCLUDE_LIST.isEmpty());

        xmlFile.delete();


    }

    @Test
    void throwableTest() {
        FileHandle logFile = Gdx.files.local("exceptionLogFile.log");

        if (logFile.exists()) {
            logFile.delete();
        }

        LoggerConfig config = new LoggerConfig();
        config.LOG_FILE = "exceptionLogFile.log";
        config.DATE_TIME_FORMAT_STR = "dd.mm.yyyy";
        LibgdxLogger.initial(config);

        Logger log = LoggerFactory.getLogger("exceptionLogger");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);

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

        String mustLogFileText = "[DATE] [main] DEBUG staticTest - Before initial\n" +
                "[DATE] [main] ERROR exceptionLogger - Test Error\n" +
                "java.lang.ArithmeticException: / by zero\n" +
                "\tat org.slf4j.impl.LibgdxLoggerTest.throwableTest(LibgdxLoggerTest.java:270)";

        //replace date
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat ft = new SimpleDateFormat("dd.mm.yyyy");
        String dateString = ft.format(date);

        mustLogFileText = mustLogFileText.replace("[DATE]", dateString);

        logFileText = logFileText.substring(0, 223).replace("\r", "");

        assertEquals(mustLogFileText, logFileText, "LogFile text not correct");


        if (logFile.exists()) {
            logFile.delete();
        }

    }
}