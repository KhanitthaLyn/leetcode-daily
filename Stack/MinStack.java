/* Design a stack class that supports the push, pop, top, and getMin operations.

    MinStack() initializes the stack object.
    void push(int val) pushes the element val onto the stack.
    void pop() removes the element on the top of the stack.
    int top() gets the top element of the stack.
    int getMin() retrieves the minimum element in the stack.

Each function should run in O(1)O(1) time.*/

class MinStack {
    private Deque<Integer> mainStack;
    private Deque<Integer> minStack;

    public MinStack() {
        mainStack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    public void push(int val) {
        mainStack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        } else {
            minStack.push(minStack.peek());
        }
    }

    public void pop() {
        mainStack.pop();
        minStack.pop();
    }

    public int top() {
        return mainStack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}

/*
A normal stack gives you O(1) push, pop, and top — but if you ask "what's the minimum in the stack?", the naive way is to scan the entire stack, which is O(n). 
That's fine for one call, but if getMin() is called thousands of times, this becomes very slow overall.

The problem: minimum can change as you push/pop, and we need it instantly, every time — so we need to somehow "remember" the min at every point in history without recomputing it.

How It Works
Approach: two stacks running in parallel.

mainStack — stores actual values, just like a normal stack.
minStack — at every position, stores "the minimum value up to this point."

Rules:
push(val): push val onto mainStack. Push min(val, minStack.peek()) onto minStack (or just val if minStack is empty).
pop(): pop from both stacks together (keep them in sync).
top(): return mainStack.peek().
getMin(): return minStack.peek() — O(1), no scanning needed!

The trick: minStack always has the same size as mainStack, so at any index, minStack[i] = the min of mainStack[0..i]. When you pop, the min "rolls back" automatically because you're popping the outdated min value too.

When to use two-stack pattern:
Whenever you need O(1) access to some running aggregate (min, max, even sum) that changes as items are pushed/popped.
When NOT to use it:
If you rarely call getMin() (say, once at the very end) — then it's simpler to just scan once, O(n), rather than pay the cost of an extra stack on every push.

Space: doubles your memory (O(n) extra for minStack) — you're trading memory for speed.
An alternative to save space: store only a (value, prevMin) pair when the min actually changes (instead of pushing on every single push) — 
reduces minStack size but adds a bit of branching complexity. Worth mentioning in an interview as an optimization if pressed on space.

Mini Example
Trace through the example: push(1), push(2), push(0), getMin(), pop(), top(), getMin()
Operation	mainStack (top→)	minStack (top→)	Result
push(1)	[1]	[1]	
push(2)	[2,1]	[1,1]	
push(0)	[0,2,1]	[0,1,1]	
getMin()			0
pop()	[2,1]	[1,1]	
top()			2
getMin()			1

Matches the expected output [null,null,null,null,0,null,2,1] 

Real use case: this "track a running min/max alongside a stack" pattern shows up in problems like "max stack," 
sliding window maximum (monotonic deque variant), and even in some undo systems that need to know "what was the smallest/most extreme state reached."

When O(1) is demanded for an aggregate query on a mutable structure, look for a way to "cache the answer at every step" rather than recomputing it.
The two-stack trick is really just "precompute and store alongside," paid for in space instead of time.
Keep auxiliary structures in perfect sync with the main one (same push/pop timing) — that's what guarantees the cached value at the top is always correct for the current state.

  */

class MinStack {

    private Stack<Integer> stack;

    public MinStack() {
        stack = new Stack<>();
    }

    public void push(int val) {
        stack.push(val);
    }

    public void pop() {
        stack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        Stack<Integer> tmp = new Stack<>();
        int mini = stack.peek();

        while (!stack.isEmpty()) {
            mini = Math.min(mini, stack.peek());
            tmp.push(stack.pop());
        }

        while (!tmp.isEmpty()) {
            stack.push(tmp.pop());
        }

        return mini;
    }
}

/*
both give correct results, but this version fails the O(1) requirement.

This code:
push, pop, top → O(1), correct.
getMin() → not O(1), it's O(n) every single call, because it has to:
Move every element out of stack into tmp (tracking the min along the way) — O(n)
Move everything back from tmp into stack — O(n)
That's O(n) per call to getMin(). If getMin() is called k times total, that's O(n·k) overall — which violates the problem's explicit requirement that "each function should run in O(1) time."

The two-stack version :
Every function, including getMin(), is truly O(1) — it's just minStack.peek().

Why this matters: Look at the constraint — up to 3×10⁴ calls total. 
If a good chunk of those are getMin() calls on a stack with tens of thousands of elements, your version's runtime balloons (worst case roughly 3×10⁴ × 3×10⁴ ≈ 9×10⁸ operations),
while the two-stack version stays O(1) per call regardless of stack size.

Summary:
This code	Two-stack
push/pop/top	O(1)	O(1)
getMin()	O(n)	O(1) 
Extra space	O(n) temporarily, during each getMin() call	O(n) permanently (minStack)

This code produces correct output and would pass all test cases functionally, but it doesn't meet the "spirit" of the problem, 
which specifically asks for O(1) on every operation. In an interview, this is exactly where the interviewer would follow up: "What's the time complexity of your getMin()?" —
and once you say O(n), the natural next question is "Can you make it O(1)?" — which leads straight into the two-stack pattern.
*/
