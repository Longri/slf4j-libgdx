/*
 * Copyright (C) 2017 team-cachebox.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.cachebox3.utils.lists;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by Longri on 06.01.2017.
 */
public class ThreadStack<T extends CancelRunable>  {

    private final CB_List<T> items;
    private ExecutorService executor;
    private final int maxItems;
    private boolean isDisposed = false;
    private T actualRunning;

    public ThreadStack() {
        this(1);
    }

    public ThreadStack(int maxItemSize) {
        items = new CB_List<T>();
        maxItems = maxItemSize;
        controlThread.start();
    }

    private Thread controlThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!isDisposed) {
                if (executor == null && !items.isEmpty()) {
                    //start
                    executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r, "ThreadStackExecutor");
                            thread.setDaemon(true);
                            thread.setPriority(Thread.NORM_PRIORITY + 3);
                            return thread;
                        }
                    });
                    synchronized (items) {
                        T item = items.first();
                        items.remove(item);
                        actualRunning = item;
                        executor.execute(item);
                    }
                    executor.shutdown();
                    try {
                        executor.awaitTermination(4, TimeUnit.HOURS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    actualRunning = null;
                    executor = null;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    int getMaxItemSize() {
        return maxItems;
    }

    public void pushAndStart(T runnable) {
        synchronized (items) {
            if (items.size >= maxItems) {
                T item = items.first();
                items.remove(item);
            }
            items.add(runnable);
        }
    }

    public void pushAndStartWithCancelRunning(T runnable) {
        if (actualRunning != null) {
            actualRunning.cancel();
        }
        pushAndStart(runnable);
    }

    public void dispose() {
        isDisposed = true;
        if (executor != null) {
            if (actualRunning != null) {
                actualRunning.cancel();
            }
        }
    }

    public boolean isReadyAndEmpty() {
        synchronized (items) {
            return executor == null && items.isEmpty();
        }
    }
}
