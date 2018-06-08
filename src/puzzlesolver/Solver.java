package puzzlesolver;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class Solver {
    /* Necessary to keep track of distance from start for A*
    *  and to prevent infinite adding of elements to priority queue */
    private class PSNode implements Comparable<PSNode> {
        PuzzleState ps;
        int distFromStart;
        int distToSolved;
        PSNode prev;

        PSNode(PuzzleState pstate, int distFromStart, PSNode prev) {
            ps = pstate;
            this.distFromStart = distFromStart;
            this.distToSolved = ps.distToSolved();
            this.prev = prev;
        }

        /* Defines the priority of a PSNode; implements Comparable interface */
        public int compareTo(PSNode other) {
            int myPriority = this.distFromStart + this.distToSolved;
            int otherPriority = other.distFromStart + other.distToSolved;
            return myPriority - otherPriority;
        }
    }
    private int numMoves;
    private LinkedList<PuzzleState> solution;

    /* Uses the A* search algorithm to solve the given PuzzleState */
    public Solver(PuzzleState initialState) {
        PriorityQueue<PSNode> fringe = new PriorityQueue<>();
        solution = new LinkedList<>();

        PSNode startNode = new PSNode(initialState, 0, null);
        fringe.add(startNode);

        PSNode bestNode = fringe.poll();
        while (!(bestNode.ps.isSolved())) {
            for (PuzzleState adjState: bestNode.ps.adjacentStates()) {
                if (bestNode.prev == null) {
                    fringe.add(new PSNode(adjState, bestNode.distFromStart + 1, bestNode));
                } else {
                    if (bestNode.prev.ps.equals(adjState)) {
                        continue; // do not reconsider the previous node's PuzzleState
                    }
                    fringe.add(new PSNode(adjState, bestNode.distFromStart + 1, bestNode));
                }
            }
            bestNode = fringe.poll();
        }
        numMoves = bestNode.distFromStart;
        while (bestNode != null) {
            solution.addFirst(bestNode.ps);
            bestNode = bestNode.prev;
        }
    }

    /* Returns the number of moves needed to solve the PuzzleState */
    public int getNumMoves() {
        return numMoves;
    }

    /* Prints the steps to the solved state */
    public void printSolution() {
        // uses the solution linked list
    }
}
