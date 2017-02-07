package org.slf4j.impl.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationLogger;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.impl.DummyLogApplication;
import org.slf4j.impl.LibgdxLogger;

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
        Gdx.files = new LwjglFiles();
        Gdx.app = new DummyLogApplication();
        Gdx.app.setApplicationLogger(new LwjglApplicationLogger());
    }


    @Test
    void parseConfig() throws IOException {
        FileHandle xmlFile = null;

        xmlFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE_XML);
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


    @AfterAll
    static void removeFiles() {
        Gdx.files = new LwjglFiles();
        Gdx.app = new DummyLogApplication();
        Gdx.app.setApplicationLogger(new LwjglApplicationLogger());
        FileHandle xmlFile = Gdx.files.local(LibgdxLogger.CONFIGURATION_FILE_XML);
        xmlFile.delete();
    }
}