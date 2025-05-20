package algorithm.heuristic;

import model.Board;
import model.Piece;

public class BlockingCarsHeuristic implements Heuristic {
    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (primary == null || !primary.isHorizontal) return Integer.MAX_VALUE;
        
        int row = primary.start.row;
        int col = primary.start.col + primary.length;
        int count = 0;
        
        while (col < board.cols) {
            char cell = board.grid[row][col];
            if (cell != '.' && cell != 'K') {
                count += (board.cols - col);
            }
            col++;
        }
        
        return count;
    }

    @Override
    public String toString() {
        return "BlockingCarsHeuristic";
    }
}