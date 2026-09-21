/*
You are given a map of a server center, represented as a m * n integer matrix grid, where 1 means that on that cell there is a server and 0 means that it is no server. 
Two servers are said to communicate if they are on the same row or on the same column.
Return the number of servers that communicate with any other server.

Example 1:
Input: grid = [
    [1,1,0,0],
    [0,0,1,0],
    [0,0,1,0],
    [0,0,0,1]
]
Output: 4

Explanation: Except for the server at the position (3,3), all other servers have other servers to communicate.
*/

//Approach 1: Row/Column Counting
//Complexity: Time O(m·n), Space O(m+n)
class Solution {
    public int countServers(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] rowCount = new int[m];
        int[] colCount = new int[n];

        // Pass 1: count servers per row and per column
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    rowCount[i]++;
                    colCount[j]++;
                }
            }
        }

        // Pass 2: a server communicates if its row or column has more than 1 server
        int result = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1 && (rowCount[i] > 1 || colCount[j] > 1)) {
                    result++;
                }
            }
        }
        return result;
    }
}

//Approach 2: BFS on Connected Components (direct graph model)
//Each server is a node, and servers in the same row or column are connected. If a component has more than 1 node, every node in it has a neighbor.
//Complexity: Time O(m·n·(m+n)) worst case, since each server scans its row and column. Space O(m·n).
import java.util.*;

class Solution {
    public int countServers(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        boolean[][] visited = new boolean[m][n];
        int result = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1 && !visited[i][j]) {
                    int size = bfs(grid, visited, i, j);
                    if (size > 1) result += size;
                }
            }
        }
        return result;
    }

    private int bfs(int[][] grid, boolean[][] visited, int r, int c) {
        int m = grid.length, n = grid[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{r, c});
        visited[r][c] = true;
        int size = 0;

        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            size++;

            // neighbors in the same row
            for (int j = 0; j < n; j++) {
                if (grid[cur[0]][j] == 1 && !visited[cur[0]][j]) {
                    visited[cur[0]][j] = true;
                    queue.offer(new int[]{cur[0], j});
                }
            }
            // neighbors in the same column
            for (int i = 0; i < m; i++) {
                if (grid[i][cur[1]] == 1 && !visited[i][cur[1]]) {
                    visited[i][cur[1]] = true;
                    queue.offer(new int[]{i, cur[1]});
                }
            }
        }
        return size;
    }
}


//Approach 3: Union-Find (rows/columns as nodes)
//Complexity: Time O(m·n·α(m+n)) ≈ O(m·n), Space O(m+n).
class Solution {
    private int[] parent;

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }

    private void union(int a, int b) {
        parent[find(a)] = find(b);
    }

    public int countServers(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        parent = new int[m + n];             // 0..m-1 = rows, m..m+n-1 = columns
        for (int i = 0; i < m + n; i++) parent[i] = i;

        // server (i,j) = an edge connecting row i and column j
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (grid[i][j] == 1) union(i, m + j);

        // count servers (edges) in each component
        int[] cnt = new int[m + n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (grid[i][j] == 1) cnt[find(i)]++;

        int result = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (grid[i][j] == 1 && cnt[find(i)] > 1) result++;
        return result;
    }
}

/*
Trade-offs & When to Use
Approach	Pros	Cons
1. Counting	Short, fastest, easy to read	Doesn't practice real graph skills
2. BFS/DFS	Matches the graph view, transfers to other component problems	Slower, more code
3. Union-Find	Fast, good when you need dynamic merge/query	Harder modeling (server = edge)

When NOT to use Approaches 2 and 3: If the question is just "does it have a friend?" and you don't need to know who, building a graph is over-engineering.
In an interview: Start with Approach 1 since it's the best fit. Then mention that if the requirement changed to finding connected groups, you'd switch to Union-Find or BFS.

Real-World Scenario

Walking through Example 1 with Approach 1:
rowCount = [2, 1, 1, 1]
colCount = [1, 1, 2, 1]
(0,0), (0,1): row 0 has 2 servers 
(1,2), (2,2): column 2 has 2 servers 
(3,3): row 3 has 1, column 3 has 1 ❌

Total = 4, matching the expected output.

The same idea applies in real systems, for example finding isolated machines that have no peers in their network segment.

Before building a graph, ask whether the problem needs the connection structure or just a property of each node. If it's the latter, counting is usually enough and faster.
The trick of turning cells into edges between rows and columns comes up often in grid problems involving rows and columns, for example Most Stones Removed with Same Row or Column.

*/
