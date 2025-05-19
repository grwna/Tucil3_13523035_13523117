package algorithm.heuristic;

import model.Board;

public interface Heuristic {
    int estimate(Board board);
}
