package puzzlesolver;

public interface PuzzleState {
    /* Returns the estimated distance to the solved state */
    int distToSolved();

    /* Returns whether the current PuzzleState is the solved state */
    default boolean isSolved() {
        return distToSolved() == 0;
    }

    /* Returns an Iterable of the adjacent states of the current PuzzleState */
    Iterable<PuzzleState> adjacentStates();

    /* Generates a readable representation of the PuzzleState */
    void printState();

    /* Tests for equality of two PuzzleStates */
    boolean equals(PuzzleState other);
}
