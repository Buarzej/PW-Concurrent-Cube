package concurrentcube.tests;

import concurrentcube.Cube;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class SameSideRotatesTest {

    private static final int SLEEP_TIME = 300;

    public static void test(int size) {
        var counter = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };

        Cube cube = new Cube(size,
                (x, y) -> {
                    try {
                        Thread.sleep(SLEEP_TIME);
                        counter.value.incrementAndGet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                },
                (x, y) -> counter.value.incrementAndGet(),
                () -> {
                },
                () -> {
                }
        );

        long startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            int layer = i;
            threads[i] = new Thread(() -> {
                try {
                    cube.rotate(0, layer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        assertDoesNotThrow(() -> {
            for (Thread thread : threads)
                thread.join();
        });
        long endTime = System.currentTimeMillis();

        assertEquals(2 * size, counter.value.get());
        assertTrue(endTime - startTime >= SLEEP_TIME);
        assertTrue(endTime - startTime < (long) (size / 2) * SLEEP_TIME);
    }
}
