package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Board;
import model.Piece;
import model.Position;

public class InputParser {

    private static int getKColIdx(String line) {
        if (line == null) return -1;
        int kPos = -1, kCount = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == 'K') { kPos = i; kCount++; }
            else if (c != ' ') return -1;
        }
        return (kCount == 1) ? kPos : -1;
    }

        // Calculate Length without counting spaces
    public static int effLength(String string){
        int length = 0;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != 'K') length++;
        }
        return length;
    }

    public static Board parseFromFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        Position exit = null;
        char[][] grid;
        int usableRows, usableCols;
        Map<Character, Piece> pieces = new HashMap<>();
        Set<Position> allCellsForPieces = new HashSet<>();

        try {
            String dimsLine = br.readLine();
            if (dimsLine == null) throw new IOException("Missing dimensions line.");
            String[] sizeParts = dimsLine.split(" ");
            if (sizeParts.length != 2) throw new IOException("Invalid dimensions format: " + dimsLine);
            usableRows = Integer.parseInt(sizeParts[0]);
            usableCols = Integer.parseInt(sizeParts[1]);

            grid = new char[usableRows + 2][usableCols + 2];
            for (char[] row : grid) Arrays.fill(row, ' ');

            if (br.readLine() == null) throw new IOException("Missing piece count or K/first grid line.");

            String lineForKAboveOrFirstGrid = br.readLine();
            if (lineForKAboveOrFirstGrid == null) throw new IOException("Missing K_above or first grid line.");
            int KColInLine = getKColIdx(lineForKAboveOrFirstGrid);
            if (KColInLine != -1) {
                if (KColInLine >= usableCols) throw new IOException("K at column " + KColInLine + " is outside playable width " + usableCols);
                exit = new Position(0, KColInLine + 1);
                grid[0][KColInLine + 1] = 'K';
                lineForKAboveOrFirstGrid = null;
            }

            for (int i = 0; i < usableRows; i++) {
                String currentLine = (i == 0 && lineForKAboveOrFirstGrid != null) ? lineForKAboveOrFirstGrid : br.readLine();
                if (currentLine == null) throw new IOException("EOF: Expected " + usableRows + " grid rows, found " + i);

                char[] lineChars;
                if (currentLine.length() == usableCols) {
                    lineChars = currentLine.toCharArray();
                    for (int j = 0; j < usableCols; j++) grid[i+1][j+1] = lineChars[j];
                } else if (currentLine.length() == usableCols + 1) {
                    if (exit != null) throw new IOException("Multiple K definitions found.");
                    if (currentLine.charAt(0) == 'K') {
                        exit = new Position(i + 1, 0);
                        grid[i+1][0] = 'K';
                        lineChars = currentLine.substring(1).toCharArray();
                    } else if (currentLine.charAt(usableCols) == 'K') {
                        exit = new Position(i + 1, usableCols + 1);
                        grid[i+1][usableCols+1] = 'K';
                        lineChars = currentLine.substring(0, usableCols).toCharArray();
                    } else throw new IOException("Malformed line " + (i+1) + " (len " + (usableCols+1) + ", no K at ends).");

                    for (int j = 0; j < usableCols; j++) grid[i+1][j+1] = lineChars[j];
                } else throw new IOException("Malformed line " + (i+1) + " (unexpected length " + currentLine.length() + ").");
            }

            if (exit == null) {
                String kBelowLine = br.readLine();
                if (kBelowLine != null) {
                    KColInLine = getKColIdx(kBelowLine);
                    if (KColInLine != -1) {
                        if (KColInLine >= usableCols) throw new IOException("K at column " + KColInLine + " is outside playable width " + usableCols);
                        exit = new Position(usableRows + 1, KColInLine + 1);
                        grid[usableRows+1][KColInLine+1] = 'K';
                    } else throw new IOException("Extra data after grid, not valid line for K");
                }
            }

            for (int r = 0; r < usableRows; r++) {
                for (int c = 0; c < usableCols; c++) {
                    char val = grid[r+1][c+1];
                    Position currentPos = new Position(r + 1, c + 1);

                    if (val == ' ' || val == '.') continue;
                    if (allCellsForPieces.contains(currentPos)) continue;

                    if (val == 'K') {
                        throw new IOException("Character 'K' found inside playable area");
                    }

                    if (pieces.containsKey(val)) {
                        throw new IOException("Duplicate piece '" + val + "' found!");
                    }

                    List<Position> currPieceOccupied = new ArrayList<>();
                    currPieceOccupied.add(currentPos);
                    int len = 1;
                    boolean horizontal = false;

                    if (c + 1 < usableCols && grid[r+1][(c+1)+1] == val) {
                        horizontal = true;
                        for (int cc = c + 1; cc < usableCols; cc++) {
                            if (grid[r+1][cc+1] == val) {
                                Position nextPos = new Position(r+1, cc+1);
                                if(allCellsForPieces.contains(nextPos)) throw new IOException("Piece '"+val+"' at "+currentPos+" overlaps with another piece at "+nextPos);
                                currPieceOccupied.add(nextPos);
                                len++;
                            } else break;
                        }
                    } else if (r + 1 < usableRows && grid[(r+1)+1][c+1] == val) {
                        for (int rr = r + 1; rr < usableRows; rr++) {
                            if (grid[rr+1][c+1] == val) {
                                Position nextPos = new Position(rr+1, c+1);
                                if(allCellsForPieces.contains(nextPos)) throw new IOException("Piece '"+val+"' at "+currentPos+" overlaps with another piece at "+nextPos);
                                currPieceOccupied.add(nextPos);
                                len++;
                            } else break;
                        }
                    }

                    for(Position p : currPieceOccupied) {
                        if(!allCellsForPieces.add(p)) {
                             throw new IOException("Cell " + p + " for piece '" + val + "' was already processed, indicating overlap or malformed piece.");
                        }
                    }
                    pieces.put(val, new Piece(val, currentPos, len, horizontal));
                }
            }

            if (exit == null) throw new IOException("Exit 'K' not found.");
            Board result = new Board(usableRows + 2, usableCols + 2, grid, pieces, exit);

            return result;
        } finally {
            br.close();
        }
    }
}
