/* There is a file system that keeps a log each time some user performs a change folder operation.

The operations are described below:

    "../" : Move to the parent folder of the current folder. (If you are already in the main folder, remain in the same folder).
    "./" : Remain in the same folder.
    "x/" : Move to the child folder named x (This folder is guaranteed to always exist).

You are given a list of strings logs where logs[i] is the operation performed by the user at the ith step.

The file system starts in the main folder, then the operations in logs are performed.

Return the minimum number of operations needed to go back to the main folder after the change folder operations.*/


class Solution {
    public int minOperations(String[] logs) {
        int depth = 0; // acts like the stack's size
        for (String log : logs) {
            if (log.equals("../")) {
                if (depth > 0) depth--; // pop only if there's something to pop
            } else if (log.equals("./")) {
                // no-op
            } else {
                depth++; // push
            }
        }
        return depth;
    }
}

/*
Full stack-based approach:

Create an empty stack representing "depth."
Loop through each log:
"../" → if the stack isn't empty, pop.
"./" → do nothing.
a folder name ("x/") → push it.
At the end, the stack's size is the answer.

Key insight: we don't actually need to store meaningful values (like folder names) — we only care about count, not content. So in practice, 
we can replace the stack with a plain int depth counter, which gives identical results but is faster and uses O(1) memory.

If you actually needed the current folder name too (a variant of this problem), you'd use a real Deque<String> instead — same structure, just push/pop real folder names instead of depth++/depth--.

When to use this stack/counter pattern:
Problems with a nested "enter/exit" structure — balanced parentheses, path simplification, undo/redo history.
When NOT to use a full stack:
When you don't need the "content" of each layer (like this problem) — a counter alone is enough; a full stack just wastes memory.

Trade-offs:
Real Deque<String>: O(n) memory, but flexible (you know the current folder name, full path history).
Plain int counter: O(1) memory, faster, but only answers "how deep."

This problem only asks "how many times to go back," so the counter is the better fit.

Mini Example
Tracing Example 3: ["d1/","d2/","../","d3/","../","d4/","../","d5/"]

log	action	depth
d1/	push	1
d2/	push	2
../	pop	1
d3/	push	2
../	pop	1
d4/	push	2
../	pop	1
d5/	push	2

Final depth = 2 → matches the expected output 

Real-world parallels: breadcrumb navigation in web apps, undo history in a text editor, or even a browser's back button — all the same underlying idea (tracking depth/history of nested state).

Whenever you see "enter/exit" or "nested," think stack first — but then ask yourself: "Do I actually need the content of each layer, or just the count?" 
If it's just the count, collapse it into a counter — saves both time and memory.
Always guard against "popping when empty" — check depth > 0 (or !stack.isEmpty()) before popping, or you'll get an invalid negative value or an error that shouldn't happen.
*/

public class Solution {
    public int minOperations(String[] logs) {
        Stack<String> stack = new Stack<>();
        for (String log : logs) {
            if (log.equals("../")) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (!log.equals("./")) {
                stack.push(log);
            }
        }
        return stack.size();
    }
}


/*
Actually pushes the folder name string each time (e.g., "d1/", "d2/").
Uses O(n) extra memory in the worst case (all push operations, no ../).
Stack<String> in Java is a legacy class (synchronized, extends Vector) — works fine here, but in modern Java you'd more often use Deque<String> 
(e.g., ArrayDeque) for a stack, since Stack carries unnecessary synchronization overhead.*/
