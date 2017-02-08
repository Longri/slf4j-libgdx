/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An implementation of {@link ILoggerFactory} which always returns
 * {@link LibgdxLogger} instances.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LibgdxLoggerFactory implements ILoggerFactory {

    public static final HashSet<String> EXCLUDE_LIST = new HashSet<String>();
    public static final HashSet<String> INCLUDE_LIST = new HashSet<String>();
    static final ObjectMap<String, LibgdxLogger> loggerMap = new ObjectMap<String, LibgdxLogger>();
    private static final LibgdxLogger EMPTY_LOGGER = new EmptyLogger();

    /**
     * Return an appropriate {@link LibgdxLogger} instance by name.
     */
    public Logger getLogger(String name) {
        return getInstance(name);
    }

    static LibgdxLogger getInstance(String name) {

        // return empty logger if the name on disable list
        if (!INCLUDE_LIST.isEmpty()) {
            if (!INCLUDE_LIST.contains(name)) return EMPTY_LOGGER;
        }

        if (!EXCLUDE_LIST.isEmpty()) {
            if (EXCLUDE_LIST.contains(name)) return EMPTY_LOGGER;
        }

        LibgdxLogger simpleLogger = loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            LibgdxLogger newInstance = (Gdx.app == null || Gdx.files == null) ? new PendingLogger(name) : new LibgdxLogger(name);
            LibgdxLogger oldInstance = loggerMap.put(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    /**
     * Clear the internal logger cache.
     * <p>
     * This method is intended to be called by classes (in the same package) for
     * testing purposes. This method is internal. It can be modified, renamed or
     * removed at any time without notice.
     * <p>
     * You are strongly discouraged from calling this method in production code.
     */
    public static void reset() {
        loggerMap.clear();
    }
}
