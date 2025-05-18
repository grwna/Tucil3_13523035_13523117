package parser;

import java.io.BufferedReader;
import java.io.File;
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
        // Trim the line and handle the case of a single K
        line = line.trim();
        if (line.equals("K")) return 0;
        
        int kPos = -1, kCount = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == 'K') { kPos = i; kCount++; }
            else if (c != ' ') return -1;
        }
        return (kCount == 1) ? kPos : -1;
    }

    public static Board parseFromFile(String path) throws IOException {
        // Validasi file ada
        File inputFile = new File(path);
        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new IOException("File is not found: " + path);
        }

        BufferedReader br = new BufferedReader(new FileReader(path));
        Position exit = null;
        char[][] grid;
        int usableRows, usableCols;
        Map<Character, Piece> pieces = new HashMap<>();
        Set<Position> allCellsForPieces = new HashSet<>();
        Set<Character> validSymbols = new HashSet<>();
        boolean hasPrimaryPiece = false;

        try {
            // Validasi 
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

            // Check for K above board
            String kLineCheck = br.readLine();
            if (kLineCheck == null) throw new IOException("Unexpected end of file after piece count.");
            
            String firstGridLine = null;
            int KColInLine = getKColIdx(kLineCheck);
            int totalKCount = 0;
            
            // Handle 'K' above the grid
            if (KColInLine != -1) {
                // Fix: Calculate the actual column position based on the spaced line
                // Find the actual position of K after handling spaces
                int adjustedKCol = -1;
                int nonSpaceCount = 0;
                
                for (int i = 0; i < kLineCheck.length(); i++) {
                    char c = kLineCheck.charAt(i);
                    if (c == 'K') {
                        adjustedKCol = nonSpaceCount;
                        break;
                    } else if (c != ' ') {
                        nonSpaceCount++;
                    }
                }
                
                if (adjustedKCol == -1) adjustedKCol = KColInLine;
                
                if (adjustedKCol >= usableCols) throw new IOException("K at column " + adjustedKCol + " is outside playable width " + usableCols);
                exit = new Position(0, adjustedKCol + 1);
                grid[0][adjustedKCol + 1] = 'K';
                totalKCount++;
                
                // After finding K on top, read the first actual grid line
                firstGridLine = br.readLine();
                if (firstGridLine == null) throw new IOException("Missing first grid line after K above.");
            } else {
                // If the line we read isn't a 'K' line, it must be the first grid line
                firstGridLine = kLineCheck;
            }

            int actualRows = 0;
            
            for (int i = 0; i < usableRows; i++) {
                String currentLine = (i == 0) ? firstGridLine : br.readLine();
                
                while (currentLine != null && currentLine.trim().isEmpty()) {
                    // Skip empty lines
                    currentLine = br.readLine();
                }
                
                if (currentLine == null) throw new IOException("EOF: Expected " + usableRows + " grid rows, found " + i);
                actualRows++;

                // Handle differently formatted lines (with spaces)
                String normalizedLine = currentLine.trim().replace(" ", "");
                int lineLength = normalizedLine.length();
                
                // Detect if K is on the side
                boolean kOnLeft = false, kOnRight = false;
                if (lineLength > 0 && normalizedLine.charAt(0) == 'K') {
                    kOnLeft = true;
                    normalizedLine = normalizedLine.substring(1);
                    lineLength--;
                } else if (lineLength > 0 && normalizedLine.charAt(lineLength - 1) == 'K') {
                    kOnRight = true;
                    normalizedLine = normalizedLine.substring(0, lineLength - 1);
                    lineLength--;
                }

                if (lineLength == usableCols) {
                    // Process grid line content - normalize to remove spaces
                    for (int j = 0; j < lineLength; j++) {
                        char c = normalizedLine.charAt(j);
                        
                        // Validate character
                        if (!isValidChar(c)) {
                            throw new IOException("Invalid character '" + c + "' at (" + (i+1) + "," + (j+1) + " position)");
                        }
                        if (c == 'P') hasPrimaryPiece = true;
                        if (c != ' ' && c != '.' && c != 'K') validSymbols.add(c);
                        grid[i+1][j+1] = c;
                    }
                    
                    // Handle K on sides if found
                    if (kOnLeft) {
                        if (totalKCount > 0) throw new IOException("Multiple K definitions found");
                        exit = new Position(i + 1, 0);
                        grid[i+1][0] = 'K';
                        totalKCount++;
                    } else if (kOnRight) {
                        if (totalKCount > 0) throw new IOException("Multiple K definitions found");
                        exit = new Position(i + 1, usableCols + 1);
                        grid[i+1][usableCols+1] = 'K';
                        totalKCount++;
                    }
                } else {
                    throw new IOException("Row " + (i+1) + "'s effective length (" + lineLength + ") does not match board's width (" + usableCols + ")");
                }
            }
            
            if (actualRows != usableRows) {
                throw new IOException("The number of row doesn't match: expected " + usableRows + ", but only found " + actualRows);
            }

            // Check for K below board if no K found yet
            if (exit == null) {
                String kBelowLine = br.readLine();
                if (kBelowLine != null) {
                    KColInLine = getKColIdx(kBelowLine);
                    if (KColInLine != -1) {
                        // Same fix as for K above - handle spaces properly
                        int adjustedKCol = -1;
                        int nonSpaceCount = 0;
                        
                        for (int i = 0; i < kBelowLine.length(); i++) {
                            char c = kBelowLine.charAt(i);
                            if (c == 'K') {
                                adjustedKCol = nonSpaceCount;
                                break;
                            } else if (c != ' ') {
                                nonSpaceCount++;
                            }
                        }
                        
                        if (adjustedKCol == -1) adjustedKCol = KColInLine;
                        
                        if (adjustedKCol >= usableCols) throw new IOException("K at column " + adjustedKCol + " is outside playable width " + usableCols);
                        exit = new Position(usableRows + 1, adjustedKCol + 1);
                        grid[usableRows+1][adjustedKCol+1] = 'K';
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
                    throw new IOException("Primary piece 'P' can't reach the exit 'K' because invalid orientation or position");
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