package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    // فقط این مقدار را تغییر بده
    private static final int LEAK_TYPE = 3;


    // ============================================================
    // Leak #1 - Static Cache
    // ============================================================

    private static final List<byte[]> STATIC_CACHE_LEAK =
            new ArrayList<>();


    // ============================================================
    // Leak #2 - Listener
    // ============================================================

    private static final List<MyListener> LISTENER_LEAK =
            new ArrayList<>();


    // ============================================================
    // Leak #3 - ThreadLocal
    // ============================================================

    private static final ThreadLocal<byte[]> THREAD_LOCAL_LEAK =
            new ThreadLocal<>();


    // ============================================================
    // Leak #4 - Unbounded Map
    // ============================================================

    private static final Map<String, byte[]> UNBOUNDED_MAP_LEAK =
            new HashMap<>();


    // ============================================================
    // Leak #5 - Long Running Thread
    // ============================================================

    private static final List<Thread> LONG_RUNNING_THREAD_LEAK =
            new ArrayList<>();


    public static void main(String[] args) throws Exception {

        System.out.println("Application started");
        System.out.println("LEAK_TYPE = " + LEAK_TYPE);

        if (LEAK_TYPE == 1) {

            runStaticCacheLeak();

        } else if (LEAK_TYPE == 2) {

            runListenerLeak();

        } else if (LEAK_TYPE == 3) {

            runThreadLocalLeak();

        } else if (LEAK_TYPE == 4) {

            runUnboundedMapLeak();

        } else if (LEAK_TYPE == 5) {

            runLongRunningThreadLeak();

        } else {

            System.out.println("Unknown LEAK_TYPE");
        }
    }


    // ============================================================
    // 1. STATIC CACHE LEAK
    // ============================================================

    private static void runStaticCacheLeak() throws Exception {

        System.out.println("Running STATIC_CACHE_LEAK");

        while (true) {

            STATIC_CACHE_LEAK.add(
                    new byte[1024 * 1024]
            );

            System.out.println(
                    "STATIC_CACHE_LEAK size = "
                            + STATIC_CACHE_LEAK.size()
                            + " MB"
            );

            Thread.sleep(100);
        }
    }


    // ============================================================
    // 2. LISTENER LEAK
    // ============================================================

    private static void runListenerLeak() throws Exception {

        System.out.println("Running LISTENER_LEAK");

        while (true) {

            for (int i = 0; i < 100; i++) {

                MyListener listener =
                        new MyListener(
                                "LISTENER_OBJECT_" + i,
                                new byte[1024 * 100]
                        );

                LISTENER_LEAK.add(listener);
            }

            System.out.println(
                    "LISTENER_LEAK size = "
                            + LISTENER_LEAK.size()
            );

            Thread.sleep(500);
        }
    }


    // ============================================================
    // 3. THREAD LOCAL LEAK
    // ============================================================

    private static void runThreadLocalLeak() throws Exception {

        System.out.println("Running THREAD_LOCAL_LEAK");

        Thread thread = new Thread(() -> {

            THREAD_LOCAL_LEAK.set(
                    new byte[50 * 1024 * 1024]
            );

            System.out.println(
                    "THREAD_LOCAL_LEAK created"
            );

            try {

                // Thread زنده می‌ماند
                Thread.sleep(Long.MAX_VALUE);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
        });

        thread.setName("THREAD_LOCAL_LEAK_THREAD");

        thread.start();

        // main را زنده نگه می‌داریم
        Thread.sleep(Long.MAX_VALUE);
    }


    // ============================================================
    // 4. UNBOUNDED MAP LEAK
    // ============================================================

    private static void runUnboundedMapLeak() throws Exception {

        System.out.println("Running UNBOUNDED_MAP_LEAK");

        long counter = 0;

        while (true) {

            String key =
                    "UNBOUNDED_MAP_KEY_" + counter++;

            UNBOUNDED_MAP_LEAK.put(
                    key,
                    new byte[1024 * 512]
            );

            if (UNBOUNDED_MAP_LEAK.size() % 10 == 0) {

                System.out.println(
                        "UNBOUNDED_MAP_LEAK size = "
                                + UNBOUNDED_MAP_LEAK.size()
                );
            }

            Thread.sleep(100);
        }
    }


    // ============================================================
    // 5. LONG RUNNING THREAD LEAK
    // ============================================================

    private static void runLongRunningThreadLeak()
            throws Exception {

        System.out.println(
                "Running LONG_RUNNING_THREAD_LEAK"
        );

        int counter = 0;

        while (true) {

            Thread thread = new Thread(() -> {

                try {

                    Thread.sleep(Long.MAX_VALUE);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            });

            thread.setName(
                    "LONG_RUNNING_THREAD_LEAK_"
                            + counter++
            );

            LONG_RUNNING_THREAD_LEAK.add(thread);

            thread.start();

            System.out.println(
                    "LONG_RUNNING_THREAD_LEAK threads = "
                            + LONG_RUNNING_THREAD_LEAK.size()
            );

            Thread.sleep(1000);
        }
    }


    // ============================================================
    // Listener
    // ============================================================

    private static class MyListener {

        private final String listenerName;

        private final byte[] listenerData;

        public MyListener(
                String listenerName,
                byte[] listenerData
        ) {
            this.listenerName = listenerName;
            this.listenerData = listenerData;
        }
    }
}