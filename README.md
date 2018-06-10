# PuzzleSolver
A Java program that represents a Rubik's Cube and solves puzzles using the A* search algorithm. (WORK IN PROGRESS)

**Files:**

- ```PuzzleState.java```: An interface to enable the use of A*

- ```Solver.java```: Implements the A* algorithm to solve a given PuzzleState

- ```RubiksCube.java```: Represents a 3x3 Rubik's Cube

- ```CubeUtils.java```: Contains classes and methods to facilitate ```RubiksCube``` operations

**More About ```RubiksCube.java```**

```RubiksCube.java``` assumes a fixed orientation of the cube with the white face in front, the orange face on top, and the green face on the left.
Thus, the solved state of the cube (when unfolded) is represented as follows:

\- - - o o o - - - - - -

\- - - o o o - - - - - -

\- - - o o o - - - - - -

g g g w w w b b b y y y

g g g w w w b b b y y y

g g g w w w b b b y y y

\- - - r r r - - - - - -

\- - - r r r - - - - - -

\- - - r r r - - - - - - 

Each ```RubiksCube``` has the following public API:

- ```rotate(String face, String direction)```: Rotates the given face in the given direction. ```face``` is the color of the face you want to rotate, and ```direction``` is the direction you want to rotate ```face```. Use ```"cw"``` for clockwise and ```"ccw"``` for counterclockwise.

- ```scramble()```: Scrambles the cube.

- ```undoMove()```: Undoes the previous move.

- ```resetCube()```: Resets the cube to the solved state.

- ```printCube()```: Prints the current state of the cube.

Example operations are shown in ```Demo.java```.

**Improvements to be made**

- ```Solver.java``` can quickly solve "almost solved" cubes (~4 rotations from solved), but it finds an unnecessarily long solution (~15 moves). For cubes that are not "almost solved," an ```OutofMemoryError``` is thrown due to repeated/compounded exploration of states far from the solution. These two occurrences tell me that my implementation of ```distToSolved()``` is not a good heuristic for A*. Thus, rethinking my implementation would allow ```Solver.java``` to solve not only more cubes, but solve them more efficiently.

- There is probably a cleaner way to represent a Rubik's Cube than the way I did. Due to my choice of data structure, at times I felt my methods were less elegant than they could be. An improvement I would like to make is to find a better use of data structures to represent the Rubik's Cube.
