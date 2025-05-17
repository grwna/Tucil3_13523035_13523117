import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import algorithm.AStar;
import algorithm.BlockingCarsHeuristic;
import algorithm.GreedyBestFirst;
import algorithm.Heuristic; // Added this import to fix the error
import algorithm.ManhattanToExitHeuristic;
import algorithm.Pathfinder;
import algorithm.UCS;
import model.Board;
import model.State;
import parser.InputParser;
import ui.GUI;
import utils.Printer;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            // Mode GUI
            GUI.main(args);
        } else {
            // Mode Command Line
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan path file input: ");
            String inputPath = scanner.nextLine();
            
            try {
                Board initialBoard = InputParser.parseFromFile(inputPath);
                System.out.println("Berhasil membaca file:");
                Printer.print(initialBoard);
                
                // Pilih mode eksekusi
                System.out.println("\nPilih mode eksekusi:");
                System.out.println("1. Jalankan semua algoritma secara otomatis");
                System.out.println("2. Pilih algoritma manual");
                System.out.println("3. Bandingkan semua algoritma");
                System.out.print("Pilihan [1/2/3]: ");
                String modeChoice = scanner.nextLine().trim();
                
                switch(modeChoice) {
                    case "1":
                        // Mode otomatis - jalankan semua algoritma seperti di Main asli
                        runAllAlgorithmsAutomatic(initialBoard);
                        break;
                    case "2":
                        // Mode pilih manual
                        runManualAlgorithm(scanner, initialBoard);
                        break;
                    case "3":
                    default:
                        // Mode bandingkan semua algoritma
                        runAllAlgorithmsComparison(initialBoard);
                        break;
                }
                
            } catch (IOException e) {
                System.out.println("Gagal membaca file: " + e.getMessage());
            } finally {
                scanner.close();
            }
        }
    }

    private static void runAllAlgorithmsAutomatic(Board initialBoard) {
        System.out.println("====================================");
        System.out.println("Menjalankan algoritma UCS...");
        System.out.println("====================================");
        runAlgorithm(new UCS(), initialBoard);
        
        System.out.println("\n====================================");
        System.out.println("Menjalankan algoritma GBFS dengan BlockingCarsHeuristic...");
        System.out.println("====================================");
        runAlgorithm(new GreedyBestFirst(new BlockingCarsHeuristic()), initialBoard);
        
        System.out.println("\n====================================");
        System.out.println("Menjalankan algoritma GBFS dengan ManhattanToExitHeuristic...");
        System.out.println("====================================");
        runAlgorithm(new GreedyBestFirst(new ManhattanToExitHeuristic()), initialBoard);
        
        System.out.println("\n====================================");
        System.out.println("Menjalankan algoritma A* dengan BlockingCarsHeuristic...");
        System.out.println("====================================");
        runAlgorithm(new AStar(new BlockingCarsHeuristic()), initialBoard);
        
        System.out.println("\n====================================");
        System.out.println("Menjalankan algoritma A* dengan ManhattanToExitHeuristic...");
        System.out.println("====================================");
        runAlgorithm(new AStar(new ManhattanToExitHeuristic()), initialBoard);
    }
    
    private static void runManualAlgorithm(Scanner scanner, Board initialBoard) {
        // Pilih algoritma
        System.out.println("\nPilih algoritma:");
        System.out.println("1. Uniform Cost Search (UCS)");
        System.out.println("2. Greedy Best First Search (GBFS)");
        System.out.println("3. A* Search");
        System.out.print("Pilihan: ");
        int algoChoice;
        try {
            algoChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid, menggunakan default (1)");
            algoChoice = 1;
        }

        Pathfinder solver;
        
        if (algoChoice == 1) {
            solver = new UCS();
        } else {
            // Pilih heuristic untuk GBFS/A*
            System.out.println("\nPilih heuristic:");
            System.out.println("1. Blocking Cars Heuristic");
            System.out.println("2. Manhattan To Exit Heuristic");
            System.out.print("Pilihan: ");
            int heurChoice;
            try {
                heurChoice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid, menggunakan default (1)");
                heurChoice = 1;
            }

            Heuristic heuristic = heurChoice == 1 ? 
                new BlockingCarsHeuristic() : 
                new ManhattanToExitHeuristic();

            solver = algoChoice == 2 ? 
                new GreedyBestFirst(heuristic) : 
                new AStar(heuristic);
        }

        // Jalankan algoritma
        System.out.println("\n====================================");
        System.out.println("Menjalankan algoritma " + solver.getName() + "...");
        System.out.println("====================================");
        runSingleAlgorithm(solver, initialBoard);
    }

    private static void runAllAlgorithmsComparison(Board initialBoard) {
        System.out.println("\n====================================");
        System.out.println("MEMBANDINGKAN SEMUA ALGORITMA");
        System.out.println("====================================\n");

        Pathfinder[] algorithms = {
            new UCS(),
            new GreedyBestFirst(new BlockingCarsHeuristic()),
            new GreedyBestFirst(new ManhattanToExitHeuristic()),
            new AStar(new BlockingCarsHeuristic()),
            new AStar(new ManhattanToExitHeuristic())
        };

        for (Pathfinder algo : algorithms) {
            System.out.println("\nALGORITMA: " + algo.getName().toUpperCase());
            System.out.println("------------------------------------");
            runSingleAlgorithm(algo, initialBoard);
            System.out.println("------------------------------------");
        }
    }
    
    private static void runAlgorithm(Pathfinder solver, Board initialBoard) {
        long startTime = System.nanoTime();
        List<State> solution = solver.solve(initialBoard);
        long endTime = System.nanoTime();
        
        if (solution.isEmpty()) {
            System.out.println("\n❌ Tidak ditemukan solusi dengan algoritma " + solver.getName());
        } else {
            System.out.println("\n✅ Solusi ditemukan dengan algoritma " + solver.getName() + "!");
            System.out.println("Jumlah langkah: " + (solution.size() - 1));
            
            // Tampilkan beberapa langkah awal dan akhir saja untuk mempersingkat output
            int stepsToShow = Math.min(solution.size(), 5);
            
            for (int i = 0; i < stepsToShow; i++) {
                State state = solution.get(i);
                System.out.println("Langkah " + i + ": " + state.move);
                if (i < 3) {  // Hanya cetak board untuk langkah awal
                    Printer.print(state.board);
                }
            }
            
            // Tampilkan langkah-langkah akhir jika solusi panjang
            if (solution.size() > 5) {
                System.out.println("...");
                for (int i = solution.size() - 3; i < solution.size(); i++) {
                    State state = solution.get(i);
                    System.out.println("Langkah " + i + ": " + state.move);
                    Printer.print(state.board);
                }
            }
            
            double durationMs = (endTime - startTime) / 1e6;
            System.out.printf("Waktu eksekusi: %.3f ms\n", durationMs);
        }
    }
    
    private static void runSingleAlgorithm(Pathfinder solver, Board initialBoard) {
        long startTime = System.nanoTime();
        List<State> solution = solver.solve(initialBoard);
        long endTime = System.nanoTime();

        if (solution.isEmpty()) {
            System.out.println("\n❌ Tidak ditemukan solusi");
        } else {
            System.out.println("\n✅ Solusi ditemukan!");
            System.out.println("Jumlah langkah: " + (solution.size() - 1));
            
            // Tampilkan 3 langkah pertama
            for (int i = 0; i < Math.min(3, solution.size()); i++) {
                State state = solution.get(i);
                System.out.println("Langkah " + i + ": " + state.move);
                Printer.print(state.board);
            }
            
            // Tampilkan langkah terakhir jika lebih dari 3 langkah
            if (solution.size() > 3) {
                System.out.println("...");
                State lastState = solution.get(solution.size() - 1);
                System.out.println("Langkah " + (solution.size() - 1) + ": " + lastState.move);
                Printer.print(lastState.board);
            }

            double durationMs = (endTime - startTime) / 1e6;
            System.out.printf("Waktu eksekusi: %.3f ms\n", durationMs);
        }
    }
}