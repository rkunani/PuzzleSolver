package puzzlesolver;

public interface PuzzleState {
    /* Returns the estimated distance to the goal state */
    public int distToGoal();

    /* Returns whether the current PuzzleState is the goal state */
    default public boolean isGoal() {
        return distToGoal() == 0;
    }

    /* Returns an Iterable of the adjacent states of the current PuzzleState */
    public Iterable<PuzzleState> adjacentStates();

    /* Generates a readable representation of the PuzzleState */
    //public String toString();
}
