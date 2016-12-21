package ru.mephi.interpreter.robot;

import java.util.*;

/**
 * @author Anton Chkadua
 */
class Maze {
    private final int width, height;
    private Map<MazeCell, List<MazeCell>> walls = new HashMap<>();
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
        List<MazeCell> wallsFromFirstCell = walls.getOrDefault(point1, new ArrayList<>());
        wallsFromFirstCell.add(point2);
        walls.put(point1, wallsFromFirstCell);
        List<MazeCell> wallsFromSecondCell = walls.getOrDefault(point2, new ArrayList<>());
        wallsFromSecondCell.add(point1);
        walls.put(point2, wallsFromSecondCell);
    }

    boolean canMove(int x1, int y1, int x2, int y2) {
        boolean hasWall = walls.getOrDefault(new MazeCell(x1, y1), new ArrayList<>()).stream().anyMatch(
                e -> e.getX() == x2 && e.getY() == y2);
        if (x2 >= 0 && x2 <= width - 1 && y2 >= 0 && y2 <= height - 1)
            hasWall = hasWall || walls.getOrDefault(new MazeCell(x2, y2), new ArrayList<>()).stream().anyMatch(
                    e -> e.getX() == x1 && e.getY() == y1);

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
        return !hasWall && !hitsTheWidthBorder && !hitsTheHeightBorder;
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
