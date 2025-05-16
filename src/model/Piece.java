package model;

public class Piece {
    public char id;
    public Position start;
    public int length;
    public boolean isHorizontal;

    public Piece(char id, Position start, int length, boolean isHorizontal) {
        this.id = id;
        this.start = start;
        this.length = length;
        this.isHorizontal = isHorizontal;
    }

    public Piece copy() {
        return new Piece(id, start.copy(), length, isHorizontal);
    }
}
