package ru.mephi.interpreter.robot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anton Chkadua
 */

public class MazeFactory {

    private static final String sizeRegex = "size (\\d) (\\d)";
    private static final String startRegex = "start (\\d) (\\d)";
    private static final String wallRegex = "(\\d) (\\d) (\\d) (\\d)";
    private static final String exitRegex = "exit (\\d) (\\d)";
    private static Maze maze;

    public static void importFile(String filename) throws IOException {
        Files.lines(Paths.get(filename)).forEach(line -> {
            if (line.matches(sizeRegex)) {
                Pattern pattern = Pattern.compile(sizeRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    Integer width = Integer.valueOf(matcher.group(1));
                    Integer height = Integer.valueOf(matcher.group(2));
                    maze = Maze.createMaze(width, height);
                }
            } else if (line.matches(startRegex)) {
                Pattern pattern = Pattern.compile(startRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    Integer x = Integer.valueOf(matcher.group(1));
                    Integer y = Integer.valueOf(matcher.group(2));
                    Robot.createRobot(x, y, maze);
                }
            } else if (line.matches(exitRegex)) {
                Pattern pattern = Pattern.compile(exitRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    Integer x = Integer.valueOf(matcher.group(2));
                    Integer y = Integer.valueOf(matcher.group(3));
                    maze.addExit(new MazeCell(x, y));
                }
            } else if (line.matches(wallRegex)) {
                Pattern pattern = Pattern.compile(wallRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    Integer x1 = Integer.valueOf(matcher.group(1));
                    Integer y1 = Integer.valueOf(matcher.group(2));
                    Integer x2 = Integer.valueOf(matcher.group(3));
                    Integer y2 = Integer.valueOf(matcher.group(4));
                    maze.addWall(new MazeCell(x1, y1), new MazeCell(x2, y2));
                }
            } else {
                throw new IllegalArgumentException();
            }
        });
    }
}
