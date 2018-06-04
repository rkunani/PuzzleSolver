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

        /* HashCode defined this way to allow retrieval from cubeMap */
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
        if (scramble) {
            scramble();
        }
    }

    //////////////////////
    /* ROTATION METHODS */
    //////////////////////

    /* Rotates the front face (white) 90 degrees clockwise */
    public void front() {
        HashMap<CubePosition, Character> temp = new HashMap<>();

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

    }
}
