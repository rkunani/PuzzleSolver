package puzzlesolver;

import java.util.HashMap;

/* A 3x3 Rubik's Cube */
public class RubiksCube {

    /* Each CubePosition corresponds to a "sticker" on a Rubik's Cube */
    private class CubePosition {
        String face;
        int position;

        CubePosition(String face, int position) {
            this.face = face;
            this.position = position;
        }

        /* Two CubePositions are equal if they have the same
        FACE and POSITION.
         */
        @Override
        public boolean equals(Object other) {
            if (this.getClass() != other.getClass()) {
                return false;
            }
            CubePosition otherCube = (CubePosition) other;
            boolean faceEqual = this.face.equals(otherCube.face);
            boolean posEqual = this.position == otherCube.position;
            return faceEqual && posEqual;
        }

        /* hashCode defined this way to allow retrieval from cubeMap.
         * Although this may increase collisions, it is not significant
         * due to the small number of items in the HashMap. */
        @Override
        public int hashCode() {
            int stringHash = this.face.hashCode();
            int posHash = this.position;
            return stringHash + posHash;
        }
    }

    private HashMap<CubePosition, Character> cubeMap;

    /* Creates a solved Rubik's Cube. If scrambled == true, scrambles the solved cube. */
    public RubiksCube(boolean scramble) {
        cubeMap = new HashMap<>();
        initializeCubeMap();
        if (scramble) {
            scramble();
        }
    }

    /* Initializes cubeMap to a solved state */
    private void initializeCubeMap() {
        CubePosition toPut;
        for (int i = 1; i <= 9; i +=1) {
            toPut = new CubePosition("white", i);
            cubeMap.put(toPut, 'w');
        }
        for (int i = 1; i <= 9; i +=1) {
            toPut = new CubePosition("orange", i);
            cubeMap.put(toPut, 'o');
        }
        for (int i = 1; i <= 9; i +=1) {
            toPut = new CubePosition("green", i);
            cubeMap.put(toPut, 'g');
        }
        for (int i = 1; i <= 9; i +=1) {
            toPut = new CubePosition("blue", i);
            cubeMap.put(toPut, 'b');
        }
        for (int i = 1; i <= 9; i +=1) {
            toPut = new CubePosition("yellow", i);
            cubeMap.put(toPut, 'y');
        }
        for (int i = 1; i <= 9; i +=1) {
            toPut = new CubePosition("red", i);
            cubeMap.put(toPut, 'r');
        }
    }

    //////////////////////
    /* ROTATION METHODS */
    //////////////////////

    /* Rotates the front face (white) 90 degrees clockwise */
    public void front() {

    }

    /**
     * Returns the number after X in the cycle of consecutive
     * integers from firstNum to lastNum, inclusive.
     */
    private int nextNum(int x, int firstNum, int lastNum) {
        int seqLength = (lastNum - firstNum) + 1;
        return ((x - 1) % seqLength) + firstNum; // (x - 1) because first number is 1, not 0
    }

    ///////////////////
    /* OTHER METHODS */
    ///////////////////

    /* Resets the cube to a solved state */
    public void resetCube() {
        cubeMap.clear();
        new RubiksCube(false);
    }

    /* Undoes the previous move */
    public void undoMove() {
        // keep a Stack of HashMaps, pop off stack?
    }

    /* Scrambles the Rubik's Cube */
    public void scramble() {
        // random number of random rotations to the cube
    }

    /* Prints the current state of the cube */
    public void printCube() {
        char[][] cubeArr =  new char[9][12]; // 9 rows, each with length 12
        initializeWithPlaceholders(cubeArr);
        fillRows(cubeArr);
        print2DArray(cubeArr);
    }

    /* Fills the rows of the 2D array representing the cube */
    private void fillRows(char[][] cubeArr) {
        for (int row = 1; row <= 9; row += 1) {
            if (row <= 3) {
                fillTopOrBottom(cubeArr, row, "orange");
            } else if (row >= 7) {
                fillTopOrBottom(cubeArr, row, "red");
            } else {
                fillMiddle(cubeArr, row);
            }
        }
    }

    /* Fills the top and bottom faces of the cube */
    private void fillTopOrBottom(char[][] cubeArr, int row, String face) {
        if (row == 1 || row == 7) {
            cubeArr[row - 1][3] = cubeMap.get(new CubePosition(face, 5));
            cubeArr[row - 1][4] = cubeMap.get(new CubePosition(face, 2));
            cubeArr[row - 1][5] = cubeMap.get(new CubePosition(face, 6));
        }
        if (row == 2 || row == 8) {
            cubeArr[row - 1][3] = cubeMap.get(new CubePosition(face, 1));
            cubeArr[row - 1][4] = cubeMap.get(new CubePosition(face, 9));
            cubeArr[row - 1][5] = cubeMap.get(new CubePosition(face, 3));
        }
        if (row == 3 || row == 9) {
            cubeArr[row - 1][3] = cubeMap.get(new CubePosition(face, 8));
            cubeArr[row - 1][4] = cubeMap.get(new CubePosition(face, 4));
            cubeArr[row - 1][5] = cubeMap.get(new CubePosition(face, 7));
        }
    }

    /* Fills the middle faces of the cube */
    private void fillMiddle(char[][] cubeArr, int row) {
        ThreeDigitIterator tdi;
        if (row == 4) {
            tdi = new ThreeDigitIterator(5, 2, 6);
            fillRow(cubeArr, tdi, row);
        }
        if (row == 5) {
            tdi = new ThreeDigitIterator(1, 9, 3);
            fillRow(cubeArr, tdi, row);
        }
        if (row == 6) {
            tdi = new ThreeDigitIterator(8, 4, 7);
            fillRow(cubeArr, tdi, row);
        }
    }

    /* Helps with segmentation of middle faces */
    private void fillRow(char[][] cubeArr, ThreeDigitIterator tdi, int row) {
        for (int i = 0; i < 12; i += 1) {
            if (i <= 2) {
                cubeArr[row - 1][i] = cubeMap.get(new CubePosition("green", tdi.next()));
            } else if (i <= 5) {
                cubeArr[row - 1][i] = cubeMap.get(new CubePosition("white", tdi.next()));
            } else if (i <= 8) {
                cubeArr[row - 1][i] = cubeMap.get(new CubePosition("blue", tdi.next()));
            } else {
                cubeArr[row - 1][i] = cubeMap.get(new CubePosition("yellow", tdi.next()));
            }
        }
    }

    /* Initializes the 2D array to all empty chars as placeholders */
    private void initializeWithPlaceholders(char[][] arr) {
        for (int row = 0; row < arr.length; row += 1) {
            for (int index = 0; index < arr[row].length; index += 1) {
                arr[row][index] = '-';
            }
        }
    }

    /* Prints a 2D char array */
    private void print2DArray(char[][] arr) {
        for (char[] row: arr) {
            for (char c: row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

}
