package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import model.Board;
import model.State;

public class GreedyBestFirst extends Pathfinder {
    private Heuristic heuristic;

    public GreedyBestFirst(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Greedy Best First Search using " + heuristic.toString();
    }

    @Override
    public List<State> solve(Board startBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        State start = new State(startBoard, "Start", null);
        int startH = heuristic.estimate(startBoard);
        openSet.add(new Node(start, startH));
        
        System.out.println("Starting Greedy Best First search with " + heuristic.toString() + "...");
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
                System.out.println("Solution found after expanding " + expandedNodes + " nodes!");
                return reconstructStatePath(currState);
            }

            if (visited.contains(boardKey)) continue;
            visited.add(boardKey);

            for (State neighbor : generateNeighbors(currState)) {
                String neighborKey = Pathfinder.boardToString(neighbor.board);
                if (!visited.contains(neighborKey)) {
                    int h = heuristic.estimate(neighbor.board);
                    openSet.add(new Node(neighbor, h));
                }
            }
        }

        System.out.println("No solution found after expanding " + expandedNodes + " nodes.");
        return new ArrayList<>();
    }
    
    private static class Node implements Comparable<Node> {
        State state;
        int priority;

        public Node(State state, int priority) {
            this.state = state;
            this.priority = priority;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.priority, other.priority);
        }
    }
}