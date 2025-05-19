package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Position adjustedExit = new Position(exit.row, exit.col);
        if (adjustedExit.row == 0) {adjustedExit.row = 1;} 
        else if (adjustedExit.row == rows - 1) {adjustedExit.row = rows - 2;}
        if (adjustedExit.col == 0) {adjustedExit.col = 1;}
        else if (adjustedExit.col == cols - 1) {adjustedExit.col = cols - 2;}

        this.exit = adjustedExit;
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
        if (primary.isHorizontal) {
            if (primary.start.row != this.exit.row) {return false;}

            for (int i = 0; i < primary.length; i++) {
                if (primary.start.col + i == this.exit.col) {
                    return true;
                }
            }
        } else {
            if (primary.start.col != this.exit.col) {return false;}
           
            for (int i = 0; i < primary.length; i++) {
                if (primary.start.row + i == this.exit.row) {
                    return true;
                }
            }
        }
        return false;
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

    public void print() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                char c = this.grid[i][j];
                if (c == 'P') {
                    System.out.print("\u001B[31m" + c + "\u001B[0m"); // Merah
                } else if (c == 'K') {
                    System.out.print("\u001B[34m" + c + "\u001B[0m"); // Biru
                } else {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    // Used for GUI animation
    public static Board createIntermediateBoard(Board startBoard, Board endBoard, char pieceId, String direction, int currentStep, int totalSteps) {
        Board intermediateBoard = startBoard.copy();
        Piece piece = intermediateBoard.pieces.get(pieceId);
        if (piece == null) {
            return intermediateBoard;
        }
        
        for (int r = 0; r < intermediateBoard.rows; r++) {
            for (int c = 0; c < intermediateBoard.cols; c++) {
                if (intermediateBoard.grid[r][c] == pieceId) {
                    intermediateBoard.grid[r][c] = '.';
                }
            }
        }
        
        int deltaRow = 0;
        int deltaCol = 0;
        
        switch (direction.toLowerCase()) {
            case "up":
                deltaRow = -currentStep;
                break;
            case "down":
                deltaRow = currentStep;
                break;
            case "left":
                deltaCol = -currentStep;
                break;
            case "right":
                deltaCol = currentStep;
                break;
        }
        
        piece.start = new Position(startBoard.pieces.get(pieceId).start.row + deltaRow, startBoard.pieces.get(pieceId).start.col + deltaCol);
        
        int r = piece.start.row;
        int c = piece.start.col;
        for (int i = 0; i < piece.length; i++) {
            intermediateBoard.grid[r][c] = pieceId;
            if (piece.isHorizontal) c++;
            else r++;
        }
        
        return intermediateBoard;
    }
}