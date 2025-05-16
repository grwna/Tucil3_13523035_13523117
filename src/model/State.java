package model;

public class State {
    public Board board;
    public String move;  // contoh: "A-kanan"
    public State prev;

    public State(Board board, String move, State prev) {
        this.board = board;
        this.move = move;
        this.prev = prev;
    }

    public int getDepth() {
        int count = 0;
        State curr = this;
        while (curr.prev != null) {
            count++;
            curr = curr.prev;
        }
        return count;
    }

    public void printPath() {
        if (prev != null) {
            prev.printPath();
        }
        if (!move.equals("Start")) {
            System.out.println("Gerakan: " + move);
        }
    }

    public void printBoards() {
        if (prev != null) prev.printBoards();
        System.out.println(move);
        for (int i = 0; i < board.rows; i++) {
            for (int j = 0; j < board.cols; j++) {
                System.out.print(board.grid[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
