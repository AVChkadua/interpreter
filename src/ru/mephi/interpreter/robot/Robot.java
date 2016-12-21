package ru.mephi.interpreter.robot;

/**
 * @author Anton Chkadua
 */
public class Robot {
    private static Robot instance;
    private int x, y;
    private final Maze maze;

    private Robot(int x, int y, Maze maze) {
        this.x = x;
        this.y = y;
        this.maze = maze;
    }

    static void createRobot(int x, int y, Maze maze){
        instance = new Robot(x, y, maze);
    }

    public static Robot getInstance() {
        return instance;
    }

    public boolean top() {
        if (canMoveTop()) {
            y += 1;
            return true;
        }
        return false;
    }

    public boolean bottop() {
        if (canMoveBottom()) {
            y += 1;
            return true;
        }
        return false;
    }

    public boolean left() {
        if (canMoveLeft()) {
            y += 1;
            return true;
        }
        return false;
    }

    public boolean right() {
        if (canMoveRight()) {
            y += 1;
            return true;
        }
        return false;
    }

    public boolean canMoveTop() {
        return maze.canMove(x, y, x, y + 1);
    }

    public boolean canMoveBottom() {
        return maze.canMove(x, y, x, y - 1);
    }

    public boolean canMoveLeft() {
        return maze.canMove(x, y, x - 1, y);
    }

    public boolean canMoveRight() {
        return maze.canMove(x, y, x + 1, y);
    }

    public void createTeleport() {
        maze.createTeleport(new MazeCell(x, y));
    }

    public void teleport() {
        MazeCell cellWithLastTeleport = maze.teleport();
        x = cellWithLastTeleport.getX();
        y = cellWithLastTeleport.getY();
    }
}