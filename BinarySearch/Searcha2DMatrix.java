/*
You are given an m x n 2-D integer array matrix and an integer target.

    Each row in matrix is sorted in non-decreasing order.
    The first integer of every row is greater than the last integer of the previous row.

Return true if target exists within matrix or false otherwise.

Can you write a solution that runs in O(log(m * n)) time?
*/

class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;
        int low = 0, high = m * n - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int row = mid / n;
            int col = mid % n;
            int value = matrix[row][col];

            if (value == target) {
                return true;
            } else if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return false;
    }
}
//Time: O(log(m*n)), Space: O(1)

public class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int ROWS = matrix.length, COLS = matrix[0].length;

        int l = 0, r = ROWS * COLS - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            int row = m / COLS, col = m % COLS;
            if (target > matrix[row][col]) {
                l = m + 1;
            } else if (target < matrix[row][col]) {
                r = m - 1;
            } else {
                return true;
            }
        }
        return false;
    }
}
/*
The problem: A naive approach would be "loop through every row, binary search each row" — that's O(m log n), not O(log(m·n)). Or worse, scan every cell — O(m·n). Neither meets the required complexity.

Why the naive way falls short: The requirement O(log(m·n)) is a strong hint — it equals O(log m + log n), but more importantly, 
it's the complexity of a single binary search over m·n elements treated as one array. So the real question is: 
can we treat this 2D grid as a 1D sorted array without physically flattening it (which would cost O(m·n) space)?

Key insight: Because "the first integer of every row is greater than the last integer of the previous row," the matrix is a fully sorted sequence if read row by row, 
left to right. We just need a way to map a single 1D index back to its (row, col) coordinates — no flattening required.

How It Works
Step 1 — Treat it as a virtual 1D array:
Total elements = m * n. Search space: low = 0, high = m*n - 1.

Step 2 — Convert a 1D index to 2D coordinates:
For any mid in the range [0, m*n - 1]:

row = mid / n
col = mid % n

This works because rows are laid end-to-end: index 0 to n-1 is row 0, n to 2n-1 is row 1, and so on.

Step 3 — Standard binary search:

low = 0, high = m * n - 1
while low <= high:
    mid = low + (high - low) / 2
    row = mid / n
    col = mid % n
    value = matrix[row][col]
    if value == target:
        return true
    else if value < target:
        low = mid + 1
    else:
        high = mid - 1
return false


When to use: Whenever a 2D structure has a global sorted ordering (not just sorted per-row or per-column independently) 
meaning you can flatten it conceptually into one sequence. This "index mapping" trick (row = idx / n, col = idx % n) is a reusable pattern for any row-major flattened search.
When NOT to use: This does not apply to the classic "Search a 2D Matrix II" variant, where rows are sorted left-to-right AND columns are sorted top-to-bottom, 
but rows are not globally increasing relative to each other (e.g., matrix[0][last] might be greater than matrix[1][0]). That variant needs a different approach (staircase search from top-right corner, 
O(m+n)) because there's no single global sorted order to binary search over.

Trade-offs: You get O(log(m·n)) time and O(1) space — no extra memory for flattening — at the cost of needing to correctly derive the index-mapping formula, which is an easy off-by-one trap if you're not careful.

Mini Example
Dry run with matrix = [[1,2,4,8],[10,11,12,13],[14,20,30,40]], target = 13.
m=3, n=4, total = 12, low=0, high=11.

low	high	mid	row=mid/4	col=mid%4	value	action
0	11	5	1	1	11	11 < 13 → low=6
6	11	8	2	0	14	14 > 13 → high=7
6	7	6	1	2	12	12 < 13 → low=7
7	7	7	1	3	13	found → return true 

Whenever a 2D structure has a genuinely global sorted order, don't overthink it as "2D" — flatten it mentally using row = idx / n, col = idx % n, 
and reuse plain 1D binary search. The hard part isn't the search algorithm itself (it's always the same template) — it's recognizing when a 2D problem is secretly a 1D problem in disguise.
*/

