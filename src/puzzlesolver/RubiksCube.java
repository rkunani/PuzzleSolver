package puzzlesolver;

import java.util.*;

/* A 3x3 Rubik's Cube */
public class RubiksCube implements PuzzleState {

    private HashMap<CubePosition, Character> cubeMap; // core representation of the cube
    private char[][] cubeArr;
    private static PieceSequence ps;
    private static HashMap<String, FaceSequence> faceMap;
    private static HashMap<String, List<CubePosition>> adjCubePositions;
    private static HashMap<Integer, Move> moveMap;
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
        HashMap<CubePosition, Character> storageMap = fillStorageMap(face); // avoids cloning
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
        moveStack.push(CubeUtils.deepCopy(cubeArr)); // invariant: top element of the stack is always the current state
    }

    /* Creates a map of the CubePositions on and adjacent to FACE */
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
        while(!isSolved()) {
            undoMove();
        }
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
        CubeUtils.fillRows(cubeMap, cubeArr);
    }

    /* Updates cubeMap to match cubeArr */
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
        ps = new PieceSequence();
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
        this.cubeMap = (HashMap<CubePosition, Character>) other.cubeMap.clone(); // cloning is ok here because CubePosition is immutable
        this.cubeArr = CubeUtils.deepCopy(other.cubeArr);
        this.moveStack = (Stack<char[][]>) other.moveStack.clone(); // cloning is ok here because the arrays in the Stack are copies
    }
}
