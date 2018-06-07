package puzzlesolver;

public class Scratch {
    public static void main(String[] args) {
        /* Testing nextNum
        int lastNum = Integer.parseInt(args[1]);
        int firstNum = Integer.parseInt(args[0]);
        for (int i = 1; i <= 10; i +=1) {
            System.out.println(nextNum(i, firstNum, lastNum));
        }
        */

        /* Printing a 2-D array
        int[][] arr = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        for (int[] row: arr) {
            for (int a: row) {
                System.out.print(a + " ");
            }
            System.out.println();
        } */

        /* Testing printing */
        RubiksCube cube = new RubiksCube(false);
        cube.printCube();
        cube.rotate("blue", "cw");
        cube.rotate("white", "ccw");
        cube.rotate("red", "cw");
        cube.printCube();
    }

    /**
     * Returns the number after X in the cycle of consecutive
     * integers from firstNum to lastNum, inclusive.
     */
    private static int nextNum(int x, int firstNum, int lastNum) {
        int seqLength = (lastNum - firstNum) + 1;
        return ((x - 1) % seqLength) + firstNum;
    }
}
