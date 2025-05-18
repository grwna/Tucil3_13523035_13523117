package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Board;
import model.State;

public class IDDFS extends Pathfinder {

    @Override
    public String getName() {
        return "Iterative Deepening Search (IDS)";
    }

    @Override
    public List<State> solve(Board startBoard) {
        System.out.println("Starting Iterative Deepening Search...");
        int totalNodesExpandedEstimate = 0;

        for (int depthLimit = 0; ; depthLimit++) {
            // System.out.println("Attempting depth: " + depthLimit);
            State startState = new State(startBoard, "Start", null);
            // visitedInCurrentPath is used to avoid cycles within a single DLS call
            Set<String> visitedInCurrentPath = new HashSet<>(); 
            
            // Simple counter for nodes expanded in this DLS iteration
            int[] nodesExpandedThisDLS = {0}; 

            List<State> resultPath = depthLimitedSearch(startState, 0, depthLimit, visitedInCurrentPath, nodesExpandedThisDLS);
            
            totalNodesExpandedEstimate += nodesExpandedThisDLS[0];

            if (resultPath != null) {
                System.out.println("Solution found at depth " + (resultPath.size() - 1) + " after exploring approximately " + totalNodesExpandedEstimate + " states.");
                return resultPath; // Path is already correctly ordered by reconstructStatePath
            }

            // Safety break for very complex puzzles or if no solution is found after reasonable effort
            if (depthLimit > 30 && totalNodesExpandedEstimate > 1000000) { // Adjusted limits
                 System.out.println("Search stopped: Exceeded reasonable depth or node expansion limit. Total states explored: " + totalNodesExpandedEstimate);
                 break;
            }
            if (depthLimit > 50) { // Absolute depth limit
                System.out.println("Search stopped: Exceeded absolute depth limit of 50. Total states explored: " + totalNodesExpandedEstimate);
                break;
            }
        }
        System.out.println("No solution found. Total states explored: " + totalNodesExpandedEstimate);
        return new ArrayList<>();
    }

    /**
     * Depth-Limited Search (DLS) helper for IDDFS.
     * @param currentState The current state to explore from.
     * @param currentDepth The depth of currentState from the start of this DLS call.
     * @param maxDepth The maximum depth allowed for this DLS call.
     * @param visitedInCurrentPath Set of board configurations visited along the current path of this DLS.
     * @param nodesExpandedCounter Array to pass counter by reference for nodes expanded in this DLS.
     * @return List of states from start to goal if found, otherwise null.
     */
    private List<State> depthLimitedSearch(State currentState, int currentDepth, int maxDepth, Set<String> visitedInCurrentPath, int[] nodesExpandedCounter) {
        nodesExpandedCounter[0]++; 
        String currentBoardKey = Pathfinder.boardToString(currentState.board);

        if (currentState.board.isSolved()) {
            return reconstructStatePath(currentState); // Found goal
        }

        if (currentDepth >= maxDepth) {
            return null; // Depth limit reached
        }

        visitedInCurrentPath.add(currentBoardKey); // Mark visited for current path

        for (State neighbor : generateNeighbors(currentState)) {
            String neighborBoardKey = Pathfinder.boardToString(neighbor.board);
            if (!visitedInCurrentPath.contains(neighborBoardKey)) {
                List<State> path = depthLimitedSearch(neighbor, currentDepth + 1, maxDepth, visitedInCurrentPath, nodesExpandedCounter);
                if (path != null) {
                    visitedInCurrentPath.remove(currentBoardKey); // Backtrack
                    return path; // Propagate solution
                }
            }
        }
        visitedInCurrentPath.remove(currentBoardKey); // Backtrack: remove from current path's visited set
        return null; // No solution from this branch within depth limit
    }

}