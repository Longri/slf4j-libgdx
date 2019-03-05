package org.slf4j.impl.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.badlogic.gdx.utils.XmlWriter;
import org.junit.jupiter.api.AfterAll;
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
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Longri on 07.02.2017.
 */
class XmlParserTest {

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
    }


    @Test
    void parseConfig() throws IOException {
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

        LoggerConfig actual = XmlParser.parseConfig(xmlFile);

        assertEquals(expected, actual, "Read config not correct");

    }

    @Test
    void IncludeExcludeTest() throws IOException {
        LibgdxLoggerFactory.INCLUDE_LIST.clear();
        LibgdxLoggerFactory.EXCLUDE_LIST.clear();
        assertThat("Include list must empty", LibgdxLoggerFactory.INCLUDE_LIST.isEmpty());
        assertThat("Exclude list must empty", LibgdxLoggerFactory.EXCLUDE_LIST.isEmpty());

        FileHandle xmlFile = Gdx.files.local("InEX_" + LibgdxLogger.CONFIGURATION_FILE_XML);
        Writer fileWriter = xmlFile.writer(false);
        XmlWriter xmlWriter = new XmlWriter(fileWriter);
        XmlWriter config = xmlWriter.element(XmlParser.CONFIG_NAME);

        config.element(LibgdxLogger.INCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class1").pop();
        config.element(LibgdxLogger.INCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class2").pop();
        config.element(LibgdxLogger.INCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class3").pop();

        config.element(LibgdxLogger.EXCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class4").pop();
        config.element(LibgdxLogger.EXCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class5").pop();
        config.element(LibgdxLogger.EXCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class6").pop();
        config.element(LibgdxLogger.EXCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class7").pop();
        config.element(LibgdxLogger.EXCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class8").pop();
        config.element(LibgdxLogger.EXCLUDE).attribute(LibgdxLogger.CLASS_KEY, "class9").pop();

        config.pop();

        xmlWriter.flush();
        xmlWriter.close();

        LibgdxLogger.initial(xmlFile);
        assertThat("Must not initialized", !LibgdxLogger.INITIALIZED);
        assertThat("Include list must have 3 entry's", LibgdxLoggerFactory.INCLUDE_LIST.size() == 3);
        assertThat("Exclude list must have 6 entry's", LibgdxLoggerFactory.EXCLUDE_LIST.size() == 6);
        Logger configLog4 = LoggerFactory.getLogger("configLog4");
        assertThat("Logger must instanceof EmptyLogger", (configLog4 instanceof EmptyLogger));
        assertThat("Must not initialized", !LibgdxLogger.INITIALIZED);

        Logger class3 = LoggerFactory.getLogger("class3");
        assertThat("Logger must instanceof LibgdxLogger", (class3 instanceof LibgdxLogger));
        assertThat("Must be initialized", LibgdxLogger.INITIALIZED);
        Logger class9 = LoggerFactory.getLogger("class9");
        assertThat("Logger must instanceof EmptyLogger", (class9 instanceof EmptyLogger));

        LibgdxLoggerFactory.reset();
        LibgdxLoggerFactory.INCLUDE_LIST.clear();
        class3 = LoggerFactory.getLogger("class3");
        assertThat("Logger must instanceof LibgdxLogger", (class3 instanceof LibgdxLogger));
        class9 = LoggerFactory.getLogger("class9");
        assertThat("Logger must instanceof EmptyLogger", (class9 instanceof EmptyLogger));
        Logger class6 = LoggerFactory.getLogger("class6");
        assertThat("Logger must instanceof EmptyLogger", (class6 instanceof EmptyLogger));

        xmlFile.delete();
    }

    @Test
    void FileNotFoundExceptionTest() throws IOException {
        FileHandle xmlFile = Gdx.files.local("NoExist" + LibgdxLogger.CONFIGURATION_FILE_XML);
        if (xmlFile.exists()) xmlFile.delete();

        assertThat("Can't run test! Can't delete File:" + xmlFile.file().getAbsolutePath(), !xmlFile.exists());
        LibgdxLogger.initial(xmlFile);
        LibgdxLoggerFactory.reset();
        Logger loggerClass = LoggerFactory.getLogger("loggerClass");
        assertThat("Config must set to default", LibgdxLogger.CONFIG.equals(new LoggerConfig()));
    }

    @Test
    void EISDIR_FileNotFoundExceptionTest() throws IOException {
        FileHandle xmlFile = Gdx.files.local("NoExistFolder" + LibgdxLogger.CONFIGURATION_FILE_XML);
        xmlFile.mkdirs();

        assertThat("xmlFile must a directory for Test", xmlFile.isDirectory());

        LibgdxLogger.initial(xmlFile);
        LibgdxLoggerFactory.reset();
        Logger loggerClass2 = LoggerFactory.getLogger("loggerClass2");
        assertThat("Config must set to default", LibgdxLogger.CONFIG.equals(new LoggerConfig()));

        if (xmlFile.exists()) xmlFile.delete();
    }

    @AfterAll
    static void removeFiles() {
        FileHandle xmlFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE_XML);
        xmlFile.delete();
    }
}