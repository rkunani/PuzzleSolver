package puzzlesolver;

public class PieceSequence {
    private int[] edgeArr;
    private int[] cornerArr;

    public PieceSequence() {
        edgeArr = new int[4];
        for (int i = 0; i < edgeArr.length; i += 1) {
            edgeArr[i] = i + 1;
        }
        cornerArr = new int[4];
        for (int i = 0; i < cornerArr.length; i += 1) {
            cornerArr[i] = i + 5;
        }
    }

    public int nextEdge(int pos, String direction) {
        int arrIndex = pos - 1;
        int nextIndex;
        if (direction.equals("cw")) {
            nextIndex = plusOne(arrIndex);
        } else { // counterclockwise rotation
            nextIndex = minusOne(arrIndex);
        }
        return edgeArr[nextIndex];
    }

    public int nextCorner(int pos, String direction) {
        int arrIndex = pos - 5;
        int nextIndex;
        if (direction.equals("cw")) {
            nextIndex = plusOne(arrIndex);
        } else { // counterclockwise rotation
            nextIndex = minusOne(arrIndex);
        }
        return cornerArr[nextIndex];
    }

    private int plusOne(int index) {
        return (index + 1) % edgeArr.length; // arbitrarily chose edgeArr.length (both have same length)
    }

    private int minusOne(int index) {
        if (index - 1 >= 0) {
            return index - 1;
        }
        return (index - 1) + edgeArr.length;
    }
}
