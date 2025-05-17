package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Board;
import model.Piece;
import model.Position;
import model.State;

public class Pathfinder {
    public List<State> solve(Board startBoard) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String getName() {
        return "Abstract Pathfinder";
    }

    // Perbaikan pada fungsi generateNeighbors
    protected List<State> generateNeighbors(State current) {
        List<State> result = new ArrayList<>();
        Board board = current.board;

        for (Piece piece : board.pieces.values()) {
            // Kita hanya perlu mencoba satu langkah pada satu waktu
            // dan hanya gerak maksimal sebanyak yang memungkinkan
            if (piece.isHorizontal) {
                // Coba gerak ke kiri
                for (int step = 1; step <= board.cols; step++) {
                    if (canMovePieceHorizontal(board, piece, -step)) {
                        Board newBoard = movePieceAndCreateNewBoard(board, piece, 0, -step);
                        String move = piece.id + "-kiri" + (step > 1 ? step : "");
                        result.add(new State(newBoard, move, current));
                    } else {
                        break; // Berhenti kalau tidak bisa gerak lebih jauh
                    }
                }
                
                // Coba gerak ke kanan
                for (int step = 1; step <= board.cols; step++) {
                    if (canMovePieceHorizontal(board, piece, step)) {
                        Board newBoard = movePieceAndCreateNewBoard(board, piece, 0, step);
                        String move = piece.id + "-kanan" + (step > 1 ? step : "");
                        result.add(new State(newBoard, move, current));
                    } else {
                        break; // Berhenti kalau tidak bisa gerak lebih jauh
                    }
                }
            } else {
                // Coba gerak ke atas
                for (int step = 1; step <= board.rows; step++) {
                    if (canMovePieceVertical(board, piece, -step)) {
                        Board newBoard = movePieceAndCreateNewBoard(board, piece, -step, 0);
                        String move = piece.id + "-atas" + (step > 1 ? step : "");
                        result.add(new State(newBoard, move, current));
                    } else {
                        break; // Berhenti kalau tidak bisa gerak lebih jauh
                    }
                }
                
                // Coba gerak ke bawah
                for (int step = 1; step <= board.rows; step++) {
                    if (canMovePieceVertical(board, piece, step)) {
                        Board newBoard = movePieceAndCreateNewBoard(board, piece, step, 0);
                        String move = piece.id + "-bawah" + (step > 1 ? step : "");
                        result.add(new State(newBoard, move, current));
                    } else {
                        break; // Berhenti kalau tidak bisa gerak lebih jauh
                    }
                }
            }
        }
        return result;
    }

    // Perbaikan pada fungsi canMove untuk horizontal movement
    private boolean canMovePieceHorizontal(Board board, Piece piece, int deltaCol) {
        if (!piece.isHorizontal) return false;
        
        int row = piece.start.row;
        int targetCol = (deltaCol > 0) ? 
                        piece.start.col + piece.length + deltaCol - 1 : // untuk ke kanan
                        piece.start.col + deltaCol; // untuk ke kiri
        
        // Periksa apakah target kolom valid
        if (targetCol < 0 || targetCol >= board.cols) {
            return false;
        }
        
        // Periksa apakah sel target kosong atau exit
        if (deltaCol > 0) { // Moving right
            for (int c = piece.start.col + piece.length; c <= piece.start.col + piece.length + deltaCol - 1; c++) {
                if (c >= 0 && c < board.cols) {
                    char cell = board.grid[row][c];
                    if (cell != '.' && cell != 'K') {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else { // Moving left
            for (int c = piece.start.col - 1; c >= piece.start.col + deltaCol; c--) {
                if (c >= 0 && c < board.cols) {
                    char cell = board.grid[row][c];
                    if (cell != '.' && cell != 'K') {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // Perbaikan pada fungsi canMove untuk vertical movement
    private boolean canMovePieceVertical(Board board, Piece piece, int deltaRow) {
        if (piece.isHorizontal) return false;
        
        int col = piece.start.col;
        int targetRow = (deltaRow > 0) ? 
                        piece.start.row + piece.length + deltaRow - 1 : // untuk ke bawah
                        piece.start.row + deltaRow; // untuk ke atas
        
        // Periksa apakah target baris valid
        if (targetRow < 0 || targetRow >= board.rows) {
            return false;
        }
        
        // Periksa apakah sel target kosong atau exit
        if (deltaRow > 0) { // Moving down
            for (int r = piece.start.row + piece.length; r <= piece.start.row + piece.length + deltaRow - 1; r++) {
                if (r >= 0 && r < board.rows) {
                    char cell = board.grid[r][col];
                    if (cell != '.' && cell != 'K') {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else { // Moving up
            for (int r = piece.start.row - 1; r >= piece.start.row + deltaRow; r--) {
                if (r >= 0 && r < board.rows) {
                    char cell = board.grid[r][col];
                    if (cell != '.' && cell != 'K') {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // Perbaikan pada fungsi untuk membuat board baru setelah pemindahan
    private Board movePieceAndCreateNewBoard(Board board, Piece piece, int deltaRow, int deltaCol) {
        Board newBoard = board.copy();
        Piece newPiece = newBoard.pieces.get(piece.id);
        
        // Hapus piece dari grid lama
        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.cols; c++) {
                if (newBoard.grid[r][c] == piece.id) {
                    newBoard.grid[r][c] = '.';
                }
            }
        }
        
        // Update posisi piece
        newPiece.start = new Position(piece.start.row + deltaRow, piece.start.col + deltaCol);
        
        // Letakkan piece di grid baru
        int r = newPiece.start.row;
        int c = newPiece.start.col;
        for (int i = 0; i < newPiece.length; i++) {
            newBoard.grid[r][c] = newPiece.id;
            if (newPiece.isHorizontal) c++;
            else r++;
        }
        
        return newBoard;
    }

    // Fungsi rekonstruksi jalur dari kondisi akhir ke awal
    protected List<State> reconstructStatePath(State goal) {
        List<State> path = new ArrayList<>();
        State current = goal;
        while (current != null) {
            path.add(current);
            current = current.prev;
        }
        Collections.reverse(path);
        return path;
    }
}