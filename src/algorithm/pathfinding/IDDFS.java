package algorithm.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Board;
import model.State;

public class IDDFS extends Pathfinder {
    private long runtimeNano = -1;
    private int nodes;

    public long getRuntimeNano() {
        return this.runtimeNano;
    }

    public int getNodes(){
        return this.nodes;
    }

    @Override
    public String getName() {
        return "Iterative Deepening Search (IDS)";
    }

    @Override
    public List<State> solve(Board startBoard) {
        System.out.println("Starting Iterative Deepening Search...");
        long startTime = System.nanoTime();
        int totalNodesExpandedEstimate = 0;

        for (int depthLimit = 0; ; depthLimit++) {
            State startState = new State(startBoard, "Start", null);
            Set<String> visitedInCurrentPath = new HashSet<>(); 
            
            int[] nodesExpandedThisDLS = {0}; 
            List<State> resultPath = depthLimitedSearch(startState, 0, depthLimit, visitedInCurrentPath, nodesExpandedThisDLS);
            totalNodesExpandedEstimate += nodesExpandedThisDLS[0];

            if (resultPath != null) {
                this.runtimeNano = System.nanoTime() - startTime;
                this.nodes = totalNodesExpandedEstimate;
                System.out.println("Solution found at depth " + (resultPath.size() - 1) + " after exploring approximately " + totalNodesExpandedEstimate + " states.");
                return resultPath;
            }

            if (depthLimit > 30 && totalNodesExpandedEstimate > 1000000) {
                 System.out.println("Search stopped: Exceeded reasonable depth or node expansion limit. Total states explored: " + totalNodesExpandedEstimate);
                 break;
            }
            if (depthLimit > 50) {
                System.out.println("Search stopped: Exceeded absolute depth limit of 50. Total states explored: " + totalNodesExpandedEstimate);
                break;
            }
        }
        this.runtimeNano = System.nanoTime() - startTime;
        this.nodes = totalNodesExpandedEstimate;
        System.out.println("No solution found. Total states explored: " + totalNodesExpandedEstimate);
        return new ArrayList<>();
    }

    private List<State> depthLimitedSearch(State currentState, int currentDepth, int maxDepth, Set<String> visitedInCurrentPath, int[] nodesExpandedCounter) {
        nodesExpandedCounter[0]++; 
        String currentBoardKey = Pathfinder.boardToString(currentState.board);

        if (currentState.board.isSolved()) {
            return reconstructStatePath(currentState); // Found goal
        }

        if (currentDepth >= maxDepth) {
            return null;
        }

        visitedInCurrentPath.add(currentBoardKey);

        for (State neighbor : generateNeighbors(currentState)) {
            String neighborBoardKey = Pathfinder.boardToString(neighbor.board);
            if (!visitedInCurrentPath.contains(neighborBoardKey)) {
                List<State> path = depthLimitedSearch(neighbor, currentDepth + 1, maxDepth, visitedInCurrentPath, nodesExpandedCounter);
                if (path != null) {
                    visitedInCurrentPath.remove(currentBoardKey);
                    return path;
                }
            }
        }
        visitedInCurrentPath.remove(currentBoardKey);
        return null;
    }
}