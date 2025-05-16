package algorithm;

import model.Board;
import model.State;

import java.util.*;

public class GreedyBestFirst extends Pathfinder {
    private Heuristic heuristic;

    public GreedyBestFirst(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "Greedy Best First Search";
    }

    @Override
    public List<State> solve(Board startBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Board> visited = new HashSet<>();

        State start = new State(startBoard, "Start", null);
        openSet.add(new Node(start, heuristic.estimate(startBoard)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            State currState = current.state;
            Board board = currState.board;

            if (board.isSolved()) {
                return reconstructStatePath(currState);
            }

            visited.add(board);

            for (State neighbor : generateNeighbors(currState)) {
                if (!visited.contains(neighbor.board)) {
                    openSet.add(new Node(neighbor, heuristic.estimate(neighbor.board)));
                }
            }
        }

        return new ArrayList<>();
    }

    private List<State> reconstructStatePath(State goal) {
        List<State> path = new ArrayList<>();
        State current = goal;
        while (current != null) {
            path.add(current);
            current = current.prev;
        }
        Collections.reverse(path);
        return path;
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
