package algorithm;

import model.Board;
import model.Piece;

public class BlockingCarsHeuristic implements Heuristic {

    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (!primary.isHorizontal) return 0;

        int row = primary.start.row;
        int col = primary.start.col + primary.length;

        int count = 0;
        while (col < board.cols) {
            if (board.grid[row][col] != '.' && board.grid[row][col] != 'K') {
                count++;
            }
            col++;
        }

        return count;
    }

    @Override
    public String toString() {
        String s = "BlockingCarsHeuristic";
        return s;
    }
}
