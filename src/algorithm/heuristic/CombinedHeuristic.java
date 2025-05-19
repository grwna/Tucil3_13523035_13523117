package algorithm.heuristic;

import model.Board;
import model.Piece;

public class CombinedHeuristic implements Heuristic {

    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (primary == null || !primary.isHorizontal) return Integer.MAX_VALUE;

        int row = primary.start.row;
        int endCol = primary.start.col + primary.length - 1;

        // Komponen 1: Manhattan Distance to Exit
        int distanceToExit = Math.abs(endCol - (board.cols - 1));

        // Komponen 2: Blocking Cars
        int blockingPenalty = 0;
        int weightedBlocking = 0;
        for (int col = endCol + 1; col < board.cols; col++) {
            char cell = board.grid[row][col];
            if (cell != '.' && cell != 'K') {
                blockingPenalty++;
                weightedBlocking += (board.cols - col);  // Prioritaskan yang dekat
            }
        }

        // Gabungan (bobot dapat disesuaikan)
        int result = distanceToExit + (2 * blockingPenalty) + weightedBlocking;

        return result;
    }

    @Override
    public String toString() {
        return "CombinedHeuristic";
    }
}
