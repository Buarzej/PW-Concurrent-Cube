package concurrentcube.tests;

import concurrentcube.Cube;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleCorrectnessTest {

    public static void test() throws InterruptedException {
        var counter = new Object() {
            final AtomicInteger value = new AtomicInteger(0);
        };

        Cube cube3 = new Cube(3,
                (x, y) -> counter.value.incrementAndGet(),
                (x, y) -> counter.value.incrementAndGet(),
                counter.value::incrementAndGet,
                counter.value::incrementAndGet
        );

        Cube cube4 = new Cube(4,
                (x, y) -> counter.value.incrementAndGet(),
                (x, y) -> counter.value.incrementAndGet(),
                counter.value::incrementAndGet,
                counter.value::incrementAndGet
        );

        // Checking for correct whole side rotation.
        cube3.rotate(1, 0);
        cube3.rotate(0, 0); // whole top side rotation
        assertEquals("444000000022111111333022022445333333111445445255255255", cube3.show());
        cube3.rotate(0, 2); // whole bottom side rotation
        assertEquals("444000000022111022333022333445333445111445111555555222", cube3.show());

        cube4.rotate(1, 0);
        cube4.rotate(0, 0); // whole top side rotation
        assertEquals("444400000000000002221111111111113333022202220222444533333333333311114445444544452555255525552555", cube4.show());
        cube4.rotate(0, 3); // whole bottom side rotation
        assertEquals("444400000000000002221111111102223333022202223333444533333333444511114445444511115555555555552222", cube4.show());

        // Rotating all sides.
        String[] correct3 = {"004004004333111022445022333111333445022445111555555222",
                "104504204013213213045022033111333445022445115455055322",
                "104504333014215215000324325211033445022445115431055322",
                "100504335014215215001325322402431531322445415431054320",
                "211504335014015115001325322400432533443142552431054022",
                "211504335014015552001325115400432322443142533004253241"};
        assertDoesNotThrow(() -> {
            for (int side = 0; side < 6; side++) {
                cube3.rotate(side, 0);
                assertEquals(correct3[side], cube3.show());
            }
        });

        String[] correct4 = {"000400040004000433331111111102224445022202223333111133333333444502224445444511115555555555552222",
                "100450045004200401132113211321130445022202220333111133333333444502224445444511154555055505553222",
                "100450045004333301142115211521150000322432243225211103330333444502224445444511154331055505553222",
                "100050045004333501142115211521150001322532253222400243314331533132224445444541154331055405543220",
                "211150045004333501140115011511150001322532253222400043324332533344431442144255524331055405540222",
                "211150045004333501140115011555520001322532251115400043324332322244431442144253330004255325532441"};
        assertDoesNotThrow(() -> {
            for (int side = 0; side < 6; side++) {
                cube4.rotate(side, 0);
                assertEquals(correct4[side], cube4.show());
            }
        });

        // Testing if all rotations and shows ended correctly.
        assertEquals(68, counter.value.get());
    }
}
