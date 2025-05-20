package algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import algorithm.heuristic.Heuristic;
import model.Board;
import model.State;

public class HillClimbing extends Pathfinder {
    public HillClimbing(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Hill Climbing Search using " + heuristic.toString();
    }

    @Override
    public List<State> solve(Board startBoard) {
        System.out.println("Starting Hill Climbing search using " + heuristic.toString() + "...");

        this.nodes = 0;
        long startTime = System.nanoTime();
        
        State currentState = new State(startBoard, "Start", null);
        int currentHeuristicValue = heuristic.estimate(currentState.board);
        int stepsTaken = 0;
        int consecutiveSidewaysMoves = 0;
        this.nodes++;

        Set<String> visitedStatesThisAttempt = new HashSet<>(); 

        while (true) {
            if (currentState.board.isSolved()) {
                this.runtimeNano = System.nanoTime() - startTime;
                System.out.println("Solution found after " + stepsTaken + " steps (" + consecutiveSidewaysMoves + " final sideways).");
                return reconstructStatePath(currentState);
            }

            String currentBoardKey = boardToString(currentState.board);
            visitedStatesThisAttempt.add(currentBoardKey);

            List<State> neighbors = generateNeighbors(currentState);
            Collections.shuffle(neighbors);

            State bestNeighbourValue = null;
            int bestHeuristicValue = currentHeuristicValue;

            State firstSidewaysNeighbor = null;

            if (neighbors.isEmpty()) {
                break; 
            }

            for (State neighbor : neighbors) {
                this.nodes++;
                String neighborBoardKey = boardToString(neighbor.board);
                if (visitedStatesThisAttempt.contains(neighborBoardKey)) {continue;}

                int neighborHeuristic = heuristic.estimate(neighbor.board);

                if (neighborHeuristic < bestHeuristicValue) {
                    bestHeuristicValue = neighborHeuristic;
                    bestNeighbourValue = neighbor;
                } else if (neighborHeuristic == currentHeuristicValue && firstSidewaysNeighbor == null) {
                    firstSidewaysNeighbor = neighbor;
                }
            }

            if (bestNeighbourValue != null) {
                currentState = bestNeighbourValue;
                currentHeuristicValue = bestHeuristicValue;
                consecutiveSidewaysMoves = 0;
            } else if (firstSidewaysNeighbor != null) {
                currentState = firstSidewaysNeighbor;
                consecutiveSidewaysMoves++;
            } else {
                break; 
            }
            stepsTaken++;
        }

        this.runtimeNano = System.nanoTime() - startTime;

        if (currentState.board.isSolved()) {
             System.out.println("Solution found at MAX_STEPS (" + stepsTaken + ").");
             return reconstructStatePath(currentState);
        }

        System.out.println("No solution found or stuck at local optimum/plateau after " + stepsTaken + " steps.");
        return new ArrayList<>();
    }
}