/*
You are keeping the scores for a baseball game with strange rules. At the beginning of the game, you start with an empty record.

Given a list of strings operations, where operations[i] is the ith operation you must apply to the record and is one of the following:

    An integer x: Record a new score of x.

    '+': Record a new score that is the sum of the previous two scores.

    'D': Record a new score that is the double of the previous score.

    'C': Invalidate the previous score, removing it from the record.

Return the sum of all the scores on the record after applying all the operations.

Note: The test cases are generated such that the answer and all intermediate calculations fit in a 32-bit integer and that all operations are valid.

*/

class Solution {
    public int calPoints(String[] operations) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String op : operations) {
            switch (op) {
                case "+" -> {
                    int top = stack.pop();
                    int second = stack.peek();
                    int sum = top + second;
                    stack.push(top);      // put the top one back, unchanged
                    stack.push(sum);      // then add the new score
                }
                case "D" -> stack.push(stack.peek() * 2);
                case "C" -> stack.pop();
                default -> stack.push(Integer.parseInt(op));
            }
        }

        int total = 0;
        for (int score : stack) {
            total += score;
        }
        return total;
    }
}

/*
The core challenge: '+' and 'D' both need to look back at recent history (the last 1–2 valid scores), and 'C' needs to delete the most recent entry. 
If you just kept a running sum variable and threw away individual scores, you'd have no way to:

Compute '+' or 'D' (you need actual previous values, not just their total).
Undo 'C' correctly (you'd need to know exactly what value to subtract back out).

So you need a structure that preserves order and gives fast access to "the most recent items." That's exactly what a stack is built for.

How It Works
Use a stack to represent the current valid record, in order:

Loop through each operation string:
If it's "+" → peek at the top two elements, push their sum.
If it's "D" → peek at the top element, push double of it.
If it's "C" → pop the top element (discard it).
Otherwise (it's a number) → parse it and push it.
At the end, sum everything left in the stack.

Note on "+": since Deque.peek() only looks at one element, to read the second-from-top you have to pop() the top temporarily, 
peek() the new top, then push() the original top back — restoring the stack before pushing the new sum.

When to use stack here:
Whenever operations reference "the last N items" and can also delete the most recent item — that combination (lookback + undo-last) is the classic stack signature.

When NOT to use a stack:
If deletions could happen anywhere in history (not just the top) — a stack only gives you fast access to the top, so removing an arbitrary past score would need a different structure (e.g., a doubly linked list).

Trade-offs:
Space: O(n) since in the worst case every operation pushes a value.
Alternative: use an ArrayList instead of Deque — works too (since you only ever touch the end), but a stack/deque communicates intent more clearly ("I only care about the top").


Mini Example
Tracing Example 1: ["1","2","+","C","5","D"]

op	action	stack (bottom→top)
"1"	push 1	[1]
"2"	push 2	[1,2]
"+"	push 1+2=3	[1,2,3]
"C"	pop (remove 3)	[1,2]
"5"	push 5	[1,2,5]
"D"	push 5*2=10	[1,2,5,10]

Sum = 1+2+5+10 = 18 matches expected output.

Real use case: this "append + look-back + undo-last" pattern shows up in things like calculator apps with an "undo last input" button, or version-history tools where you can only revert the most recent change.


When an operation needs to "see" or "undo" only the most recent item(s), reach for a stack — it's the natural fit for anything with LIFO (last-in-first-out) behavior.
Be careful with peek-order when an operation needs the 2nd-from-top element 
Deque only exposes the top directly, so you often need a temporary pop-peek-push dance to reach deeper without corrupting the stack's order.

*/

public class Solution {
    public int calPoints(String[] operations) {
        Stack<Integer> stack = new Stack<>();
        for (String op : operations) {
            if (op.equals("+")) {
                int top = stack.pop();
                int newTop = top + stack.peek();
                stack.push(top);
                stack.push(newTop);
            } else if (op.equals("D")) {
                stack.push(2 * stack.peek());
            } else if (op.equals("C")) {
                stack.pop();
            } else {
                stack.push(Integer.parseInt(op));
            }
        }
        int sum = 0;
        for (int score : stack) {
            sum += score;
        }
        return sum;
    }
}

/*
The only real difference is the underlying class:

Stack<Integer> — Java's legacy class from 1.0, extends Vector, and every method is synchronized (thread-safe but with unnecessary overhead in a single-threaded context like this).
Deque<Integer> (via ArrayDeque) — the modern recommended choice for stack behavior in Java. No synchronization overhead, generally faster, and it's what the official Java docs now recommend over Stack.

One small style note: iterating for (int score : stack) behaves the same in both — it's fine either way, just know that for Stack, iteration order goes bottom→top (insertion order), 
same as ArrayDeque pushed via push().

Bottom line: correctness, complexity, and design pattern are 100% identical. The only difference is Stack vs ArrayDeque as the implementation 
a minor "which class is idiomatic in modern Java" point, not a logic difference. If this came up in an interview, 
using Deque/ArrayDeque instead of Stack is a small detail that shows you're aware of current Java best practices, but either would be accepted as a correct solution.

*/
