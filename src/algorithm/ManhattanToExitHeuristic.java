package algorithm;

import model.Board;
import model.Piece;

public class ManhattanToExitHeuristic implements Heuristic {

    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (!primary.isHorizontal) return 0;

        int endCol = primary.start.col + primary.length - 1;
        return Math.abs(endCol - board.exit.col);
    }

    @Override
    public String toString() {
        String s = "ManhattanToExitHeuristic" ;
        return s;
    }
}
