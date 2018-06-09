package puzzlesolver;

import javax.annotation.processing.SupportedSourceVersion;
import java.util.*;

/* A 3x3 Rubik's Cube */
public class RubiksCube implements PuzzleState {

    /* Each CubePosition corresponds to a static location on a Rubik's Cube
     * (e.g. the bottom left square on the orange face) */
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

    private HashMap<CubePosition, Character> cubeMap; // core representation of the cube
    private char[][] cubeArr;
    private static PieceSequence ps;
    private static HashMap<String, FaceSequence> faceMap;
    // private list of strings to keep track of previous moves
    private static HashMap<String, List<CubePosition>> adjCubePositions;
    private static HashMap<Integer, Move> moveMap;
    private Stack<char[][]> moveStack;
    private static Random myRandom;

    /* Creates a solved Rubik's Cube */
    public RubiksCube() {
        cubeMap = new HashMap<>();
        initializeCubeMap();
        makeCube();
    }

    /* Creates a Rubik's Cube with the same state as RC */
    public RubiksCube(RubiksCube rc) {
        transferNonStaticFieldsFrom(rc);
    }

    ///////////////////////
    /* ROTATING THE CUBE */
    ///////////////////////

    /* Rotates the given FACE in the given DIRECTION */
    public void rotate(String face, String direction) {
        HashMap<CubePosition, Character> storageMap = fillStorageMap(face);
        for (int i = 1; i <= 8; i += 1) { // update face values
            int nextPos;
            char value;
            if (i <= 4) { // edge
                nextPos = ps.nextEdge(face, face, i, direction);
                value = storageMap.get(new CubePosition(face, i));
            } else { // corner
                value = storageMap.get(new CubePosition(face, i));
                nextPos = ps.nextCorner(face, face, i, direction);
            }
            cubeMap.put(new CubePosition(face, nextPos), value);
        }
        List<CubePosition> adjPositions = adjCubePositions.get(face);
        for (CubePosition cp: adjPositions) { // update adjacent face values
            String oldFace = cp.face;
            int pos = cp.position;
            String nextFace = faceMap.get(face).nextFace(oldFace, direction);
            int nextPos;
            if (pos <= 4) {
                nextPos = ps.nextEdge(face, nextFace, pos, direction);
            } else {
                nextPos = ps.nextCorner(face, nextFace, pos, direction);
            }
            char value = storageMap.get(cp);
            cubeMap.put(new CubePosition(nextFace, nextPos), value);
        }
        updateCubeArr();
        moveStack.push(deepCopy(cubeArr)); // invariant: top element of the stack is always the current state
    }

    private HashMap<CubePosition, Character> fillStorageMap(String face) {
        HashMap<CubePosition, Character> storageMap = new HashMap<>();
        for (int i = 1; i <= 8; i += 1) { // copy face values
            CubePosition cp = new CubePosition(face, i);
            char value = cubeMap.get(cp);
            storageMap.put(cp, value);
        }
        List<CubePosition> adjPositions = adjCubePositions.get(face);
        for (CubePosition adjPosition: adjPositions) { // copy adjacent position values
            char value = cubeMap.get(adjPosition);
            storageMap.put(adjPosition, value);
        }
        return storageMap;
    }

    /**
     * Makes a deep copy of a 2D array
     * @source https://stackoverflow.com/questions/1564832/how-do-i-do-a-deep-copy-of-a-2d-array-in-java
     */
    private char[][] deepCopy(char[][] arr) {
        char[][] copy = new char[arr.length][];
        for (int i = 0; i < arr.length; i += 1) {
            copy[i] = Arrays.copyOf(arr[i], arr[i].length);
        }
        return copy;
    }

    /////////////////////////////////////////////
    /* IMPLEMENTING THE PUZZLE STATE INTERFACE */
    /////////////////////////////////////////////

    public Iterable<PuzzleState> adjacentStates() {
        List<PuzzleState> adjacent = new ArrayList<>();
        for (int i = 1; i <= 12; i += 1) {
            Move move = moveMap.get(i);
            String face = move.face;
            String direction = move.direction;
            rotate(face, direction);
            adjacent.add(new RubiksCube(this));
            undoMove();
        }
        return adjacent;
    }

    public int distToSolved() {
        Set<CubePosition> cubePositions = cubeMap.keySet();
        int numOutOfPlace = 0;
        for (CubePosition cp: cubePositions) {
            char value = cubeMap.get(cp);
            String face = cp.face;
            if (face.charAt(0) != value) {
                numOutOfPlace += 1;
            }
        }
        return numOutOfPlace;
    }

    public void printState() {
        printCube();
    }

    public boolean equals(PuzzleState other) {
        RubiksCube otherCube = (RubiksCube) other;
        return this.cubeMap.equals(otherCube.cubeMap);
    }

    //////////////////////
    /* OTHER OPERATIONS */
    //////////////////////

    /* Resets the cube to a solved state */
    public void resetCube() {
        // idk how to do this
    }

    /* Undoes the previous move */
    public void undoMove() {
        if (isSolved()) {
            return;
        }
        moveStack.pop(); // removes the current state from the stack
        cubeArr = moveStack.peek(); // restores the state before the current state
        updateCubeMap();
    }

    /* Scrambles the Rubik's Cube with a minimum
     * of 15 moves and a maximum of 25 moves */
    public void scramble() {
        int numMoves = myRandom.nextInt(25) + 15;
        for (int i = 0; i < numMoves; i += 1) {
            int moveNum = myRandom.nextInt(12) + 1;
            Move move = moveMap.get(moveNum);
            String face = move.face;
            String direction = move.direction;
            rotate(face, direction);
        }
    }

    ///////////////////////////////////
    /* UPDATING THE CUBE ARRAY & MAP */
    ///////////////////////////////////

    /* Updates cubeArr to match cubeMap */
    private void updateCubeArr() {
        fillRows(cubeArr);
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

    /* Updates cubeMap to match cubeArr */
    private void updateCubeMap() {
        for (int row = 0; row < 9; row += 1) {
            for (int col = 0; col < 12; col += 1) {
                if (cubeArr[row][col] == '-') {
                    continue;
                } else if (row <= 2) {
                    putInCubeMap("orange", row, col);
                } else if (row >= 6) {
                    putInCubeMap("red", row, col);
                } else if (col <= 2) {
                    putInCubeMap("green", row, col);
                } else if (col <= 5) {
                    putInCubeMap("white", row, col);
                } else if (col <= 8) {
                    putInCubeMap("blue", row, col);
                } else if (col <= 11) {
                    putInCubeMap("yellow", row, col);
                }
            }
        }
    }

    private void putInCubeMap(String face, int row, int col) {
        if (row == 0 || row == 6 || row == 3) {
            if (col == 3 || col == 0 || col == 6 || col == 9) {
                cubeMap.put(new CubePosition(face, 5), cubeArr[row][col]);
            } else if (col == 4 || col == 1 || col == 7 || col == 10) {
                cubeMap.put(new CubePosition(face, 2), cubeArr[row][col]);
            } else if (col == 5 || col == 2 || col == 8 || col == 11) {
                cubeMap.put(new CubePosition(face, 6), cubeArr[row][col]);
            }
        } else if (row == 1 || row == 7 || row == 4) {
            if (col == 3 || col == 0 || col == 6 || col == 9) {
                cubeMap.put(new CubePosition(face, 1), cubeArr[row][col]);
            } else if (col == 4 || col == 1 || col == 7 || col == 10) {
                cubeMap.put(new CubePosition(face, 9), cubeArr[row][col]);
            } else if (col == 5 || col == 2 || col == 8 || col == 11) {
                cubeMap.put(new CubePosition(face, 3), cubeArr[row][col]);
            }
        } else if (row == 2 || row == 8 || row == 5) {
            if (col == 3 || col == 0 || col == 6 || col == 9) {
                cubeMap.put(new CubePosition(face, 8), cubeArr[row][col]);
            } else if (col == 4 || col == 1 || col == 7 || col == 10) {
                cubeMap.put(new CubePosition(face, 4), cubeArr[row][col]);
            } else if (col == 5 || col == 2 || col == 8 || col == 11) {
                cubeMap.put(new CubePosition(face, 7), cubeArr[row][col]);
            }
        }
    }

    ///////////////////////
    /* PRINTING THE CUBE */
    ///////////////////////

    /* Prints the current state of the cube */
    public void printCube() {
        System.out.println("Current state of the cube:");
        print2DArray(cubeArr);
        System.out.println();
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

    ////////////////////////////////
    /* CONSTRUCTOR HELPER METHODS */
    ////////////////////////////////

    /* Initializes cubeMap to a solved state */
    private void initializeCubeMap() {
        CubePosition toPut;
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubePosition("white", i);
            cubeMap.put(toPut, 'w');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubePosition("orange", i);
            cubeMap.put(toPut, 'o');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubePosition("green", i);
            cubeMap.put(toPut, 'g');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubePosition("blue", i);
            cubeMap.put(toPut, 'b');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubePosition("yellow", i);
            cubeMap.put(toPut, 'y');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubePosition("red", i);
            cubeMap.put(toPut, 'r');
        }
    }

    /* Appropriately fills faceMap */
    private static void populateFaceMap() {
        faceMap.put("white", new FaceSequence("orange", "blue", "red", "green"));
        faceMap.put("orange", new FaceSequence("yellow", "blue", "white", "green"));
        faceMap.put("yellow", new FaceSequence("red", "blue", "orange", "green"));
        faceMap.put("red", new FaceSequence("white", "blue", "yellow", "green"));
        faceMap.put("green", new FaceSequence("white", "red", "yellow", "orange"));
        faceMap.put("blue", new FaceSequence("white", "orange", "yellow", "red"));
    }

    /* Appropriately fills adjCubePositions */
    private void populateAdjCubePositions() {
        // white
        List<CubePosition> whiteList = new ArrayList<>();
        whiteList.add(new CubePosition("orange", 8));
        whiteList.add(new CubePosition("orange", 4));
        whiteList.add(new CubePosition("orange", 7));
        whiteList.add(new CubePosition("blue", 5));
        whiteList.add(new CubePosition("blue", 1));
        whiteList.add(new CubePosition("blue", 8));
        whiteList.add(new CubePosition("red", 6));
        whiteList.add(new CubePosition("red", 2));
        whiteList.add(new CubePosition("red", 5));
        whiteList.add(new CubePosition("green", 7));
        whiteList.add(new CubePosition("green", 3));
        whiteList.add(new CubePosition("green", 6));
        adjCubePositions.put("white", whiteList);
        // green
        List<CubePosition> greenList = new ArrayList<>();
        ThreeDigitIterator tdi = new ThreeDigitIterator(5, 1, 8);
        List<String> greenAdjFaces = faceMap.get("green").toList();
        for (String adjFace: greenAdjFaces) {
            if (adjFace.equals("yellow")) {
                greenList.add(new CubePosition("yellow", 6));
                greenList.add(new CubePosition("yellow", 3));
                greenList.add(new CubePosition("yellow", 7));
            } else {
                for (int i = 0; i < 3; i += 1) {
                    greenList.add(new CubePosition(adjFace, tdi.next()));
                }
            }
        }
        adjCubePositions.put("green", greenList);
        // orange
        List<CubePosition> orangeList = new ArrayList<>();
        tdi = new ThreeDigitIterator(5, 2, 6);
        List<String> orangeAdjFaces = faceMap.get("orange").toList();
        for (String adjFace: orangeAdjFaces) {
            for (int i = 0; i < 3; i += 1) {
                orangeList.add(new CubePosition(adjFace, tdi.next()));
            }
        }
        adjCubePositions.put("orange", orangeList);
        // red
        List<CubePosition> redList = new ArrayList<>();
        tdi = new ThreeDigitIterator(8, 4, 7);
        List<String> redAdjFaces = faceMap.get("red").toList();
        for (String adjFace: redAdjFaces) {
            for (int i = 0; i < 3; i += 1) {
                redList.add(new CubePosition(adjFace, tdi.next()));
            }
        }
        adjCubePositions.put("red", redList);
        // blue
        List<CubePosition> blueList = new ArrayList<>();
        tdi = new ThreeDigitIterator(6, 3, 7);
        List<String> blueAdjFaces = faceMap.get("blue").toList();
        for (String adjFace: blueAdjFaces) {
            if (adjFace.equals("yellow")) {
                blueList.add(new CubePosition("yellow", 5));
                blueList.add(new CubePosition("yellow", 1));
                blueList.add(new CubePosition("yellow", 8));
            } else {
                for (int i = 0; i < 3; i += 1) {
                    blueList.add(new CubePosition(adjFace, tdi.next()));
                }
            }
        }
        adjCubePositions.put("blue", blueList);
        // yellow
        List<CubePosition> yellowList = new ArrayList<>();
        yellowList.add(new CubePosition("red", 8));
        yellowList.add(new CubePosition("red", 4));
        yellowList.add(new CubePosition("red", 7));
        yellowList.add(new CubePosition("blue", 6));
        yellowList.add(new CubePosition("blue", 3));
        yellowList.add(new CubePosition("blue", 7));
        yellowList.add(new CubePosition("orange", 5));
        yellowList.add(new CubePosition("orange", 2));
        yellowList.add(new CubePosition("orange", 6));
        yellowList.add(new CubePosition("green", 5));
        yellowList.add(new CubePosition("green", 1));
        yellowList.add(new CubePosition("green", 8));
        adjCubePositions.put("yellow", yellowList);
    }

    /* Initializes the 2D array to all empty chars as placeholders */
    private static void initializeWithPlaceholders(char[][] arr) {
        for (int row = 0; row < arr.length; row += 1) {
            for (int index = 0; index < arr[row].length; index += 1) {
                arr[row][index] = '-';
            }
        }
    }

    /* Associates numbers and moves */
    private static void populateMoveMap() {
        moveMap.put(1, new Move("white", "cw"));
        moveMap.put(2, new Move("white", "ccw"));
        moveMap.put(3, new Move("yellow", "cw"));
        moveMap.put(4, new Move("yellow", "ccw"));
        moveMap.put(5, new Move("green", "cw"));
        moveMap.put(6, new Move("green", "ccw"));
        moveMap.put(7, new Move("blue", "cw"));
        moveMap.put(8, new Move("blue", "ccw"));
        moveMap.put(9, new Move("red", "cw"));
        moveMap.put(10, new Move("red", "ccw"));
        moveMap.put(11, new Move("orange", "cw"));
        moveMap.put(12, new Move("orange", "ccw"));
    }

    /* Initializes the fields */
    private void makeCube() {
        cubeArr =  new char[9][12]; // 9 rows, each with length 12
        initializeWithPlaceholders(cubeArr);
        updateCubeArr();
        ps = new PieceSequence();
        faceMap = new HashMap<>();
        populateFaceMap();
        adjCubePositions = new HashMap<>();
        populateAdjCubePositions();
        moveMap = new HashMap<>();
        populateMoveMap();
        moveStack = new Stack<>();
        moveStack.push(deepCopy(cubeArr)); // pushes the solved state onto the stack
        myRandom = new Random();
    }

    /* Transfers all fields to OTHER to avoid recomputation */
    private void transferNonStaticFieldsFrom(RubiksCube other) {
        this.cubeMap = (HashMap<CubePosition, Character>) other.cubeMap.clone();
        this.cubeArr = deepCopy(other.cubeArr);
        this.moveStack = (Stack<char[][]>) other.moveStack.clone();
    }
}
