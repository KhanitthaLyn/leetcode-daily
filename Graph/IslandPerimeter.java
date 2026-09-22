/*
You are given a row x col grid representing a map where grid[i][j] = 1 represents land and grid[i][j] = 0 represents water.
Grid cells are connected horizontally/vertically (not diagonally). The grid is completely surrounded by water, and there is exactly one island (i.e., one or more connected land cells).

The island doesn't have "lakes", meaning the water inside isn't connected to the water around the island. One cell is a square with side length 1.
Return the perimeter of the island.

Example 1:
Input: grid = [
    [1,1,0,0],
    [1,0,0,0],
    [1,1,1,0],
    [0,0,1,1]
]

Output: 18
*/
/*
The naive idea "count all land cells × 4" overcounts, because it includes internal edges shared between two land cells.
The fix: for every pair of adjacent land cells, subtract 2 from the total (1 side from each cell), since that shared edge is not part of the perimeter. */

//Approach 1: Counting Exposed Edges (recommended, no graph needed)
//For each land cell, check its 4 neighbors. Every neighbor that is water or out of bounds adds 1 to the perimeter.
//Complexity: Time O(rows·cols), Space O(1)
class Solution {
    private boolean[][] visited;

    public int islandPerimeter(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    return dfs(grid, i, j); // only one island, so return immediately
                }
            }
        }
        return 0;
    }

    private int dfs(int[][] grid, int r, int c) {
        int rows = grid.length, cols = grid[0].length;

        // out of bounds or water counts as an exposed edge
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == 0) {
            return 1;
        }
        if (visited[r][c]) {
            return 0; // already counted, contributes nothing more
        }

        visited[r][c] = true;
        int perimeter = 0;
        perimeter += dfs(grid, r - 1, c);
        perimeter += dfs(grid, r + 1, c);
        perimeter += dfs(grid, r, c - 1);
        perimeter += dfs(grid, r, c + 1);
        return perimeter;
    }
}

//Approach 2: 4 × land cells − 2 × shared edges
//Count land cells and count adjacent land pairs (only check right and down to avoid double-counting), then compute directly.
//Complexity: Time O(rows·cols), Space O(1)
class Solution {
    public int islandPerimeter(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int landCount = 0;
        int sharedEdges = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    landCount++;
                    // check right neighbor
                    if (j + 1 < cols && grid[i][j + 1] == 1) sharedEdges++;
                    // check down neighbor
                    if (i + 1 < rows && grid[i + 1][j] == 1) sharedEdges++;
                }
            }
        }
        return landCount * 4 - sharedEdges * 2;
    }
}

//Approach 3: DFS Traversal (graph-style, useful if you need to also confirm connectivity)
//Since the problem guarantees exactly one island, DFS isn't strictly necessary, but this pattern is worth knowing for related problems (e.g. multiple islands, or verifying the island is a single connected region).
//Complexity: Time O(rows·cols), Space O(rows·cols) for the visited array and recursion stack
class Solution {
    private boolean[][] visited;

    public int islandPerimeter(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        visited = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    return dfs(grid, i, j); // only one island, so return immediately
                }
            }
        }
        return 0;
    }

    private int dfs(int[][] grid, int r, int c) {
        int rows = grid.length, cols = grid[0].length;

        // out of bounds or water counts as an exposed edge
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == 0) {
            return 1;
        }
        if (visited[r][c]) {
            return 0; // already counted, contributes nothing more
        }

        visited[r][c] = true;
        int perimeter = 0;
        perimeter += dfs(grid, r - 1, c);
        perimeter += dfs(grid, r + 1, c);
        perimeter += dfs(grid, r, c - 1);
        perimeter += dfs(grid, r, c + 1);
        return perimeter;
    }
}

/*
Approach	Pros	Cons
1. Exposed edges	Very clear, easy to explain, minimal state	Slightly more code than Approach 2
2. 4×land − 2×shared	Fewest lines, elegant math trick	Less intuitive to derive on the spot
3. DFS	Generalizes to multi-island or connectivity-check problems	Overkill here; extra space for visited array and recursion


When to use Approach 1 or 2: This problem specifically — no real connectivity question, just counting exposed sides. Either is fine; Approach 2 is slightly faster to write once you know the trick.
When to use Approach 3: If the problem evolves into "verify there's exactly one island" or "find the perimeter of the largest island among several," 
DFS/BFS becomes necessary since you now need to track which cells belong to which component.


Real-World Scenario
Walking through Example 2, grid = [[1,0]], with Approach 1:

Cell (0,0) is land. Check its 4 sides:
up: out of bounds → exposed
down: out of bounds → exposed
left: out of bounds → exposed
right: (0,1) is water → exposed
All 4 sides exposed → perimeter = 4, matching the expected output.

In practice, this "count boundary edges by checking neighbors" pattern shows up in image processing (contour detection) and game grids (finding the outline of a region).

Perimeter/boundary problems on a grid are rarely about traversal — they're about counting exposed edges. 
Reach for DFS/BFS only when you also need to know which cells belong together (multiple regions, largest region, etc.).
The identity perimeter = 4 × cells − 2 × shared edges is a reusable trick anytime you're computing a boundary from a set of unit squares.
*/
