/*
You are given the root of a binary tree `root`. Invert the binary tree and return its root.
Example 1:
Input: root = [1,2,3,4,5,6,7]
Output: [1,3,2,7,6,5,4]
```
Example 2:
Input: root = [3,2,1]
Output: [3,1,2]
```
Constraints:

* `0 <= The number of nodes in the tree <= 100`.
* `-100 <= Node.val <= 100`
*/

class Solution {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;

        // Swap left and right children
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        // Recurse into the (now swapped) subtrees
        invertTree(root.left);
        invertTree(root.right);

        return root;
    }
}

/*
A plain loop (for/while) doesn't work cleanly here because trees don't have a fixed size and branches aren't uniform. So you need recursion (or an explicit stack/queue) to walk the structure.

How It Works
Recursive DFS approach:

If the node is null → nothing to invert, return null (base case)
Swap the current node's left and right children
Recursively invert the left subtree and the right subtree (now in their swapped positions)
Return the current node (same root reference, but everything inside is now flipped)

Walkthrough with [1,2,3,4,5,6,7]:

At root 1: swap 2 and 3 → left becomes 3, right becomes 2
Go to 3 (originally the right side, with children 6,7): swap into 7,6
Go to 2 (originally the left side, with children 4,5): swap into 5,4
Result: [1,3,2,7,6,5,4]  matches the example

Alternative: iterative BFS using a Queue — pop a node, swap its children, push both children back onto the queue, repeat until the queue is empty. Same result, no recursion.


Recursive DFS: short, readable code, but uses call stack space — for a very deep (skewed) tree, this risks a stack overflow. With this problem's constraint (≤100 nodes), it's a non-issue.
Iterative (own Queue/Stack): safer against stack overflow since you control memory yourself, but slightly more code.
When to use recursive: when the tree isn't extremely deep, or you just want clean, direct code for a concept check.
When NOT to use recursive: in production systems handling very large, arbitrarily deep trees — prefer iterative to avoid stack overflow risk.

Mini Example
This "traverse and mutate a tree" pattern shows up in real work like:

File system trees — e.g., mirroring a folder structure's display order
UI component trees (like React's DOM) — mirroring layout for right-to-left languages (e.g., Arabic)
Comment trees / menu trees in web apps, where you need to walk deep and modify every node

"For any tree problem, think recursion first: find the base case (null), then trust that the recursive call will correctly handle the rest of the subtree ('trust the recursion')." 
You don't need to hold the whole tree in your head at once — just handle the single node in front of you, and let the function take care of the rest.

*/
