package concurrentcube;

import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;

public class Cube {

    private final int size;
    private final Side[] sides = new Side[6];
    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;

    private int currentAxis = -1; // Axes have numbers 0-2, show() is treated as axis 3.
    private int waitingAxes = 0;
    private int workingRotations = 0;
    private final int[] waitingForAxis = new int[4];

    private final Semaphore mutex = new Semaphore(1, true);
    private final Semaphore[] axisQueues = new Semaphore[4];
    private final Semaphore[] layerQueues;

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        this.size = size;
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;

        // Setting up the sides.
        for (int i = 0; i < 6; i++)
            sides[i] = new Side(size, i);

        // Preparing for concurrency.
        for (int i = 0; i <= 3; i++)
            axisQueues[i] = new Semaphore(0, true);

        layerQueues = new Semaphore[size];
        for (int i = 0; i < size; i++)
            layerQueues[i] = new Semaphore(1, true);
    }

    public void rotate(int side, int layer) throws InterruptedException {
        int axis = CubeUtils.getAxisNumber(side);

        // Entry procedure.
        mutex.acquire();
        if (waitingAxes > 0 || (currentAxis != -1 && currentAxis != axis)) {
            waitingForAxis[axis]++;
            if (waitingForAxis[axis] == 1)
                waitingAxes++;
            mutex.release();
            axisQueues[axis].acquireUninterruptibly();
            waitingForAxis[axis]--;
            if (waitingForAxis[axis] == 0)
                waitingAxes--;
        }

        currentAxis = axis;
        workingRotations++;
        if (waitingForAxis[axis] > 0)
            axisQueues[axis].release();
        else
            mutex.release();

        // Proper rotation.
        int unifiedLayer = CubeUtils.getUnifiedLayerNumber(axis, side, layer, size);
        try {
            if (!Thread.currentThread().isInterrupted()) {
                layerQueues[unifiedLayer].acquire();

                beforeRotation.accept(side, layer);

                if (layer == 0)
                    sides[side].rotateSide(true);
                else if (layer == size - 1)
                    sides[CubeUtils.getOppositeSide(side)].rotateSide(false);

                int[] rotationHelper;
                switch (side) {
                    case 0: // TOP
                        rotationHelper = sides[4].getRow(layer);
                        rotationHelper = sides[3].rotateRow(layer, rotationHelper, false);
                        rotationHelper = sides[2].rotateRow(layer, rotationHelper, false);
                        rotationHelper = sides[1].rotateRow(layer, rotationHelper, false);
                        sides[4].rotateRow(layer, rotationHelper, false);
                        break;
                    case 1: // LEFT
                        rotationHelper = sides[0].getColumn(layer);
                        rotationHelper = sides[2].rotateColumn(layer, rotationHelper, false);
                        rotationHelper = sides[5].rotateColumn(layer, rotationHelper, false);
                        rotationHelper = sides[4].rotateColumn(size - 1 - layer, rotationHelper, true);
                        sides[0].rotateColumn(layer, rotationHelper, true);
                        break;
                    case 2: // FRONT
                        rotationHelper = sides[0].getRow(size - 1 - layer);
                        rotationHelper = sides[3].rotateColumn(layer, rotationHelper, false);
                        rotationHelper = sides[5].rotateRow(layer, rotationHelper, true);
                        rotationHelper = sides[1].rotateColumn(size - 1 - layer, rotationHelper, false);
                        sides[0].rotateRow(size - 1 - layer, rotationHelper, true);
                        break;
                    case 3: // RIGHT
                        rotationHelper = sides[0].getColumn(size - 1 - layer);
                        rotationHelper = sides[4].rotateColumn(layer, rotationHelper, true);
                        rotationHelper = sides[5].rotateColumn(size - 1 - layer, rotationHelper, true);
                        rotationHelper = sides[2].rotateColumn(size - 1 - layer, rotationHelper, false);
                        sides[0].rotateColumn(size - 1 - layer, rotationHelper, false);
                        break;
                    case 4: // BACK
                        rotationHelper = sides[0].getRow(layer);
                        rotationHelper = sides[1].rotateColumn(layer, rotationHelper, true);
                        rotationHelper = sides[5].rotateRow(size - 1 - layer, rotationHelper, false);
                        rotationHelper = sides[3].rotateColumn(size - 1 - layer, rotationHelper, true);
                        sides[0].rotateRow(layer, rotationHelper, false);
                        break;
                    case 5: // BOTTOM
                        rotationHelper = sides[2].getRow(size - 1 - layer);
                        rotationHelper = sides[3].rotateRow(size - 1 - layer, rotationHelper, false);
                        rotationHelper = sides[4].rotateRow(size - 1 - layer, rotationHelper, false);
                        rotationHelper = sides[1].rotateRow(size - 1 - layer, rotationHelper, false);
                        sides[2].rotateRow(size - 1 - layer, rotationHelper, false);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + side);
                }

                afterRotation.accept(side, layer);
                layerQueues[unifiedLayer].release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Exit procedure.
            mutex.acquireUninterruptibly();
            workingRotations--;
            if (workingRotations > 0) {
                mutex.release();
            } else {
                if (waitingAxes > 0) {
                    for (int i = 1; i <= 3; i++) {
                        if (waitingForAxis[(axis + i) % 4] > 0) {
                            axisQueues[(axis + i) % 4].release();
                            break;
                        }
                    }
                } else {
                    currentAxis = -1;
                    mutex.release();
                }
            }
        }

        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();
    }

    public String show() throws InterruptedException {
        // Entry procedure;
        mutex.acquire();
        if (waitingAxes > 0 || (currentAxis != -1 && currentAxis != 3)) {
            waitingForAxis[3]++; // show() is treated as axis 3
            if (waitingForAxis[3] == 1)
                waitingAxes++;
            mutex.release();
            axisQueues[3].acquireUninterruptibly();
            waitingForAxis[3]--;
            if (waitingForAxis[3] == 0)
                waitingAxes--;
        }

        currentAxis = 3;
        workingRotations++;
        if (waitingForAxis[3] > 0)
            axisQueues[3].release();
        else
            mutex.release();

        // Proper show.
        final StringBuilder currentState = new StringBuilder();
        if (!Thread.currentThread().isInterrupted()) {
            beforeShowing.run();

            for (Side side : sides)
                currentState.append(side.toString());

            afterShowing.run();
        }

        // Exit procedure.
        mutex.acquireUninterruptibly();
        workingRotations--;
        if (workingRotations > 0) {
            mutex.release();
        } else {
            if (waitingAxes > 0) {
                for (int i = 0; i <= 2; i++) {
                    if (waitingForAxis[i] > 0) {
                        axisQueues[i].release();
                        break;
                    }
                }
            } else {
                currentAxis = -1;
                mutex.release();
            }
        }

        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();
        else
            return currentState.toString();
    }

}
