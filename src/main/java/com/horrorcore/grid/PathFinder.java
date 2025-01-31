package com.horrorcore.grid;

import java.util.*;

public class PathFinder {
    public static List<GridCell> findPath(Grid grid, GridCell start, GridCell end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<GridCell> closedSet = new HashSet<>();
        Map<GridCell, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start);
        startNode.g = 0;
        startNode.h = manhattan(start, end);
        startNode.f = startNode.h;

        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.cell.equals(end)) {
                return reconstructPath(current);
            }

            closedSet.add(current.cell);

            for (GridCell neighbor : getValidNeighbors(grid, current.cell)) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeG = current.g + 1;

                Node neighborNode = allNodes.get(neighbor);
                if (neighborNode == null) {
                    neighborNode = new Node(neighbor);
                    allNodes.put(neighbor, neighborNode);
                } else if (tentativeG >= neighborNode.g) {
                    continue;
                }

                neighborNode.parent = current;
                neighborNode.g = tentativeG;
                neighborNode.h = manhattan(neighbor, end);
                neighborNode.f = neighborNode.g + neighborNode.h;

                if (!openSet.contains(neighborNode)) {
                    openSet.add(neighborNode);
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private static class Node implements Comparable<Node> {
        GridCell cell;
        Node parent;
        double f, g, h;

        Node(GridCell cell) {
            this.cell = cell;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
    }

    private static List<GridCell> getValidNeighbors(Grid grid, GridCell cell) {
        List<GridCell> neighbors = new ArrayList<>();
        int[][] dirs = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        for (int[] dir : dirs) {
            int newX = cell.getX() + dir[0];
            int newY = cell.getY() + dir[1];

            if (newX >= 0 && newX < grid.getWidth() &&
                    newY >= 0 && newY < grid.getHeight()) {
                GridCell neighbor = grid.getCell(newX, newY);
                if (neighbor.getType() != GridCell.CellType.BLOCKED &&
                        neighbor.getBuilding() == null &&
                        neighbor.getOccupant() == null) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private static double manhattan(GridCell a, GridCell b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private static List<GridCell> reconstructPath(Node end) {
        List<GridCell> path = new ArrayList<>();
        Node current = end;

        while (current != null) {
            path.add(0, current.cell);
            current = current.parent;
        }

        return path;
    }
}