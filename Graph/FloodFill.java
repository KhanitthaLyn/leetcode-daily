/*

You are given an image represented by an m x n grid of integers image, where image[i][j] represents the pixel value of the image. You are also given three integers sr, sc, and color. Your task is to perform a flood fill on the image starting from the pixel image[sr][sc].
To perform a flood fill:

    Begin with the starting pixel and change its color to color.
    Perform the same process for each pixel that is directly adjacent (pixels that share a side with the original pixel, either horizontally or vertically) and shares the same color as the starting pixel.
    Keep repeating this process by checking neighboring pixels of the updated pixels and modifying their color if it matches the original color of the starting pixel.
    The process stops when there are no more adjacent pixels of the original color to update.

Return the modified image after performing the flood fill.

Example 1:
Input: image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, color = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]

Explanation:
From the center of the image with position (sr, sc) = (1, 1) (the red pixel), all pixels connected by a path of the same color as the starting pixel (the blue pixels) are colored with the new color.
Note the bottom corner is not colored 2, because it is not horizontally or vertically connected to the starting pixel.

Example 2:
Input: image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, color = 0
Output: [[0,0,0],[0,0,0]]
Explanation: The starting pixel is already colored with 0, which is the same as the target color. Therefore, no changes are made to the image.

Constraints:
    m == grid.length
    n == grid[i].length
    1 <= m, n <= 50
    0 <= image[i][j], color < (2^16)
    0 <= sr < m
    0 <= sc < n
*/

//Approach 1: DFS Recursive (most common, clean)
//Complexity: Time O(m·n), Space O(m·n) for the recursion stack in the worst case (e.g. entire image is one color)
class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int startColor = image[sr][sc];
        if (startColor == color) return image; // avoid infinite loop / no-op case
        dfs(image, sr, sc, startColor, color);
        return image;
    }

    private void dfs(int[][] image, int r, int c, int startColor, int newColor) {
        int rows = image.length, cols = image[0].length;
        if (r < 0 || r >= rows || c < 0 || c >= cols || image[r][c] != startColor) {
            return;
        }

        image[r][c] = newColor;
        dfs(image, r + 1, c, startColor, newColor);
        dfs(image, r - 1, c, startColor, newColor);
        dfs(image, r, c + 1, startColor, newColor);
        dfs(image, r, c - 1, startColor, newColor);
    }
}
//Approach 2: BFS Iterative (avoids recursion stack depth issues)
//Complexity: Time O(m·n), Space O(m·n) for the queue in the worst case
import java.util.*;

class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int startColor = image[sr][sc];
        if (startColor == color) return image;

        int rows = image.length, cols = image[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{sr, sc});
        image[sr][sc] = color;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : dirs) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && image[nr][nc] == startColor) {
                    image[nr][nc] = color;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
        return image;
    }
}
//Approach 3: DFS Iterative with Explicit Stack (avoids call-stack recursion limits)
//Complexity: Time O(m·n), Space O(m·n) for the stack in the worst case.
import java.util.*;

class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int startColor = image[sr][sc];
        if (startColor == color) return image;

        int rows = image.length, cols = image[0].length;
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{sr, sc});
        image[sr][sc] = color;

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!stack.isEmpty()) {
            int[] cur = stack.pop();
            for (int[] d : dirs) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && image[nr][nc] == startColor) {
                    image[nr][nc] = color;
                    stack.push(new int[]{nr, nc});
                }
            }
        }
        return image;
    }
}

/*
We need to find and repaint an entire connected region that shares the same starting color — 
this is a textbook connected component problem on a grid, where each pixel is a node and adjacent same-colored pixels are edges.
The key trap: if color equals the starting pixel's color, do nothing. Otherwise, a naive DFS would loop forever (paint to color X, 
then check neighbor with color X, which was just repainted, and treat it as "still needs painting").

Approach	Pros	Cons
1. DFS Recursive	Shortest, most readable, standard interview answer	Risk of StackOverflowError on huge grids (not an issue here, m,n ≤ 50)
2. BFS Iterative	No recursion depth risk, fills in a natural "wave" pattern	Slightly more boilerplate (queue + direction array)
3. DFS Iterative (stack)	Same recursion-safety as BFS, keeps DFS-style traversal order	Same boilerplate cost as BFS, less intuitive than plain recursion
When to use Approach 1: Default choice for interviews — grid is small (≤ 50×50 here), and recursion depth is not a real risk.
When to use Approach 2 or 3: If the grid could be very large (e.g. real image editor with millions of pixels), recursion could blow the stack. Iterative BFS/DFS avoids that entirely.
Critical trade-off regardless of approach: always check startColor == color first — skip this and you risk infinite recursion/looping.

Real-World Scenario
Walking through Example 1 with Approach 1, sr=1, sc=1, color=2:
startColor = image[1][1] = 1, and color = 2, so they differ — proceed.
DFS from (1,1): paint to 2, then check up/down/left/right.
(0,1)=1 → paint to 2 → spreads to (0,0)=1 and (0,2)=1
(2,1)=0 → skip (different color)
(1,2)=0 → skip
(1,0)=1 → paint to 2
(2,2)=1 never gets visited because it only touches (1,2)=0 and (2,1)=0, neither of which connects back to the starting region.

Result: [[2,2,2],[2,2,0],[2,0,1]] — matches expected output. This exact algorithm powers the "bucket fill" tool in any raster image editor.

Flood fill is DFS/BFS with one extra guard clause: always check if the new value equals the old value before starting traversal, or you'll loop forever repainting already-painted cells.
This pattern — "grid graph, spread while a condition holds" — is the foundation for Number of Islands, Rotting Oranges, and Walls and Gates. Master this one and the rest are variations on the same skeleton.
*/
