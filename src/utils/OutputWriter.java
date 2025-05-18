
package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import model.State;

public class OutputWriter {
    private static final String OUTPUT_FOLDER = "test";
    
    /**
     * Writes the solution steps to a file in the test directory
     * 
     * @param solution The list of states representing the solution
     * @param algorithmName The name of the algorithm used to find the solution
     * @param executionTime The execution time in milliseconds
     * @return The path of the created file
     * @throws IOException If an I/O error occurs
     */
    public static String writeSolution(List<State> solution, String algorithmName, double executionTime, String outputPath) throws IOException {
        // Create the file and write the solution
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("==========================================================");
            writer.println("  RUSH HOUR GAME SOLUTION USING " + algorithmName.toUpperCase());
            writer.println("==========================================================");
            writer.println();
            writer.println("Number of moves: " + (solution.size() - 1));
            writer.println("Execution time: " + String.format("%.3f ms", executionTime));
            writer.println();
            writer.println("----------------------------------------------------------");
            State state = solution.get(0);
            writer.println("Start");
            for (int r = 0; r < state.board.rows; r++) {
                for (int c = 0; c < state.board.cols; c++) {
                    writer.print(state.board.grid[r][c]);
                }
                writer.println();
            }
            writer.println("----------------------------------------------------------");
            state = solution.get(solution.size()-1);
            writer.println("Final State");
            for (int r = 0; r < state.board.rows; r++) {
                for (int c = 0; c < state.board.cols; c++) {
                    writer.print(state.board.grid[r][c]);
                }
                writer.println();
            }
            writer.println("----------------------------------------------------------");
            writer.println("SOLUTION STEPS:");
            writer.println("----------------------------------------------------------");
            
            for (int i = 1; i < solution.size(); i++) {
                state = solution.get(i);
                writer.println("Move " + i + ": " + state.move);
            }
            writer.println("\nSolution finished. Game solved successfully!");
        }
        
        return outputPath;
    }

}