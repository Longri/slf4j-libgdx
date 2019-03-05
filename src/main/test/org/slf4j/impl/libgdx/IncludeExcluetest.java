package org.slf4j.impl.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.XmlWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.EmptyLogger;
import org.slf4j.impl.LibgdxLogger;
import org.slf4j.impl.LibgdxLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Longri on 27.03.2017.
 */
public class IncludeExcluetest {

    static Logger staticlog = LoggerFactory.getLogger("staticTest");

    static {
        staticlog.debug("Before initial");
    }

    @BeforeAll
    static void setGdx() {
        File nativeFile = new File("./native/gdx-platform-1.9.9-natives-desktop.jar");
        SharedLibraryLoader sharedLibraryLoader = new SharedLibraryLoader(nativeFile.getAbsolutePath());
        sharedLibraryLoader.load("gdx");
        Gdx.app = new HeadlessApplication(new Game() {
            @Override
            public void create() {

            }
        });
        Gdx.files = Gdx.app.getFiles();

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
    void includeExclude() throws IOException {
        // initial with config.xml
        FileHandle xmlFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE_XML);
        Writer fileWriter = xmlFile.writer(false);
        XmlWriter xmlWriter = new XmlWriter(fileWriter);
        xmlWriter.element(XmlParser.CONFIG_NAME)
                .attribute(LibgdxLogger.DEFAULT_LOG_LEVEL_KEY, "Debug")
                .attribute(LibgdxLogger.SHOW_DATE_TIME_KEY, true)
                .attribute(LibgdxLogger.LEVEL_IN_BRACKETS_KEY, true)
                .attribute(LibgdxLogger.SHOW_THREAD_NAME_KEY, true)
                .attribute(LibgdxLogger.SHOW_LOG_NAME_KEY, false)
                .attribute(LibgdxLogger.SHOW_SHORT_LOG_NAME_KEY, true)
                .attribute(LibgdxLogger.LOG_FILE_KEY, "logFile.log")
                .attribute(LibgdxLogger.DATE_TIME_FORMAT_KEY, "dd-MM-yy hh:mm:ss-SSS")
                .element(LibgdxLogger.EXCLUDE)
                .attribute(LibgdxLogger.CLASS_KEY, "configLog3")
                .pop()
                .element(LibgdxLogger.EXCLUDE)
                .attribute(LibgdxLogger.CLASS_KEY, "configLog1")
                .pop()
                .pop();


        xmlWriter.flush();
        xmlWriter.close();

        LoggerConfig expected = new LoggerConfig();
        expected.DEFAULT_LOG_LEVEL = 10;
        expected.SHOW_DATE_TIME = true;
        expected.LEVEL_IN_BRACKETS = true;
        expected.SHOW_THREAD_NAME = true;
        expected.SHOW_LOG_NAME = false;
        expected.SHOW_SHORT_LOG_NAME = true;
        expected.LOG_FILE = "logFile.log";
        expected.DATE_TIME_FORMAT_STR = "dd-MM-yy hh:mm:ss-SSS";

        LibgdxLogger.initial(xmlFile);
        assertThat("Must not initialized", !LibgdxLogger.INITIALIZED);
        Logger configLog2 = LoggerFactory.getLogger("configLog2");
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);
        Logger configLog3 = LoggerFactory.getLogger("configLog3");
        Logger configLog4 = LoggerFactory.getLogger("de.test.short.configLog4");

        assertThat("Must instanceOf EmptyLogger", configLog3 instanceof EmptyLogger);

        assertThat("Include list must empty", LibgdxLoggerFactory.INCLUDE_LIST.isEmpty());
        assertThat("Exclude list must have one entry", LibgdxLoggerFactory.EXCLUDE_LIST.size() == 2);

        xmlFile.delete();
        FileHandle logFile = LibgdxLogger.getLogFileHandle();
        if (logFile.exists()) {
            logFile.delete();
        }

        configLog3.debug("IGNORED");
        configLog2.debug("LOGGED");
        configLog4.debug("test");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertThat("LogFile must exist", logFile.exists());

        String logFileText = logFile.readString("utf-8");

        assertThat("Log file must have LOGGED entry", logFileText.contains("LOGGED"));
        assertThat("Log file may not include IGNORED entry", !logFileText.contains("IGNORED"));

        if (logFile.exists()) {
            logFile.delete();
        }

    }
}
