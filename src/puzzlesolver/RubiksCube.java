package puzzlesolver;

import java.util.*;

/* A 3x3 Rubik's Cube */
public class RubiksCube implements PuzzleState {

    private HashMap<CubeUtils.CubePosition, Character> cubeMap; // core representation of the cube
    private char[][] cubeArr;
    private static CubeUtils.PieceSequence ps;
    private static HashMap<String, CubeUtils.FaceSequence> faceMap;
    private static HashMap<String, List<CubeUtils.CubePosition>> adjCubePositions;
    private static HashMap<Integer, CubeUtils.Move> moveMap;
    private Stack<char[][]> moveStack;
    private static Random myRandom;

    /* Creates a solved Rubik's Cube */
    public RubiksCube() {
        cubeMap = new HashMap<>();
        makeCube();
    }

    /* Creates a Rubik's Cube with the same state as RC */
    private RubiksCube(RubiksCube rc) {
        transferNonStaticFieldsFrom(rc);
    }

    ///////////////////////
    /* ROTATING THE CUBE */
    ///////////////////////

    /* Rotates the given FACE in the given DIRECTION */
    public void rotate(String face, String direction) {
        HashMap<CubeUtils.CubePosition, Character> storageMap = fillStorageMap(face);
        for (int i = 1; i <= 8; i += 1) { // update face values
            int nextPos;
            char value;
            if (i <= 4) { // edge
                nextPos = ps.nextEdge(face, face, i, direction);
                value = storageMap.get(new CubeUtils.CubePosition(face, i));
            } else { // corner
                value = storageMap.get(new CubeUtils.CubePosition(face, i));
                nextPos = ps.nextCorner(face, face, i, direction);
            }
            cubeMap.put(new CubeUtils.CubePosition(face, nextPos), value);
        }
        List<CubeUtils.CubePosition> adjPositions = adjCubePositions.get(face);
        for (CubeUtils.CubePosition cp: adjPositions) { // update adjacent face values
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
            cubeMap.put(new CubeUtils.CubePosition(nextFace, nextPos), value);
        }
        updateCubeArr();
        moveStack.push(CubeUtils.deepCopy(cubeArr)); // invariant: top element of the stack is always the current state
    }

    private HashMap<CubeUtils.CubePosition, Character> fillStorageMap(String face) {
        HashMap<CubeUtils.CubePosition, Character> storageMap = new HashMap<>();
        for (int i = 1; i <= 8; i += 1) { // copy face values
            CubeUtils.CubePosition cp = new CubeUtils.CubePosition(face, i);
            char value = cubeMap.get(cp);
            storageMap.put(cp, value);
        }
        List<CubeUtils.CubePosition> adjPositions = adjCubePositions.get(face);
        for (CubeUtils.CubePosition adjPosition: adjPositions) { // copy adjacent position values
            char value = cubeMap.get(adjPosition);
            storageMap.put(adjPosition, value);
        }
        return storageMap;
    }

    

    /////////////////////////////////////////////
    /* IMPLEMENTING THE PUZZLE STATE INTERFACE */
    /////////////////////////////////////////////

    public Iterable<PuzzleState> adjacentStates() {
        List<PuzzleState> adjacent = new ArrayList<>();
        for (int i = 1; i <= 12; i += 1) {
            CubeUtils.Move move = moveMap.get(i);
            String face = move.face;
            String direction = move.direction;
            rotate(face, direction);
            adjacent.add(new RubiksCube(this));
            undoMove();
        }
        return adjacent;
    }

    public int distToSolved() {
        Set<CubeUtils.CubePosition> cubePositions = cubeMap.keySet();
        int sum = 0;
        for (CubeUtils.CubePosition cp: cubePositions) {
            char value = cubeMap.get(cp);
            String face = cp.face;
            sum += numMovesAway(value, face);
        }
        return sum;
    }

    public void printState() {
        printCube();
    }

    public boolean equals(PuzzleState other) {
        RubiksCube otherCube = (RubiksCube) other;
        return this.cubeMap.equals(otherCube.cubeMap);
    }

    /* Returns the minimum number of rotations to put VALUE on the correct face */
    private static int numMovesAway(char value, String currFace) {
        if (opposite(currFace) == value) {
            return 2;
        }
        return 1;
    }

    /* Returns the character corresponding to the face opposite from FACE */
    private static char opposite(String face) {
        if (face.equals("white")) {
            return 'y';
        } else if (face.equals("yellow")) {
            return 'w';
        } else if (face.equals("green")) {
            return 'b';
        } else if (face.equals("blue")) {
            return 'g';
        } else if (face.equals("red")) {
            return 'y';
        } else { // face is "yellow"
            return 'r';
        }
    }

    //////////////////////
    /* OTHER OPERATIONS */
    //////////////////////

    /* Undoes the previous CubeUtils.Move */
    public void undoMove() {
        if (isSolved()) {
            return;
        }
        moveStack.pop(); // reCubeUtils.Moves the current state from the stack
        cubeArr = moveStack.peek(); // restores the state before the current state
        updateCubeMap();
    }

    /* Scrambles the Rubik's Cube with a minimum
     * of 15 CubeUtils.Moves and a maximum of 25 CubeUtils.Moves */
    public void scramble() {
        int numMoves = myRandom.nextInt(25) + 15;
        for (int i = 0; i < numMoves; i += 1) {
            int moveNum = myRandom.nextInt(12) + 1;
            CubeUtils.Move move = moveMap.get(moveNum);
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
        CubeUtils.fillRows(cubeMap, cubeArr);
    }

    private void updateCubeMap() {
        CubeUtils.updateCubeMap(cubeMap, cubeArr);
    }

    ///////////////////////
    /* PRINTING THE CUBE */
    ///////////////////////

    /* Prints the current state of the cube */
    public void printCube() {
        CubeUtils.print2DArray(cubeArr);
        System.out.println();
    }
    
    ////////////////////////////////
    /* CONSTRUCTOR HELPER METHODS */
    ////////////////////////////////

    /* Initializes the fields */
    private void makeCube() {
        CubeUtils.initializeCubeMap(cubeMap);
        cubeArr =  new char[9][12]; // 9 rows, each with length 12
        CubeUtils.initializeWithPlaceholders(cubeArr);
        updateCubeArr();
        ps = new CubeUtils.PieceSequence();
        faceMap = new HashMap<>();
        CubeUtils.populateFaceMap(faceMap);
        adjCubePositions = new HashMap<>();
        CubeUtils.populateAdjCubePositions(adjCubePositions, faceMap);
        moveMap = new HashMap<>();
        CubeUtils.populateMoveMap(moveMap);
        moveStack = new Stack<>();
        moveStack.push(CubeUtils.deepCopy(cubeArr)); // pushes the solved state onto the stack
        myRandom = new Random();
    }

    /* Transfers all fields from OTHER to avoid recomputation */
    private void transferNonStaticFieldsFrom(RubiksCube other) {
        this.cubeMap = (HashMap<CubeUtils.CubePosition, Character>) other.cubeMap.clone();
        this.cubeArr = CubeUtils.deepCopy(other.cubeArr);
        this.moveStack = (Stack<char[][]>) other.moveStack.clone();
    }
}
