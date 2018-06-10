package puzzlesolver;

import java.util.*;

public class CubeUtils {

    /////////////
    /* CLASSES */
    /////////////

    /* Each CubePosition corresponds to a static location on a Rubik's Cube
     * (e.g. the bottom left square on the orange face) */
    public static class CubePosition {
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

    public static class FaceSequence {
        private String[] arr;

        public FaceSequence(String first, String second, String third, String fourth) {
            arr = new String[4];
            arr[0] = first;
            arr[1] = second;
            arr[2] = third;
            arr[3] = fourth;
        }

        /* Returns the face after oldFace when rotating in the given DIRECTION */
        public String nextFace(String oldFace, String direction) {
            int arrIndex = getArrIndex(oldFace);
            int nextIndex;
            if (direction.equals("cw")) {
                nextIndex = plusOne(arrIndex);
            } else {
                nextIndex = minusOne(arrIndex);
            }
            return arr[nextIndex];
        }

        /* Converts the FaceSequence into a List */
        public List<String> toList() {
            List<String> faceList = new ArrayList<>();
            for (int i = 0; i < arr.length; i += 1) {
                faceList.add(arr[i]);
            }
            return faceList;
        }

        private int getArrIndex(String oldFace) {
            for (int i = 0; i < arr.length; i += 1) {
                if (arr[i].equals(oldFace)) {
                    return i;
                }
            }
            return -1;
        }

        private int plusOne(int index) {
            return (index + 1) % arr.length; // arbitrarily chose edgeArr.length (both have same length)
        }

        private int minusOne(int index) {
            if (index - 1 >= 0) {
                return index - 1;
            }
            return (index - 1) + arr.length;
        }
    }

    public static class Move {
        public String face;
        public String direction;

        public Move(String face, String direction) {
            this.face = face;
            this.direction = direction;
        }
    }

    public static class PieceSequence {
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

    public static class ThreeDigitIterator implements Iterator<Integer> {
        private int[] myArr;
        private int index;

        /* An infinite cycle of the integers FIRST, SECOND, and THIRD in that order */
        public ThreeDigitIterator(int first, int second, int third) {
            myArr =  new int[3];
            myArr[0] = first;
            myArr[1] = second;
            myArr[2] = third;
            index = 0;
        }

        public boolean hasNext() {
            return true; // always true because this is an infinite cycle
        }

        public Integer next() {
            int toReturn = myArr[index];
            index = (index + 1) % myArr.length;
            return toReturn;
        }
    }

    /////////////
    /* METHODS */
    /////////////

    /**
     * Makes a deep copy of a 2D array
     * @source https://stackoverflow.com/questions/1564832/how-do-i-do-a-deep-copy-of-a-2d-array-in-java
     */
    public static char[][] deepCopy(char[][] arr) {
        char[][] copy = new char[arr.length][];
        for (int i = 0; i < arr.length; i += 1) {
            copy[i] = Arrays.copyOf(arr[i], arr[i].length);
        }
        return copy;
    }

    /* Appropriately fills faceMap */
    public static void populateFaceMap(HashMap<String, FaceSequence> faceMap) {
        faceMap.put("white", new FaceSequence("orange", "blue", "red", "green"));
        faceMap.put("orange", new FaceSequence("yellow", "blue", "white", "green"));
        faceMap.put("yellow", new FaceSequence("red", "blue", "orange", "green"));
        faceMap.put("red", new FaceSequence("white", "blue", "yellow", "green"));
        faceMap.put("green", new FaceSequence("white", "red", "yellow", "orange"));
        faceMap.put("blue", new FaceSequence("white", "orange", "yellow", "red"));
    }

    /* Appropriately fills adjCubePositions */
    public static void populateAdjCubePositions(HashMap<String, List<CubeUtils.CubePosition>> adjCubePositions, HashMap<String, FaceSequence> faceMap) {
        // white
        List<CubePosition> whiteList = new ArrayList<>();
        whiteList.add(new CubeUtils.CubePosition("orange", 8));
        whiteList.add(new CubeUtils.CubePosition("orange", 4));
        whiteList.add(new CubeUtils.CubePosition("orange", 7));
        whiteList.add(new CubeUtils.CubePosition("blue", 5));
        whiteList.add(new CubeUtils.CubePosition("blue", 1));
        whiteList.add(new CubeUtils.CubePosition("blue", 8));
        whiteList.add(new CubeUtils.CubePosition("red", 6));
        whiteList.add(new CubeUtils.CubePosition("red", 2));
        whiteList.add(new CubeUtils.CubePosition("red", 5));
        whiteList.add(new CubeUtils.CubePosition("green", 7));
        whiteList.add(new CubeUtils.CubePosition("green", 3));
        whiteList.add(new CubeUtils.CubePosition("green", 6));
        adjCubePositions.put("white", whiteList);
        // green
        List<CubeUtils.CubePosition> greenList = new ArrayList<>();
        ThreeDigitIterator tdi = new ThreeDigitIterator(5, 1, 8);
        List<String> greenAdjFaces = faceMap.get("green").toList();
        for (String adjFace: greenAdjFaces) {
            if (adjFace.equals("yellow")) {
                greenList.add(new CubeUtils.CubePosition("yellow", 6));
                greenList.add(new CubeUtils.CubePosition("yellow", 3));
                greenList.add(new CubeUtils.CubePosition("yellow", 7));
            } else {
                for (int i = 0; i < 3; i += 1) {
                    greenList.add(new CubeUtils.CubePosition(adjFace, tdi.next()));
                }
            }
        }
        adjCubePositions.put("green", greenList);
        // orange
        List<CubeUtils.CubePosition> orangeList = new ArrayList<>();
        tdi = new ThreeDigitIterator(5, 2, 6);
        List<String> orangeAdjFaces = faceMap.get("orange").toList();
        for (String adjFace: orangeAdjFaces) {
            for (int i = 0; i < 3; i += 1) {
                orangeList.add(new CubeUtils.CubePosition(adjFace, tdi.next()));
            }
        }
        adjCubePositions.put("orange", orangeList);
        // red
        List<CubeUtils.CubePosition> redList = new ArrayList<>();
        tdi = new ThreeDigitIterator(8, 4, 7);
        List<String> redAdjFaces = faceMap.get("red").toList();
        for (String adjFace: redAdjFaces) {
            for (int i = 0; i < 3; i += 1) {
                redList.add(new CubeUtils.CubePosition(adjFace, tdi.next()));
            }
        }
        adjCubePositions.put("red", redList);
        // blue
        List<CubeUtils.CubePosition> blueList = new ArrayList<>();
        tdi = new ThreeDigitIterator(6, 3, 7);
        List<String> blueAdjFaces = faceMap.get("blue").toList();
        for (String adjFace: blueAdjFaces) {
            if (adjFace.equals("yellow")) {
                blueList.add(new CubeUtils.CubePosition("yellow", 5));
                blueList.add(new CubeUtils.CubePosition("yellow", 1));
                blueList.add(new CubeUtils.CubePosition("yellow", 8));
            } else {
                for (int i = 0; i < 3; i += 1) {
                    blueList.add(new CubeUtils.CubePosition(adjFace, tdi.next()));
                }
            }
        }
        adjCubePositions.put("blue", blueList);
        // yellow
        List<CubeUtils.CubePosition> yellowList = new ArrayList<>();
        yellowList.add(new CubeUtils.CubePosition("red", 8));
        yellowList.add(new CubeUtils.CubePosition("red", 4));
        yellowList.add(new CubeUtils.CubePosition("red", 7));
        yellowList.add(new CubeUtils.CubePosition("blue", 6));
        yellowList.add(new CubeUtils.CubePosition("blue", 3));
        yellowList.add(new CubeUtils.CubePosition("blue", 7));
        yellowList.add(new CubeUtils.CubePosition("orange", 5));
        yellowList.add(new CubeUtils.CubePosition("orange", 2));
        yellowList.add(new CubeUtils.CubePosition("orange", 6));
        yellowList.add(new CubeUtils.CubePosition("green", 5));
        yellowList.add(new CubeUtils.CubePosition("green", 1));
        yellowList.add(new CubeUtils.CubePosition("green", 8));
        adjCubePositions.put("yellow", yellowList);
    }

    /* Initializes the 2D array to all empty chars as placeholders */
    public static void initializeWithPlaceholders(char[][] arr) {
        for (int row = 0; row < arr.length; row += 1) {
            for (int index = 0; index < arr[row].length; index += 1) {
                arr[row][index] = '-';
            }
        }
    }

    /* Associates numbers and moves */
    public static void populateMoveMap(HashMap<Integer, Move> moveMap) {
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

    /* Initializes cubeMap to a solved state */
    public static void initializeCubeMap(HashMap<CubeUtils.CubePosition, Character> cubeMap) {
        CubeUtils.CubePosition toPut;
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubeUtils.CubePosition("white", i);
            cubeMap.put(toPut, 'w');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubeUtils.CubePosition("orange", i);
            cubeMap.put(toPut, 'o');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubeUtils.CubePosition("green", i);
            cubeMap.put(toPut, 'g');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubeUtils.CubePosition("blue", i);
            cubeMap.put(toPut, 'b');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubeUtils.CubePosition("yellow", i);
            cubeMap.put(toPut, 'y');
        }
        for (int i = 1; i <= 9; i += 1) {
            toPut = new CubeUtils.CubePosition("red", i);
            cubeMap.put(toPut, 'r');
        }
    }

    /* Fills the rows of the 2D array representing the cube */
    public static void fillRows(HashMap<CubeUtils.CubePosition, Character> cubeMap, char[][] cubeArr) {
        for (int row = 1; row <= 9; row += 1) {
            if (row <= 3) {
                fillTopOrBottom(cubeMap, cubeArr, row, "orange");
            } else if (row >= 7) {
                fillTopOrBottom(cubeMap, cubeArr, row, "red");
            } else {
                fillMiddle(cubeMap, cubeArr, row);
            }
        }
    }

    /* Fills the top and bottom faces of the cube */
    private static void fillTopOrBottom(HashMap<CubeUtils.CubePosition, Character> cubeMap, char[][] cubeArr, int row, String face) {
        if (row == 1 || row == 7) {
            cubeArr[row - 1][3] = cubeMap.get(new CubeUtils.CubePosition(face, 5));
            cubeArr[row - 1][4] = cubeMap.get(new CubeUtils.CubePosition(face, 2));
            cubeArr[row - 1][5] = cubeMap.get(new CubeUtils.CubePosition(face, 6));
        }
        if (row == 2 || row == 8) {
            cubeArr[row - 1][3] = cubeMap.get(new CubeUtils.CubePosition(face, 1));
            cubeArr[row - 1][4] = cubeMap.get(new CubeUtils.CubePosition(face, 9));
            cubeArr[row - 1][5] = cubeMap.get(new CubeUtils.CubePosition(face, 3));
        }
        if (row == 3 || row == 9) {
            cubeArr[row - 1][3] = cubeMap.get(new CubeUtils.CubePosition(face, 8));
            cubeArr[row - 1][4] = cubeMap.get(new CubeUtils.CubePosition(face, 4));
            cubeArr[row - 1][5] = cubeMap.get(new CubeUtils.CubePosition(face, 7));
        }
    }

    /* Fills the middle faces of the cube */
    private static void fillMiddle(HashMap<CubeUtils.CubePosition, Character> cubeMap, char[][] cubeArr, int row) {
        ThreeDigitIterator tdi;
        if (row == 4) {
            tdi = new ThreeDigitIterator(5, 2, 6);
            fillRow(cubeMap, cubeArr, tdi, row);
        }
        if (row == 5) {
            tdi = new ThreeDigitIterator(1, 9, 3);
            fillRow(cubeMap, cubeArr, tdi, row);
        }
        if (row == 6) {
            tdi = new ThreeDigitIterator(8, 4, 7);
            fillRow(cubeMap, cubeArr, tdi, row);
        }
    }

    /* Helps with segmentation of middle faces */
    private static void fillRow(HashMap<CubeUtils.CubePosition, Character> cubeMap, char[][] cubeArr, ThreeDigitIterator tdi, int row) {
        for (int i = 0; i < 12; i += 1) {
            if (i <= 2) {
                cubeArr[row - 1][i] = cubeMap.get(new CubeUtils.CubePosition("green", tdi.next()));
            } else if (i <= 5) {
                cubeArr[row - 1][i] = cubeMap.get(new CubeUtils.CubePosition("white", tdi.next()));
            } else if (i <= 8) {
                cubeArr[row - 1][i] = cubeMap.get(new CubeUtils.CubePosition("blue", tdi.next()));
            } else {
                cubeArr[row - 1][i] = cubeMap.get(new CubeUtils.CubePosition("yellow", tdi.next()));
            }
        }
    }

    /* Updates cubeMap to match cubeArr */
    public static void updateCubeMap(HashMap<CubeUtils.CubePosition, Character> cubeMap, char[][] cubeArr) {
        for (int row = 0; row < 9; row += 1) {
            for (int col = 0; col < 12; col += 1) {
                if (cubeArr[row][col] == '-') {
                    continue;
                } else if (row <= 2) {
                    putInCubeMap(cubeMap, cubeArr, "orange", row, col);
                } else if (row >= 6) {
                    putInCubeMap(cubeMap, cubeArr, "red", row, col);
                } else if (col <= 2) {
                    putInCubeMap(cubeMap, cubeArr, "green", row, col);
                } else if (col <= 5) {
                    putInCubeMap(cubeMap, cubeArr, "white", row, col);
                } else if (col <= 8) {
                    putInCubeMap(cubeMap, cubeArr, "blue", row, col);
                } else if (col <= 11) {
                    putInCubeMap(cubeMap, cubeArr, "yellow", row, col);
                }
            }
        }
    }

    private static void putInCubeMap(HashMap<CubeUtils.CubePosition, Character> cubeMap, char[][] cubeArr, String face, int row, int col) {
        if (row == 0 || row == 6 || row == 3) {
            if (col == 3 || col == 0 || col == 6 || col == 9) {
                cubeMap.put(new CubeUtils.CubePosition(face, 5), cubeArr[row][col]);
            } else if (col == 4 || col == 1 || col == 7 || col == 10) {
                cubeMap.put(new CubeUtils.CubePosition(face, 2), cubeArr[row][col]);
            } else if (col == 5 || col == 2 || col == 8 || col == 11) {
                cubeMap.put(new CubeUtils.CubePosition(face, 6), cubeArr[row][col]);
            }
        } else if (row == 1 || row == 7 || row == 4) {
            if (col == 3 || col == 0 || col == 6 || col == 9) {
                cubeMap.put(new CubeUtils.CubePosition(face, 1), cubeArr[row][col]);
            } else if (col == 4 || col == 1 || col == 7 || col == 10) {
                cubeMap.put(new CubeUtils.CubePosition(face, 9), cubeArr[row][col]);
            } else if (col == 5 || col == 2 || col == 8 || col == 11) {
                cubeMap.put(new CubeUtils.CubePosition(face, 3), cubeArr[row][col]);
            }
        } else if (row == 2 || row == 8 || row == 5) {
            if (col == 3 || col == 0 || col == 6 || col == 9) {
                cubeMap.put(new CubeUtils.CubePosition(face, 8), cubeArr[row][col]);
            } else if (col == 4 || col == 1 || col == 7 || col == 10) {
                cubeMap.put(new CubeUtils.CubePosition(face, 4), cubeArr[row][col]);
            } else if (col == 5 || col == 2 || col == 8 || col == 11) {
                cubeMap.put(new CubeUtils.CubePosition(face, 7), cubeArr[row][col]);
            }
        }
    }

    /* Prints a 2D char array */
    public static void print2DArray(char[][] arr) {
        for (char[] row: arr) {
            for (char c: row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }
}
