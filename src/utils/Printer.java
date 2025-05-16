package utils;

import model.Board;

public class Printer {
    public static void print(Board board) {
        for (int i = 0; i < board.rows; i++) {
            for (int j = 0; j < board.cols; j++) {
                char c = board.grid[i][j];
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
}
