package ru.mephi.interpreter.robot;

/**
 * @author Anton Chkadua
 */
public class MazeCell {
    private int x;
    private int y;

    MazeCell(int x, int y){
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof MazeCell))
            return false;
        MazeCell point = (MazeCell) obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) + Integer.hashCode(y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
