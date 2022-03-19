package concurrentcube.tests;

import concurrentcube.Cube;

import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class RandomInterruptionTest {

    private static final int THREAD_COUNT = 10;
    private static final int ROTATE_COUNT = 1000;

    public static void test(int size) {

        assertTimeout(Duration.ofMillis(THREAD_COUNT * ROTATE_COUNT), () -> {
            Cube cube = new Cube(size,
                    (x, y) -> {
                    },
                    (x, y) -> {
                    },
                    () -> {
                    },
                    () -> {
                    }
            );

            Thread[] threads = new Thread[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                threads[i] = new Thread(() -> {
                    Random r = new Random();
                    for (int j = 0; j < ROTATE_COUNT; j++) {
                        int side = r.nextInt(6);
                        int layer = r.nextInt(size);
                        try {
                            cube.rotate(side, layer);
                        } catch (InterruptedException e) {
                            // Do nothing -- expected behavior.
                        }
                    }
                });
                threads[i].start();
            }

            for (int i = 0; i < THREAD_COUNT; i++)
                threads[i].interrupt();
        });
    }
}
