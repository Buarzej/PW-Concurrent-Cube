package concurrentcube.tests;

import concurrentcube.Cube;
import concurrentcube.CubeUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllPossibleRotationsCorrectnessTest {

    public static void test(int size) {
        var counter = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };

        Cube rotatingCube = new Cube(size,
                (x, y) -> counter.value.incrementAndGet(),
                (x, y) -> counter.value.incrementAndGet(),
                counter.value::incrementAndGet,
                counter.value::incrementAndGet
        );

        Cube defaultCube = new Cube(size,
                (x, y) -> counter.value.incrementAndGet(),
                (x, y) -> counter.value.incrementAndGet(),
                counter.value::incrementAndGet,
                counter.value::incrementAndGet
        );

        // Testing all variants of rotations back and forth.
        assertDoesNotThrow(() -> {
            for (int side = 0; side < 6; side++) {
                for (int layer = 0; layer < size; layer++) {
                    rotatingCube.rotate(side, layer);
                    rotatingCube.rotate(CubeUtils.getOppositeSide(side), size - 1 - layer);
                }
            }
        });

        assertDoesNotThrow(() -> {
            assertEquals(defaultCube.show(), rotatingCube.show());
        });

        // Testing if all rotations and shows ended correctly.
        assertEquals(24 * size + 4, counter.value.get());
    }
}
