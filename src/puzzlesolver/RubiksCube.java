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
}
