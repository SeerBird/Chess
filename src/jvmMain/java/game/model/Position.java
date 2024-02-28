package game.model;

public class Position {
    int x;
    int y;

    private Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass()== Position.class){
            if(((Position)obj).x==x){
                return ((Position) obj).y == y;
            }
        }
        return false;
    }

    public static Position pos(int x, int y) {
        return new Position(x, y);
    }
}
