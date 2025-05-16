package algorithm;

import model.Board;

public interface Heuristic {
    int estimate(Board board);
}
