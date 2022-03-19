package concurrentcube.tests;

import concurrentcube.Cube;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class DifferentSidesRotationsTest {

    private static final int SLEEP_TIME = 50;

    public static void test() {
        var counter = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };

        Cube cube = new Cube(3,
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
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            int side = i;
            threads[i] = new Thread(() -> {
                try {
                    cube.rotate(side, 0);
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

        assertEquals(6, counter.value.get());
        assertTrue(endTime - startTime >= 3 * SLEEP_TIME);
    }
}
