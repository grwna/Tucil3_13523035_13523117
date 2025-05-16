package algorithm;

import model.Board;
import model.State;

import java.util.*;

public class AStar extends Pathfinder {
    private Heuristic heuristic;

    public AStar(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public String getName() {
        return "A* Search";
    }

    @Override
    public List<State> solve(Board startBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Board, Integer> gScore = new HashMap<>();
        Set<Board> visited = new HashSet<>();

        State start = new State(startBoard, "Start", null);
        openSet.add(new Node(start, heuristic.estimate(start.board), 0));
        gScore.put(start.board, 0);

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

                int tentativeG = current.gScore + 1;
                if (!gScore.containsKey(neighbor.board) || tentativeG < gScore.get(neighbor.board)) {
                    gScore.put(neighbor.board, tentativeG);
                    int f = tentativeG + heuristic.estimate(neighbor.board);
                    openSet.add(new Node(neighbor, f, tentativeG));
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
