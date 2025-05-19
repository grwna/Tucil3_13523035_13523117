package algorithm.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import model.Board;
import model.State;

public class UCS extends Pathfinder {
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
        return "Uniform Cost Search";
    }

    @Override
    public List<State> solve(Board startBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        Map<String, Integer> gScore = new HashMap<>();

        State start = new State(startBoard, "Start", null);
        long startTime = System.nanoTime();
        openSet.add(new Node(start, 0));
        String startBoardKey = Pathfinder.boardToString(startBoard);
        gScore.put(startBoardKey, 0);

        System.out.println("Starting UCS search...");
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

                int tentativeG = current.cost + 1;
                if (!gScore.containsKey(neighborKey) || tentativeG < gScore.get(neighborKey)) {
                    gScore.put(neighborKey, tentativeG);
                    openSet.add(new Node(neighbor, tentativeG));
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
        int cost;

        public Node(State state, int cost) {
            this.state = state;
            this.cost = cost;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }
}