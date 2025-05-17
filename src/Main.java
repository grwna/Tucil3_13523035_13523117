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
import utils.Printer;

public class Main {
    public static void main(String[] args) {
        testGUI(args);
        // testParsing();
        // testProgram();   
    }

    public static void testGUI(String[] args){
        GUI.main(args);
    }

    public static void testParsing(){
        Scanner sc = new Scanner(System.in);
        Board initialBoard;

        // 1. Input file
        System.out.print("Masukkan path file input: ");
        String path = sc.nextLine();
        sc.close();
        try {
            initialBoard = InputParser.parseFromFile(path);
            System.out.println("Berhasil membaca file.");
            Printer.print(initialBoard);
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
        }
    }

    public static void testProgram(){
        Scanner sc = new Scanner(System.in);
        Board initialBoard = null;

        // 1. Input file
        System.out.print("Masukkan path file input: ");
        String path = sc.nextLine();
        try {
            initialBoard = InputParser.parseFromFile(path);
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
            return;
        }

        // 2. Pilih algoritma
        System.out.println("\nPilih algoritma:");
        System.out.println("1. Uniform Cost Search (UCS)");
        System.out.println("2. Greedy Best First Search (GBFS)");
        System.out.println("3. A* Search");
        System.out.print("Pilihan: ");
        int algoChoice = sc.nextInt();
        sc.nextLine(); // consume newline

        Pathfinder solver = null;
        Heuristic heuristic = null;

        if (algoChoice == 1) {
            solver = new UCS(); // kamu bisa buat UCS serupa A*
        } else {
            // 3. Pilih heuristic
            System.out.println("\nPilih heuristic:");
            System.out.println("1. Blocking Cars Heuristic");
            System.out.println("2. Manhattan To Exit Heuristic");
            System.out.print("Pilihan: ");
            int heurChoice = sc.nextInt();
            sc.nextLine(); // consume newline

            if (heurChoice == 1) {
                heuristic = new BlockingCarsHeuristic();
            } else {
                heuristic = new ManhattanToExitHeuristic();
            }

            if (algoChoice == 2) {
                solver = new GreedyBestFirst(heuristic); // bisa pakai base Pathfinder juga
            } else {
                solver = new AStar(heuristic);
            }
        }
        sc.close();
        // 4. Jalankan algoritma
        long startTime = System.nanoTime();
        List<State> solution = solver.solve(initialBoard);
        long endTime = System.nanoTime();

        // 5. Tampilkan hasil
        if (solution.isEmpty()) {
            System.out.println("\n❌ Tidak ditemukan solusi.");
        } else {
            System.out.println("\n✅ Solusi ditemukan!");
            for (int i = 0; i < solution.size(); i++) {
                State state = solution.get(i);
                System.out.println("Langkah " + i + ": " + state.move);
                Printer.print(state.board);
            }

            double durationMs = (endTime - startTime) / 1e6;
            System.out.println("Jumlah langkah: " + (solution.size() - 1));
            System.out.printf("Waktu eksekusi: %.3f ms\n", durationMs);
        }
    } 
}
