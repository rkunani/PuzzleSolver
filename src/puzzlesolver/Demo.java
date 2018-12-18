package puzzlesolver;

public class Demo {
    public static void main(String[] args) {
        /* Using the RubiksCube public API */
        RubiksCube cube = new RubiksCube();
        cube.rotate("white", "ccw"); // rotates the white face counter-clockwise
        cube.rotate("blue", "cw"); // rotates the blue face clockwise
        cube.rotate("green", "cw");
        cube.rotate("yellow", "ccw");
        cube.printCube(); // prints the current state of the cube
        cube.undoMove(); // undoes the last move
        cube.printCube();
        cube.scramble(); // scrambles the cube
        cube.printCube();
        cube.resetCube(); // resets the cube to the solved state
        cube.printCube();


        /* Solving the RubiksCube */
        Solver s = new Solver(cube); // solves the cube
    }
}
