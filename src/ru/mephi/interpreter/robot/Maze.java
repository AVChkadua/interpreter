package ru.mephi.interpreter.robot;

import java.util.*;

/**
 * @author Anton Chkadua
 */
class Maze {
    private final int width, height;
    private Map<MazeCell, MazeCell> walls = new HashMap<>();
    private Deque<MazeCell> teleports = new ArrayDeque<>();
    private Set<MazeCell> exits = new HashSet<>();

    private Maze(int width, int height) {
        this.width = width;
        this.height = height;
    }

    static Maze createMaze(int width, int height) {
        return new Maze(width, height);
    }

    void addWall(MazeCell point1, MazeCell point2) {
        walls.put(point1, point2);
    }

    boolean canMove(int x1, int y1, int x2, int y2) {
        boolean hasWall = walls.entrySet().stream().anyMatch(e ->
                (e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                        e.getValue().getX() == x2 && e.getValue().getY() == y2) ||
                        (e.getKey().getX() == x2 && e.getKey().getY() == y2 &&
                                e.getValue().getX() == x1 && e.getValue().getY() == y1));

        boolean hitsTheWidthBorder = false;
        if (x1 < x2) {
            hitsTheWidthBorder = x1 == width - 1;
        } else if (x1 > x2) {
            hitsTheWidthBorder = x1 == 0;
        }

        boolean hitsTheHeightBorder = false;
        if (y1 < y2) {
            hitsTheHeightBorder = y1 == height - 1;
        } else if (y1 > y2) {
            hitsTheHeightBorder = y1 == 0;
        }
        return !hasWall && hitsTheWidthBorder && hitsTheHeightBorder;
    }

    void addExit(MazeCell exit) {
        exits.add(exit);
    }

    void createTeleport(MazeCell cellWithTeleport) {
        if (!teleports.contains(cellWithTeleport))
            teleports.addFirst(cellWithTeleport);
    }

    MazeCell teleport() {
        return teleports.pollFirst();
    }

    Set<MazeCell> getExits() {
        return exits;
    }
}
