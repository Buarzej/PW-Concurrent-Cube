package concurrentcube;

public class Side {
    private final int size;
    private final int[][] blocks;

    public Side(int size, int color) {
        this.size = size;
        this.blocks = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                blocks[i][j] = color;
    }

    public int[] getRow(int row) {
        int[] obtainedRow = new int[size];

        System.arraycopy(blocks[row], 0, obtainedRow, 0, size);

        return obtainedRow;
    }

    public int[] getColumn(int column) {
        int[] obtainedColumn = new int[size];

        for (int i = 0; i < size; i++)
            obtainedColumn[i] = blocks[i][column];

        return obtainedColumn;
    }

    private void swap(int oldX, int oldY, int newX, int newY) {
        int temp = blocks[oldX][oldY];
        blocks[oldX][oldY] = blocks[newX][newY];
        blocks[newX][newY] = temp;
    }

    public int[] rotateRow(int rowNumber, int[] newRow, boolean reversed) {
        int[] oldRow = getRow(rowNumber);

        for (int i = 0; i < size; i++)
            if (reversed)
                blocks[rowNumber][size - 1 - i] = newRow[i];
            else
                blocks[rowNumber][i] = newRow[i];

        return oldRow;
    }

    public int[] rotateColumn(int columnNumber, int[] newColumn, boolean reversed) {
        int[] oldColumn = getColumn(columnNumber);

        for (int i = 0; i < size; i++)
            if (reversed)
                blocks[size - 1 - i][columnNumber] = newColumn[i];
            else
                blocks[i][columnNumber] = newColumn[i];

        return oldColumn;
    }

    public void rotateSide(boolean clockwise) {
        // Transpose the side.
        for (int i = 0; i < size; i++)
            for (int j = i + 1; j < size; j++)
                swap(i, j, j, i);

        if (clockwise) {
            // Flip the side horizontally.
            for (int i = 0; i < size; i++) {
                int start = 0, end = size - 1;
                while (start < end)
                    swap(i, start++, i, end--);
            }
        } else {
            // Flip the side vertically.
            for (int i = 0; i < size; i++) {
                int start = 0, end = size - 1;
                while (start < end)
                    swap(start++, i, end--, i);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                sb.append(blocks[i][j]);

        return sb.toString();
    }
}
