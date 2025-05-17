package algorithm;

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
        openSet.add(new Node(start, 0));
        String startBoardKey = boardToString(startBoard);
        gScore.put(startBoardKey, 0);

        System.out.println("Starting UCS search...");
        int expandedNodes = 0;
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            State currState = current.state;
            Board board = currState.board;
            String boardKey = boardToString(board);
            
            expandedNodes++;
            if (expandedNodes % 1000 == 0) {
                System.out.println("Expanded " + expandedNodes + " nodes, current queue size: " + openSet.size());
            }

            if (board.isSolved()) {
                System.out.println("Solution found after expanding " + expandedNodes + " nodes!");
                return reconstructStatePath(currState);
            }

            if (visited.contains(boardKey)) continue;
            visited.add(boardKey);

            for (State neighbor : generateNeighbors(currState)) {
                String neighborKey = boardToString(neighbor.board);
                if (visited.contains(neighborKey)) continue;

                int tentativeG = current.cost + 1;
                if (!gScore.containsKey(neighborKey) || tentativeG < gScore.get(neighborKey)) {
                    gScore.put(neighborKey, tentativeG);
                    openSet.add(new Node(neighbor, tentativeG));
                }
            }
        }

        System.out.println("No solution found after expanding " + expandedNodes + " nodes.");
        return new ArrayList<>();
    }
    
    // Helper method untuk mengubah board menjadi string untuk keperluan set dan map
    private String boardToString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.rows; i++) {
            for (int j = 0; j < board.cols; j++) {
                sb.append(board.grid[i][j]);
            }
        }
        return sb.toString();
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