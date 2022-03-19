package concurrentcube.tests;

import concurrentcube.Cube;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class OppositeSidesRotationsTest {

    private static final int SLEEP_TIME = 300;

    public static void test() {
        var counter = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };

        Cube cube = new Cube(6,
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
        Thread[] threads = new Thread[6];
        for (int i = 0; i < 6; i++) {
            int side = i < 3 ? 0 : 5;
            int layer = i % 3;
            threads[i] = new Thread(() -> {
                try {
                    cube.rotate(side, layer);
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

        assertEquals(12, counter.value.get());
        assertTrue(endTime - startTime >= SLEEP_TIME);
        assertTrue(endTime - startTime < 2 * SLEEP_TIME);
    }
}
