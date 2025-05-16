package model;

import java.util.*;

public class Board {
    public int rows, cols;
    public Map<Character, Piece> pieces;
    public char[][] grid;
    public Position exit;

    public Board(int rows, int cols, char[][] grid, Map<Character, Piece> pieces, Position exit) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
        this.pieces = pieces;
        this.exit = exit;
    }

    public Board copy() {
        Map<Character, Piece> copiedPieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            copiedPieces.put(entry.getKey(), entry.getValue().copy());
        }

        char[][] copiedGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            copiedGrid[i] = Arrays.copyOf(grid[i], cols);
        }

        return new Board(rows, cols, copiedGrid, copiedPieces, exit.copy());
    }

    public boolean isSolved() {
        Piece primary = pieces.get('P');
        if (primary == null || !primary.isHorizontal) return false;

        int row = primary.start.row;
        int rightEdge = primary.start.col + primary.length - 1;

        // Jika exit tidak didefinisikan, cukup periksa apakah ada jalur ke kanan
        if (exit == null) {
            // Geser dari ujung primary ke kanan sampai ujung papan
            for (int col = rightEdge + 1; col < cols; col++) {
                if (grid[row][col] != '.' && grid[row][col] != 'K') {
                    return false;
                }
            }
            // Jika semua sel kosong, mobil dapat keluar
            return true;
        } else {
            // Jika exit didefinisikan, periksa apakah mobil dapat mencapai exit
            if (exit.row != row) return false;  // exit tidak di baris yang sama

            // Periksa apakah jalur ke exit bebas hambatan
            for (int col = rightEdge + 1; col <= exit.col; col++) {
                if (grid[row][col] != '.' && grid[row][col] != 'K') {
                    return false;
                }
            }
            return true;
        }
    }

    public Set<Position> getOccupiedPositions(Piece piece) {
        Set<Position> result = new HashSet<>();
        int row = piece.start.row;
        int col = piece.start.col;
        for (int i = 0; i < piece.length; i++) {
            result.add(new Position(row, col));
            if (piece.isHorizontal) col++;
            else row++;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Board)) return false;
        Board b = (Board) o;
        return Arrays.deepEquals(this.grid, b.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }
}