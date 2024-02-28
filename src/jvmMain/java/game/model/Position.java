package game.model;

import java.util.Objects;

public class Position {
    public int x;
    public int y;

    private Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == Position.class) {
            if (((Position) obj).x == x) {
                return ((Position) obj).y == y;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static Position pos(int x, int y) {
        return new Position(x, y);
    }
}
