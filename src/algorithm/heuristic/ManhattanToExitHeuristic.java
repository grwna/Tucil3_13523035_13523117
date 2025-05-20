package algorithm.heuristic;

import model.Board;
import model.Piece;

public class ManhattanToExitHeuristic implements Heuristic {
    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (primary == null || !primary.isHorizontal) return Integer.MAX_VALUE;
        
        int row = primary.start.row;
        int endCol = primary.start.col + primary.length - 1;
        
        int distanceToExit = Math.abs(endCol - (board.cols - 1));
        
        int blocking = 0;
        for (int col = endCol + 1; col < board.cols; col++) {
            if (board.grid[row][col] != '.' && board.grid[row][col] != 'K') {
                blocking++;
            }
        }
        
        return distanceToExit + (blocking * 2);
    }

    @Override
    public String toString() {
        return "ManhattanToExitHeuristic";
    }
}