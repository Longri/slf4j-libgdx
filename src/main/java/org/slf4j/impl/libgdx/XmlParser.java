package org.slf4j.impl.libgdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import org.slf4j.impl.LibgdxLogger;
import org.slf4j.impl.LibgdxLoggerFactory;

import java.io.IOException;

/**
 * Created by Longri on 07.02.2017.
 */
public class XmlParser {

    final static String NULL = "NULL";
    public final static String CONFIG_NAME = "config";

    public static LoggerConfig parseConfig(FileHandle xmlFile) throws IOException {
        LoggerConfig config = new LoggerConfig();

        if (xmlFile == null || xmlFile.isDirectory()) return config;

        Element root = new XmlReader().parse(xmlFile);
        if (root.getName().equals(CONFIG_NAME)) {
            config.DEFAULT_LOG_LEVEL = getInt(root, LibgdxLogger.DEFAULT_LOG_LEVEL_KEY, config.DEFAULT_LOG_LEVEL);

            config.SHOW_DATE_TIME = getBoolean(root, LibgdxLogger.SHOW_DATE_TIME_KEY, config.SHOW_DATE_TIME);
            config.DATE_TIME_FORMAT_STR = getString(root, LibgdxLogger.DATE_TIME_FORMAT_KEY, config.DATE_TIME_FORMAT_STR);
            config.SHOW_THREAD_NAME = getBoolean(root, LibgdxLogger.SHOW_THREAD_NAME_KEY, config.SHOW_THREAD_NAME);
            config.SHOW_LOG_NAME = getBoolean(root, LibgdxLogger.SHOW_LOG_NAME_KEY, config.SHOW_LOG_NAME);
            config.SHOW_SHORT_LOG_NAME = getBoolean(root, LibgdxLogger.SHOW_SHORT_LOG_NAME_KEY, config.SHOW_SHORT_LOG_NAME);
            config.LOG_FILE = getString(root, LibgdxLogger.LOG_FILE_KEY, config.LOG_FILE);
            config.LEVEL_IN_BRACKETS = getBoolean(root, LibgdxLogger.LEVEL_IN_BRACKETS_KEY, config.LEVEL_IN_BRACKETS);

        }

        return config;
    }


    private static int getInt(Element element, String name, int defaultValue) {
        String string = element.get(name, NULL);
        if (string.equals(NULL)) {
            return defaultValue;
        } else {
            //parse int
            int value;
            try {
                value = Integer.valueOf(string);
                return value;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }

    private static boolean getBoolean(Element element, String name, boolean defaultValue) {
        String string = element.get(name, NULL);
        if (string.equals(NULL)) {
            return defaultValue;
        } else {
            //parse boolean
            boolean value;
            try {
                value = Boolean.valueOf(string);
                return value;
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }


    private static String getString(Element element, String name, String defaultValue) {
        return element.get(name, defaultValue);
    }


    public static void parseExcludeInclude(FileHandle xmlFile) throws IOException {

        Element root = new XmlReader().parse(xmlFile);
        if (root.getName().equals(CONFIG_NAME)) {
            LibgdxLoggerFactory.EXCLUDE_LIST.clear();
            LibgdxLoggerFactory.INCLUDE_LIST.clear();

            for (int i = 0, n = root.getChildCount(); i < n; i++) {
                Element ele = root.getChild(i);

                if (ele.getName().equals(LibgdxLogger.INCLUDE)) {
                    String name = ele.getAttribute(LibgdxLogger.CLASS_KEY);
                    LibgdxLoggerFactory.INCLUDE_LIST.add(name);
                } else if (ele.getName().equals(LibgdxLogger.EXCLUDE)) {
                    String name = ele.getAttribute(LibgdxLogger.CLASS_KEY);
                    LibgdxLoggerFactory.EXCLUDE_LIST.add(name);
                }
            }
        }
    }
}
