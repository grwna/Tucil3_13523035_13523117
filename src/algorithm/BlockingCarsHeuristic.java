package algorithm;

import model.Board;
import model.Piece;

public class BlockingCarsHeuristic implements Heuristic {

    @Override
    public int estimate(Board board) {
        Piece primary = board.pieces.get('P');
        if (primary == null || !primary.isHorizontal) return Integer.MAX_VALUE; // Impossible
        
        int row = primary.start.row;
        int col = primary.start.col + primary.length;
        int count = 0;
        
        // Hitung mobil yang menghalangi jalur keluar
        while (col < board.cols) {
            char cell = board.grid[row][col];
            if (cell != '.' && cell != 'K') {
                // Tambahkan berat pada mobil yang menghalangi (prioritaskan yang terdekat dengan primary)
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