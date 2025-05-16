package algorithm;

import model.Board;
import model.State;

import java.util.*;

public class UCS extends Pathfinder {

    @Override
    public String getName() {
        return "Uniform Cost Search";
    }

    @Override
    public List<State> solve(Board startBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Board> visited = new HashSet<>();
        Map<Board, Integer> gScore = new HashMap<>();

        State start = new State(startBoard, "Start", null);
        openSet.add(new Node(start, 0));
        gScore.put(startBoard, 0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            State currState = current.state;
            Board board = currState.board;

            if (board.isSolved()) {
                return reconstructStatePath(currState);
            }

            visited.add(board);

            for (State neighbor : generateNeighbors(currState)) {
                if (visited.contains(neighbor.board)) continue;

                int tentativeG = current.cost + 1;
                if (!gScore.containsKey(neighbor.board) || tentativeG < gScore.get(neighbor.board)) {
                    gScore.put(neighbor.board, tentativeG);
                    openSet.add(new Node(neighbor, tentativeG));
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
