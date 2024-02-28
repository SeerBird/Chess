package game.model;

public class Position {
    int x;
    int y;

    private Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position pos(int x, int y) {
        return new Position(x, y);
    }
}
