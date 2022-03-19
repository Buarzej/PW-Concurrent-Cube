package concurrentcube;

public class CubeUtils {

    public static int getOppositeSide(int side) {
        switch (side) {
            case 0:
                return 5;
            case 1:
                return 3;
            case 2:
                return 4;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 0;
            default:
                throw new IllegalStateException("Unexpected value: " + side);
        }
    }

    public static int getAxisNumber(int side) {
        switch (side) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 0;
            default:
                throw new IllegalStateException("Unexpected value: " + side);
        }
    }

    public static int getUnifiedLayerNumber(int axis, int side, int layer, int size) {
        return axis == side ? layer : size - 1 - layer;
    }
}
