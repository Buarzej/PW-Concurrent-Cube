package concurrentcube;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import concurrentcube.tests.*;

class CubeTest {

    @Test
    @DisplayName("Tests rotation and show logic correctness.")
    void simpleCorrectnessTest() throws InterruptedException {
        SimpleCorrectnessTest.test();
    }

    @ParameterizedTest
    @DisplayName("Runs all possible rotations back and forth on cube of given size.")
    @ValueSource(ints = {9, 10})
    void allPossibleRotationsCorrectnessTest(int size) {
        AllPossibleRotationsCorrectnessTest.test(size);
    }

    @Test
    @DisplayName("Tests whether multiple shows are run concurrently.")
    void concurrentShowsTest() {
        ConcurrentShowsTest.test();
    }

    @Test
    @DisplayName("Tests whether multiple rotations of the same layer are run sequentially.")
    void sameLayerRotationsTest() {
        SameLayerRotationsTest.test();
    }

    @Test
    @DisplayName("Tests whether rotations on sides in different axes are run sequentially.")
    void differentSidesRotationsTest() {
        DifferentSidesRotationsTest.test();
    }

    @Test
    @DisplayName("Tests whether rotations on opposite sides axes are run concurrently.")
    void oppositeSidesRotationsTest() {
        OppositeSidesRotationsTest.test();
    }

    @ParameterizedTest
    @DisplayName("Tests whether multiple rotations on the same side are run concurrently.")
    @ValueSource(ints = {9, 10})
    void sameSideRotatesTest(int size) {
        SameSideRotatesTest.test(size);
    }

    @ParameterizedTest
    @DisplayName("Checks for proper thread interruption handling.")
    @ValueSource(ints = {9, 10})
    void randomInterruptionTest(int size) { RandomInterruptionTest.test(size); }
}