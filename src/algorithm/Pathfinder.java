package algorithm;

import model.*;

import java.util.*;

public abstract class Pathfinder {
    public abstract List<State> solve(Board start);
    public abstract String getName();

    protected List<State> generateNeighbors(State current) {
        List<State> result = new ArrayList<>();
        Board board = current.board;

        for (Piece piece : board.pieces.values()) {
            for (int delta = -1; delta <= 1; delta += 2) {
                for (int step = 1; step <= 5; step++) {
                    Board newBoard = board.copy();
                    Piece moving = newBoard.pieces.get(piece.id);
                    int newRow = moving.start.row + (moving.isHorizontal ? 0 : delta * step);
                    int newCol = moving.start.col + (moving.isHorizontal ? delta * step : 0);

                    if (!canMove(board, piece, delta * step)) break;

                    moving.start.row = newRow;
                    moving.start.col = newCol;

                    newBoard.grid = applyMove(board.grid, moving);
                    String move = piece.id + "-" + (moving.isHorizontal ? (delta == 1 ? "kanan" : "kiri") : (delta == 1 ? "bawah" : "atas"));
                    result.add(new State(newBoard, move, current));
                }
            }
        }
        return result;
    }

    private boolean canMove(Board board, Piece piece, int step) {
        int row = piece.start.row;
        int col = piece.start.col;

        for (int i = 1; i <= Math.abs(step); i++) {
            int r = row + (piece.isHorizontal ? 0 : i * Integer.signum(step));
            int c = col + (piece.isHorizontal ? i * Integer.signum(step) : 0);

            if (r < 0 || r >= board.rows || c < 0 || c >= board.cols) return false;

            char ch = board.grid[r][c];
            if (ch != '.' && ch != 'K') return false;
        }

        return true;
    }

    private char[][] applyMove(char[][] grid, Piece piece) {
        char[][] newGrid = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], grid[i].length);
        }

        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[0].length; j++) {
                if (newGrid[i][j] == piece.id) newGrid[i][j] = '.';
            }
        }

        int r = piece.start.row;
        int c = piece.start.col;
        for (int i = 0; i < piece.length; i++) {
            newGrid[r][c] = piece.id;
            if (piece.isHorizontal) c++;
            else r++;
        }

        return newGrid;
    }
}
