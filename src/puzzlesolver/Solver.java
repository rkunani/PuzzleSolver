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
        System.out.println("Initial state: ");
        initialState.printState();
        System.out.println("\nSolving...");

        PSNode startNode = new PSNode(initialState, 0, null);
        fringe.add(startNode);

        PSNode bestNode = fringe.poll();
        while (!(bestNode.ps.isSolved())) {
            for (PuzzleState adjState: bestNode.ps.adjacentStates()) {
                PSNode toAdd = new PSNode(adjState, bestNode.distFromStart + 1, bestNode);
                if (bestNode.prev == null || !toAdd.ps.equals(bestNode.prev.ps)) {
                    fringe.add(toAdd);
                }
            }
            bestNode = fringe.poll();
        }
        numMoves = bestNode.distFromStart;
        while (bestNode != null) {
            solution.addFirst(bestNode.ps);
            bestNode = bestNode.prev;
        }
        System.out.println("Found a solution with " + numMoves + " moves!\n");
        printSolution();
    }

    /* Prints the steps to the solved state */
    public void printSolution() {
        for (PuzzleState ps: solution) {
            ps.printState();
        }
    }
}
