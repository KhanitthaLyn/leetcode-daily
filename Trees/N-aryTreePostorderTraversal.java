/*
You are given the root of an n-ary tree, return the postorder traversal of its nodes' values.
Nary-Tree input serialization is represented in their level order traversal. Each group of children is separated by the null value (See examples)

Example 1:
Input: root = [1,null,3,2,4,null,5,6]
Output: [5,6,3,2,4,1]

Example 2:
Input: root = [1,null,2,3,4,5,null,null,6,7,null,8,null,9,10,null,null,11,null,12,null,13,null,null,14]
Output: [2,6,14,11,7,3,12,8,4,13,9,10,5,1]

Constraints:
    0 <= number of nodes in the tree <= 10,000
    0 <= Node.val <= 10,000
    The height of the n-ary tree is less than or equal to 1000.
*/

//Approach A — Recursive DFS (cleanest, easiest to reason about):
//Base case: if node is null, return
//Recurse into every child, in order
//After all children are processed, add the current node's value
class Solution {
    public List<Integer> postorder(Node root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    private void dfs(Node node, List<Integer> result) {
        if (node == null) return;
        for (Node child : node.children) {
            dfs(child, result);
        }
        result.add(node.val);
    }
}

//Approach B — Iterative with one stack, reversed "root-last, children-first-pushed" trick:
//This mimics preorder but pushes children left-to-right so they pop right-to-left, producing root, then children reversed — then you reverse the whole result at the end.
class Solution {
    public List<Integer> postorder(Node root) {
        LinkedList<Integer> result = new LinkedList<>();
        if (root == null) return result;

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            result.addFirst(node.val); // insert at front instead of reversing later

            for (Node child : node.children) {
                stack.push(child); // push left-to-right, so they pop right-to-left
            }
        }
        return result;
    }
}
//Approach C — Iterative with explicit child-index tracking (true postorder simulation, no trick):
//Push nodes with a pointer to "which child to visit next." Only add the node's value once all its children have been fully processed.

class Solution {
    public List<Integer> postorder(Node root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Deque<Node> stack = new ArrayDeque<>();
        Deque<Integer> childIndex = new ArrayDeque<>();
        stack.push(root);
        childIndex.push(0);

        while (!stack.isEmpty()) {
            Node node = stack.peek();
            int idx = childIndex.peek();

            if (idx < node.children.size()) {
                childIndex.pop();
                childIndex.push(idx + 1);
                stack.push(node.children.get(idx));
                childIndex.push(0);
            } else {
                result.add(node.val);
                stack.pop();
                childIndex.pop();
            }
        }
        return result;
    }
}

/*
Binary tree postorder is simple: left → right → root. But here a node can have any number of children, not just two. So the challenge is: 
how do you visit an arbitrary-sized list of children (in order), then the node itself, without hardcoding "left" and "right"?

The core problem is really about generalizing traversal from a fixed-arity structure (binary) to a variable-arity structure (n-ary), 
and also about avoiding the naive "reverse a preorder" trick's readability sacrifice, if you want it to actually read as a genuine postorder.

Approach	Pros	Cons
A. Recursive	Shortest, most readable, matches the definition directly	Risk of stack overflow — this problem allows height up to 1000, 
which is right at the edge of Java's default recursion limit (~roughly a few thousand, depends on JVM stack size)
B. Iterative (reverse trick)	Simple to write, avoids recursion depth issue, O(n)	Feels like a "trick" rather than genuine postorder logic — 
less intuitive if you need to explain your reasoning in an interview
C. Iterative (explicit index)	True postorder logic, no recursion, handles deep trees safely, demonstrates real understanding of the traversal mechanics	More verbose, 
slightly more error-prone to write under time pressure

When to use A: quick solution, tree not extremely deep, or you're optimizing for clarity/interview speed
When to use B: you want an iterative solution fast and don't mind the "reverse" trick — common accepted approach on LeetCode
When to use C: you specifically want to demonstrate you understand why postorder is harder iteratively (this is often what interviewers are probing for), 
or the tree height constraint makes you worried about recursion (with height ≤ 1000, Approach A is usually still fine in practice on LeetCode's default stack size, but not guaranteed in all environments)

Mini Example
Postorder shows up whenever you need to finish all dependents before handling the thing that depends on them:

Deleting a file system tree — you must delete all files inside a folder before deleting the folder itself
Compiling dependency graphs (build systems) — compile all a module's dependencies before compiling the module
Evaluating expression trees — compute all operands before applying the operator at the parent node

"Postorder means 'children finish, then parent.' If recursion feels natural, start there — but if you're asked to do it iteratively, 
don't just recurse-in-disguise; understand that you need to track 'have I finished all this node's children yet?' explicitly." That's the real skill being tested, not just memorizing the traversal order.

*/
