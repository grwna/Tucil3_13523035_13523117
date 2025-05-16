package parser;

import model.*;

import java.io.*;
import java.util.*;

public class InputParser {
    public static Board parseFromFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));

        String[] size = br.readLine().split(" ");
        int rows = Integer.parseInt(size[0]);
        int cols = Integer.parseInt(size[1]);
        int pieceCount = Integer.parseInt(br.readLine());

        char[][] grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String line = br.readLine();
            grid[i] = line.toCharArray();
        }

        Map<Character, Piece> pieces = new HashMap<>();
        Position exit = null;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = grid[i][j];
                if (c == '.' || c == ' ') continue;
                if (c == 'K') {
                    exit = new Position(i, j);
                    continue;
                }
                if (!pieces.containsKey(c)) {
                    int len = 1;
                    boolean horizontal = false;

                    if (j + 1 < cols && grid[i][j + 1] == c) {
                        horizontal = true;
                        int jj = j + 1;
                        while (jj < cols && grid[i][jj] == c) {
                            len++;
                            jj++;
                        }
                    } else if (i + 1 < rows && grid[i + 1][j] == c) {
                        int ii = i + 1;
                        while (ii < rows && grid[ii][j] == c) {
                            len++;
                            ii++;
                        }
                    }

                    pieces.put(c, new Piece(c, new Position(i, j), len, horizontal));
                }
            }
        }

        br.close();
        return new Board(rows, cols, grid, pieces, exit);
    }
}
