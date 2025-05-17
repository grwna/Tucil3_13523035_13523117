package algorithm;

import model.Board;
import model.Piece;

public class ManhattanToExitHeuristic implements Heuristic {

    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (primary == null || !primary.isHorizontal) return Integer.MAX_VALUE; // Impossible
        
        int row = primary.start.row;
        int endCol = primary.start.col + primary.length - 1;
        
        // Jarak mobil utama ke exit
        int distanceToExit = Math.abs(endCol - (board.cols - 1));
        
        // Tambahan: penalti untuk mobil yang menghalangi jalur keluar
        int blocking = 0;
        for (int col = endCol + 1; col < board.cols; col++) {
            if (board.grid[row][col] != '.' && board.grid[row][col] != 'K') {
                blocking++;
            }
        }
        
        return distanceToExit + (blocking * 2); // Beri bobot lebih untuk mobil yang menghalangi
    }

    @Override
    public String toString() {
        return "ManhattanToExitHeuristic";
    }
}