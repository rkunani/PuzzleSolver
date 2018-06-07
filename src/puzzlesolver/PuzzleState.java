package puzzlesolver;

public interface PuzzleState {
    /* Returns the estimated distance to the solved state */
    public int distToSolved();

    /* Returns whether the current PuzzleState is the solved state */
    default public boolean isSolved() {
        return distToSolved() == 0;
    }

    /* Returns an Iterable of the adjacent states of the current PuzzleState */
    public Iterable<PuzzleState> adjacentStates();

    /* Generates a readable representation of the PuzzleState */
    public void printState();
}
