package puzzlesolver;

public class Scratch {
    public static void main(String[] args) {
        RubiksCube cube = new RubiksCube();
        cube.rotate("white", "ccw");
        cube.rotate("blue", "cw");
        cube.rotate("green", "cw");
        cube.rotate("yellow", "ccw");
        cube.rotate("orange", "ccw");
        Solver s = new Solver(cube);
    }
}
