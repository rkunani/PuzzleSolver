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

    /* Returns the edge after POS when rotating in the given DIRECTION */
    public int nextEdge(String rotatingFace, String nextFace, int pos, String direction) {
        if (rotatingFace.equals("white") || rotatingFace.equals(nextFace)) {
            int arrIndex = pos - 1;
            int nextIndex;
            if (direction.equals("cw")) {
                nextIndex = plusOne(arrIndex);
            } else { // counterclockwise rotation
                nextIndex = minusOne(arrIndex);
            }
            return edgeArr[nextIndex];
        } else if (rotatingFace.equals("yellow")) {
            int arrIndex = pos - 1;
            int nextIndex;
            if (direction.equals("cw")) {
                nextIndex = minusOne(arrIndex);
            } else {
                nextIndex = plusOne(arrIndex);
            }
            return edgeArr[nextIndex];
        } else if (rotatingFace.equals("blue")) {
            if (nextFace.equals("yellow")) {
                return 1;
            } else {
                return 3;
            }
        } else if (rotatingFace.equals("green")) {
            if (nextFace.equals("yellow")) {
                return 3;
            } else {
                return 1;
            }
        } else { // positions 2 and 4 are not inverted on the yellow side
            return pos;
        }
    }

    /* Returns the corner after POS when rotating in the given DIRECTION */
    public int nextCorner(String rotatingFace, String nextFace, int pos, String direction) {
        if (rotatingFace.equals("white") || rotatingFace.equals(nextFace)) {
            int arrIndex = pos - 5;
            int nextIndex;
            if (direction.equals("cw")) {
                nextIndex = plusOne(arrIndex);
            } else { // counterclockwise rotation
                nextIndex = minusOne(arrIndex);
            }
            return cornerArr[nextIndex];
        } else if (rotatingFace.equals("yellow")) {
            int arrIndex = pos - 5;
            int nextIndex;
            if (direction.equals("cw")) {
                nextIndex = minusOne(arrIndex);
            } else { // counterclockwise rotation
                nextIndex = plusOne(arrIndex);
            }
            return cornerArr[nextIndex];
        } else if (rotatingFace.equals("blue")) {
            if (nextFace.equals("yellow")) {
                return other(pos);
            } else if (nextFace.equals("orange") && direction.equals("ccw")) {
                return other(pos);
            } else if (nextFace.equals("red") && direction.equals("cw")) {
                return other(pos);
            } else {
                return pos;
            }
        } else if (rotatingFace.equals("green")) {
            if (nextFace.equals("yellow")) {
                return other(pos);
            } else if (nextFace.equals("orange") && direction.equals("cw")) {
                return other(pos);
            } else if (nextFace.equals("red") && direction.equals("ccw")) {
                return other(pos);
            } else {
                return pos;
            }
        } else {
            return pos;
        }
    }

    private int plusOne(int index) {
        return (index + 1) % edgeArr.length; // arbitrarily choice, both arrays same length
    }

    private int minusOne(int index) {
        if (index - 1 >= 0) {
            return index - 1;
        }
        return (index - 1) + edgeArr.length;
    }

    private int other(int x) {
        if (x == 6) {
            return 8;
        } else if (x == 8) {
            return 6;
        } else if (x == 5) {
            return 7;
        } else {
            return 5;
        }
    }
}
