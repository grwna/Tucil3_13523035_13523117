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

    public static Board parseFromFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        Position exit = null;
        char[][] grid;
        int usableRows, usableCols;
        Map<Character, Piece> pieces = new HashMap<>();
        Set<Position> allCellsForPieces = new HashSet<>();
        Set<Character> validSymbols = new HashSet<>();
        boolean hasPrimaryPiece = false;

        try {
            String dimsLine = br.readLine();
            if (dimsLine == null) throw new IOException("Missing dimensions line.");
            String[] sizeParts = dimsLine.split(" ");
            if (sizeParts.length != 2) throw new IOException("Invalid dimensions format: " + dimsLine);
            
            try {
                usableRows = Integer.parseInt(sizeParts[0]);
                usableCols = Integer.parseInt(sizeParts[1]);
                
                if (usableRows <= 0 || usableCols <= 0) {
                    throw new IOException("Board dimention can't be negative: " + usableRows + "x" + usableCols);
                }
            } catch (NumberFormatException e) {
                throw new IOException("Board dimention must be integer: " + dimsLine);
            }

            grid = new char[usableRows + 2][usableCols + 2];
            for (char[] row : grid) Arrays.fill(row, ' ');

            String pieceCountLine = br.readLine();
            if (pieceCountLine == null) throw new IOException("Missing piece count or first grid line.");
            
            int pieceCount = -1;
            try {
                pieceCount = Integer.parseInt(pieceCountLine.trim());
                if (pieceCount < 0) {
                    throw new IOException("The number of character (piece) has to be positive: " + pieceCount);
                }
            } catch (NumberFormatException e) {
                // Bukan angka, anggap sebagai baris pertama grid
                pieceCountLine = null;
            }

            String lineForKAboveOrFirstGrid = pieceCountLine != null ? br.readLine() : pieceCountLine;
            if (lineForKAboveOrFirstGrid == null) throw new IOException("Missing K_above or first grid line.");
            int KColInLine = getKColIdx(lineForKAboveOrFirstGrid);
            
            int totalKCount = 0;
            
            if (KColInLine != -1) {
                if (KColInLine >= usableCols) throw new IOException("K at column " + KColInLine + " is outside playable width " + usableCols);
                exit = new Position(0, KColInLine + 1);
                grid[0][KColInLine + 1] = 'K';
                lineForKAboveOrFirstGrid = null;
                totalKCount++;
            }

            int actualRows = 0;
            
            for (int i = 0; i < usableRows; i++) {
                String currentLine = (i == 0 && lineForKAboveOrFirstGrid != null) ? lineForKAboveOrFirstGrid : br.readLine();
                while (currentLine != null && currentLine.trim().isEmpty()) {
                    currentLine = br.readLine();
                }
                if (currentLine == null) throw new IOException("EOF: Expected " + usableRows + " grid rows, found " + i);
                actualRows++;

                char[] lineChars;
                String originalLine = currentLine;
                currentLine = currentLine.replaceAll(" ", "");
                
                // K di kiri
                boolean hasLeftK = false;
                if (currentLine.length() > 0 && currentLine.charAt(0) == 'K') {
                    if (totalKCount > 0) throw new IOException("Multiple K definitions found (total so far: " + totalKCount + ")");
                    hasLeftK = true;
                    exit = new Position(i + 1, 0);
                    grid[i+1][0] = 'K';
                    currentLine = currentLine.substring(1);
                    totalKCount++;
                }
                
                // K di kanan
                boolean hasRightK = false;
                if (currentLine.length() > 0 && currentLine.charAt(currentLine.length() - 1) == 'K') {
                    if (totalKCount > 0) throw new IOException("Multiple K definitions found (total so far: " + totalKCount + ")");
                    hasRightK = true;
                    exit = new Position(i + 1, usableCols + 1);
                    grid[i+1][usableCols+1] = 'K';
                    currentLine = currentLine.substring(0, currentLine.length() - 1);
                    totalKCount++;
                }
                
                if (currentLine.length() == usableCols) {
                    lineChars = currentLine.toCharArray();
                    for (int j = 0; j < usableCols; j++) {
                        char c = lineChars[j];
                        // Validasi karakter
                        if (!isValidChar(c)) {
                            throw new IOException("Invalid character '" + c + "' at (" + (i+1) + "," + (j+1) + " position)");
                        }
                        if (c == 'P') hasPrimaryPiece = true;
                        if (c != ' ' && c != '.' && c != 'K') validSymbols.add(c);
                        grid[i+1][j+1] = c;
                    }
                } else {
                    String errorMsg = "Row " + (i+1) + " has incorrect length. ";
                    if (hasLeftK) errorMsg += "Found K at left, ";
                    if (hasRightK) errorMsg += "Found K at right, ";
                    errorMsg += "Current content length: " + currentLine.length() + ", expected: " + usableCols;
                    throw new IOException(errorMsg);
                }
            }
            
            if (actualRows != usableRows) {
                throw new IOException("The number of row doesn't match: expected " + usableRows + ", but only found " + actualRows);
            }

            if (exit == null) {
                String kBelowLine = br.readLine();
                if (kBelowLine != null) {
                    KColInLine = getKColIdx(kBelowLine);
                    if (KColInLine != -1) {
                        if (KColInLine >= usableCols) throw new IOException("K at column " + KColInLine + " is outside playable width " + usableCols);
                        exit = new Position(usableRows + 1, KColInLine + 1);
                        grid[usableRows+1][KColInLine+1] = 'K';
                        totalKCount++;
                    } else throw new IOException("Extra data after grid, not valid line for K");
                }
            }
            
            if (totalKCount == 0) {
                throw new IOException("There is no 'K' character (exit) on the board");
            } else if (totalKCount > 1) {
                throw new IOException("There is more than one 'K' character (exit), found: " + totalKCount);
            }
            
            if (!hasPrimaryPiece) {
                throw new IOException("There is no 'P' (primary piece) on the board");
            }
            
            int pCount = 0;
            Position pStart = null;

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
                        throw new IOException("Wrong piece configuration for '" + val + "' found!");
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
                    
                    if (val == 'P') {
                        pCount++;
                        pStart = currentPos;
                    }
                    
                    pieces.put(val, new Piece(val, currentPos, len, horizontal));
                }
            }
            
            Piece primaryPiece = pieces.get('P');
            if (primaryPiece != null) {
                boolean canReachExit = false;
                
                if (primaryPiece.isHorizontal) {
                    if ((exit.col == 0 || exit.col == usableCols + 1) && 
                        exit.row == primaryPiece.start.row) {
                        canReachExit = true;
                    }
                } 
                else {
                    if ((exit.row == 0 || exit.row == usableRows + 1) && 
                        exit.col == primaryPiece.start.col) {
                        canReachExit = true;
                    }
                }
                
                if (!canReachExit) {
                    throw new IOException("Primary piece 'P' can't reach the exit 'K' (Make sure no extra whitespace on the board!)");
                }
            }
            for (char i = 'A'; i <= 'Z'; i++) {
                if ((char) i != 'K' && pieces.containsKey((char)i)){
                    if (pieces.get((char)i).length < 2 || pieces.get(i).length > 3){
                        throw new IOException("Invalid piece length " + (char) i);
                    }
                }
            }
            return new Board(usableRows + 2, usableCols + 2, grid, pieces, exit);
        } finally {
            br.close();
        }
    }
    
    private static boolean isValidChar(char c) {
        return Character.isLetterOrDigit(c) || c == ' ' || c == '.' || c == 'K';
    }
}