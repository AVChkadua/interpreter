package ru.mephi.interpreter.robot;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Anton Chkadua
 */
public class Robot {
    private static Robot instance;
    private final Maze maze;
    private int x, y;
    private Set<MazeCell> exits;
    private Set<MazeCell> visited = new HashSet<>();

    private Robot(int x, int y, Maze maze) {
        this.x = x;
        this.y = y;
        this.maze = maze;
        this.exits = maze.getExits();
    }

    static void createRobot(int x, int y, Maze maze) {
        instance = new Robot(x, y, maze);
    }

    public static Robot getInstance() {
        return instance;
    }

    public String top() {
        if (canMoveTop()) {
            y += 1;
            visited.add(new MazeCell(x, y));
            return "1";
        }
        return "0";
    }

    public String bottom() {
        if (canMoveBottom()) {
            y -= 1;
            visited.add(new MazeCell(x, y));
            return "1";
        }
        return "0";
    }

    public String left() {
        if (canMoveLeft()) {
            y -= 1;
            visited.add(new MazeCell(x, y));
            return "1";
        }
        return "0";
    }

    public String right() {
        if (canMoveRight()) {
            x += 1;
            visited.add(new MazeCell(x, y));
            return "1";
        }
        return "0";
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

    public boolean visitedTop() {
        return visited.contains(new MazeCell(x, y + 1)) && canMoveTop();
    }

    public boolean visitedBottom() {
        return visited.contains(new MazeCell(x, y - 1)) && canMoveBottom();
    }

    public boolean visitedLeft() {
        return visited.contains(new MazeCell(x - 1, y)) && canMoveLeft();
    }

    public boolean visitedRight() {
        return visited.contains(new MazeCell(x + 1, y)) && canMoveRight();
    }

    public void createTeleport() {
        maze.createTeleport(new MazeCell(x, y));
    }

    public void teleport() {
        MazeCell cellWithLastTeleport = maze.teleport();
        x = cellWithLastTeleport.getX();
        y = cellWithLastTeleport.getY();
    }


    public boolean checkIfAtExit() {
        return exits.contains(new MazeCell(x,y));
    }
}