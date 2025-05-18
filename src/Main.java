import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import algorithm.AStar;
import algorithm.BlockingCarsHeuristic;
import algorithm.GreedyBestFirst;
import algorithm.Heuristic;
import algorithm.ManhattanToExitHeuristic;
import algorithm.Pathfinder;
import algorithm.UCS;
import model.Board;
import model.State;
import parser.InputParser;
import ui.GUI;
import utils.OutputWriter;
import utils.Printer;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            // Mode GUI
            GUI.main(args);
        } else {
            // Mode Command Line
            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            
            while (running) {
                System.out.println("\n====================================");
                System.out.println("  PROGRAM SOLVER RUSH HOUR GAME");
                System.out.println("====================================");
                
                // Input file loop
                Board initialBoard = null;
                while (initialBoard == null) {
                    System.out.print("\nInsert path file input (or 'exit' to quit from program): ");
                    String inputPath = scanner.nextLine().trim();
                    
                    if (inputPath.equalsIgnoreCase("exit")) {
                        running = false;
                        break;
                    }
                    
                    try {
                        initialBoard = InputParser.parseFromFile(inputPath);
                        System.out.println("\n Successfully read the file");
                        Printer.print(initialBoard);
                    } catch (IOException e) {
                        System.out.println("Failed to read the file: " + e.getMessage());
                        System.out.println("Please try again!");
                    }
                }
                
                if (!running) break;
                
                // Main menu loop
                boolean solving = true;
                while (solving) {
                    System.out.println("\nMain Menu:");
                    System.out.println("1. Find the solution for the board");
                    System.out.println("2. Insert new file");
                    System.out.println("3. Exit from the program");
                    System.out.print("Choice [1/2/3]: ");
                    String mainChoice = scanner.nextLine().trim();
                    
                    switch (mainChoice) {
                        case "1":
                            runManualAlgorithm(scanner, initialBoard);
                            break;
                        case "2":
                            solving = false; // Will go back to file input
                            break;
                        case "3":
                            solving = false;
                            running = false;
                            break;
                        default:
                            System.out.println("invalid choice, please try again!");
                    }
                }
            }
            
            System.out.println("\nThank you for using this program!");
            scanner.close();
        }
    }
    
    private static void runManualAlgorithm(Scanner scanner, Board initialBoard) {
        // Pilih algoritma
        System.out.println("\nChoose the algorithm:");
        System.out.println("1. Uniform Cost Search (UCS)");
        System.out.println("2. Greedy Best First Search (GBFS)");
        System.out.println("3. A* Search");
        System.out.println("4. Back");
        System.out.print("Choice: ");
        int algoChoice;
        try {
            algoChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice!");
            return;
        }

        if (algoChoice == 4) return;

        Pathfinder solver;
        
        if (algoChoice == 1) {
            solver = new UCS();
        } else {
            // Pilih heuristic untuk GBFS/A*
            System.out.println("\nChoose the heuristic:");
            System.out.println("1. Blocking Cars Heuristic");
            System.out.println("2. Manhattan To Exit Heuristic");
            System.out.println("3. Back");
            System.out.print("Choice: ");
            int heurChoice;
            try {
                heurChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice!");
                heurChoice = 1;
            }

            if (heurChoice == 3) return;

            Heuristic heuristic = heurChoice == 1 ? 
                new BlockingCarsHeuristic() : 
                new ManhattanToExitHeuristic();

            solver = algoChoice == 2 ? 
                new GreedyBestFirst(heuristic) : 
                new AStar(heuristic);
        }

        // Jalankan algoritma
        System.out.println("\n====================================");
        System.out.println("Running " + solver.getName() + "Algorithm ...");
        System.out.println("====================================");
        runSingleAlgorithm(solver, initialBoard);
    }
    
    private static void runSingleAlgorithm(Pathfinder solver, Board initialBoard) {
        List<State> solution = solver.solve(initialBoard);

        if (solution.isEmpty()) {
            System.out.println("\nNo solution found!");
        } else {
            System.out.println("\n✅ Solution found!");
            System.out.println("Number of moves: " + (solution.size() - 1));
            
            // Tampilkan 3 langkah pertama
            for (int i = 0; i < Math.min(3, solution.size()); i++) {
                State state = solution.get(i);
                System.out.println("Move number " + i + ": " + state.move);
                Printer.print(state.board);
            }
            
            if (solution.size() > 3) {
                System.out.println("...");
                State lastState = solution.get(solution.size() - 1);
                System.out.println("Move number " + (solution.size() - 1) + ": " + lastState.move);
                Printer.print(lastState.board);
            }

            System.out.printf("Execution time: %.3f ms\n", solver.getRuntimeNano() /1e6);


            System.out.print("\nDo you want to save the complete solution to a file? (y/n): ");
            Scanner scanner = new Scanner(System.in);
            String saveChoice = scanner.nextLine().trim().toLowerCase();
            
            if (saveChoice.equals("y") || saveChoice.equals("yes")) {
                try {
                    String outputPath = OutputWriter.writeSolution(solution, solver.getName(), solver.getRuntimeNano()/1e6);
                    System.out.println("Solution saved successfully to: " + outputPath);
                } catch (IOException e) {
                    System.out.println("Failed to save solution: " + e.getMessage());
                }
            }
        }
    }
}