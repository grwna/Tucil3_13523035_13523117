
package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import model.State;

public class OutputWriter {
    public static String writeSolution(List<State> solution, String algorithmName, double executionTime, String outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("==========================================================");
            writer.println("  RUSH HOUR GAME SOLUTION USING " + algorithmName.toUpperCase());
            writer.println("==========================================================");
            writer.println();
            writer.println("Number of moves: " + (solution.size() - 1));
            writer.println("Execution time: " + String.format("%.3f ms", executionTime));
            writer.println();
            writer.println("----------------------------------------------------------");
            writer.println("SOLUTION STEPS:");
            writer.println("----------------------------------------------------------");
            State state;
            for (int i = 1; i < solution.size(); i++) {
                state = solution.get(i);
                writer.println("Move " + i + ": " + state.move);
            }
            writer.println("\n");
            for (int i = 0; i < solution.size(); i++){
                state = solution.get(i);
                writer.print("\nMove " + i + ": " + state.move + "\n");
                for (int r = 0; r < state.board.rows; r++) {
                    for (int c = 0; c < state.board.cols; c++) {
                        writer.print(state.board.grid[r][c]);
                    }
                    writer.println();
                }
            }
            writer.println("----------------------------------------------------------");
            writer.println("\nSolution finished. Game solved successfully!");
        }
        
        return outputPath;
    }

}