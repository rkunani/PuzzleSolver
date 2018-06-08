package puzzlesolver;

public class Scratch {
    public static void main(String[] args) {
        RubiksCube cube = new RubiksCube();
        cube.printCube();
        cube.rotate("white", "cw");
        cube.printCube();
        cube.rotate("blue", "ccw");
        cube.printCube();
        cube.undoMove();
        cube.printCube();
    }
}
