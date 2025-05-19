package algorithm.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import algorithm.heuristic.Heuristic;
import model.Board;
import model.State;

public class AStar extends Pathfinder {
    private Heuristic heuristic;
    private long runtimeNano = -1;
    private int nodes;

    public long getRuntimeNano() {
        return this.runtimeNano;
    }

    public int getNodes(){
        return this.nodes;
    }

    public AStar(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "A* Search with " + heuristic.toString();
    }

    @Override
    public List<State> solve(Board startBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<String, Integer> gScore = new HashMap<>();
        Set<String> visited = new HashSet<>();

        State start = new State(startBoard, "Start", null);
        long startTime = System.nanoTime();
        int startH = heuristic.estimate(start.board);
        openSet.add(new Node(start, startH, 0));
        String startBoardKey = Pathfinder.boardToString(startBoard);
        gScore.put(startBoardKey, 0);

        System.out.println("Starting A* search with " + heuristic.toString() + "...");
        System.out.println("Initial heuristic value: " + startH);
        int expandedNodes = 0;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            State currState = current.state;
            Board board = currState.board;
            String boardKey = Pathfinder.boardToString(board);
            
            expandedNodes++;
            if (expandedNodes % 1000 == 0) {
                System.out.println("Expanded " + expandedNodes + " nodes, current queue size: " + openSet.size());
            }

            if (board.isSolved()) {
                this.runtimeNano = System.nanoTime() - startTime;
                this.nodes = expandedNodes;
                System.out.println("Solution found after expanding " + expandedNodes + " nodes!");
                return reconstructStatePath(currState);
            }

            if (visited.contains(boardKey)) continue;
            visited.add(boardKey);

            for (State neighbor : generateNeighbors(currState)) {
                String neighborKey = Pathfinder.boardToString(neighbor.board);
                if (visited.contains(neighborKey)) continue;

                int tentativeG = current.gScore + 1;
                if (!gScore.containsKey(neighborKey) || tentativeG < gScore.get(neighborKey)) {
                    gScore.put(neighborKey, tentativeG);
                    int h = heuristic.estimate(neighbor.board);
                    int f = tentativeG + h;
                    openSet.add(new Node(neighbor, f, tentativeG));
                }
            }
        }
        this.runtimeNano = System.nanoTime() - startTime;
        this.nodes = expandedNodes;
        System.out.println("No solution found after expanding " + expandedNodes + " nodes.");
        return new ArrayList<>();
    }
    
    private static class Node implements Comparable<Node> {
        State state;
        int fScore;
        int gScore;

        public Node(State state, int fScore, int gScore) {
            this.state = state;
            this.fScore = fScore;
            this.gScore = gScore;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.fScore, other.fScore);
        }
    }
}