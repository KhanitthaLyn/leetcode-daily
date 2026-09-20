/*
You are given an array of CPU tasks tasks, where tasks[i] is an uppercase english character from A to Z. You are also given an integer n.
Each CPU cycle allows the completion of a single task, and tasks may be completed in any order.
The only constraint is that identical tasks must be separated by at least n CPU cycles, to cooldown the CPU.
Return the minimum number of CPU cycles required to complete all tasks.

Example 1:
Input: tasks = ["X","X","Y","Y"], n = 2
Output: 5

Explanation: A possible sequence is: X -> Y -> idle -> X -> Y.

Constraints:

    1 <= tasks.length <= 10000
    0 <= n <= 100
*/

/*
Imagine
You have a pile of tasks, and identical tasks need a "cooldown" period of n cycles before they can run again. If nothing else is available to fill the gap, the CPU sits idle.
Think of it like a laundromat with one washer per task-type is not quite it — better analogy: think of a chef in a kitchen who can't cook the same dish twice in a row without waiting n minutes for the pan to cool down. 
To minimize total time, the chef should always cook whichever dish still has the most orders remaining — because that's the dish most likely to cause bottlenecks later if left waiting.
This "always pick the most frequent remaining task" strategy is a greedy heuristic, and a Max-Heap is the natural tool to track "which task type has the most remaining count" at any given moment.
*/

/*
The problem: At every cycle, you must decide which task to run, subject to the cooldown constraint. Get the ordering wrong, and you create unnecessary idle cycles.
Why not brute-force? You could try all possible orderings and pick the shortest one, but with up to 10,000 tasks, that's factorially explosive — completely infeasible.

Why greedy-by-frequency works: The tasks with the highest frequency are the ones most likely to force idle cycles later, 
since they need the most "spacing" throughout the whole schedule. Scheduling them first (whenever eligible) spreads them out as early and evenly as possible, 
minimizing forced gaps. A Max-Heap gives you O(log 26) access to "the most frequent remaining task" — trivial here since there are only 26 possible task types.
*/


/*
Approach 1: Max-Heap + Cooldown Queue (simulation)
Idea: Simulate cycle by cycle. Keep a max-heap of remaining counts. Each cycle, pop the most frequent task, decrement it, 
and put it in a "cooldown queue" with the cycle at which it becomes eligible again. When that cycle arrives, push it back into the heap.
Complexity: Time O(n_tasks) roughly, since heap operations are bounded by 26 elements → effectively O(n_tasks · log 26). Space O(26) = O(1).
*/
public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;

    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) if (f > 0) maxHeap.offer(f);

    Queue<int[]> cooldown = new LinkedList<>(); // [remainingCount, availableAtCycle]
    int time = 0;

    while (!maxHeap.isEmpty() || !cooldown.isEmpty()) {
        time++;
        if (!maxHeap.isEmpty()) {
            int count = maxHeap.poll() - 1;
            if (count > 0) {
                cooldown.offer(new int[]{count, time + n});
            }
        }
        // check if the earliest cooling-down task is ready to re-enter the heap
        if (!cooldown.isEmpty() && cooldown.peek()[1] == time) {
            maxHeap.offer(cooldown.poll()[0]);
        }
    }
    return time;
}

/*
Approach 2: Math Formula (frequency-based, no simulation)

Idea: You don't actually need to simulate anything. The answer only depends on:

maxFreq: the highest frequency among all task types
maxCount: how many task types share that highest frequency

Picture the most frequent task laid out with its required gaps as a grid:

A _ _ A _ _ A _ _ A   (maxFreq = 4, n = 2)

This creates (maxFreq - 1) full gaps of size n, plus the last row. Other tasks fill into the gaps; if they don't fill all of them, idle cycles remain.
Complexity: Time O(tasks.length) just to count frequencies. Space O(1) (fixed 26-element array).
*/

public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;

    int maxFreq = 0;
    int maxCount = 0;
    for (int f : freq) {
        if (f > maxFreq) {
            maxFreq = f;
            maxCount = 1;
        } else if (f == maxFreq) {
            maxCount++;
        }
    }

    int partCount = maxFreq - 1;
    int partLength = n - (maxCount - 1);
    int emptySlots = partCount * partLength;
    int availableTasks = tasks.length - maxFreq * maxCount;
    int idles = Math.max(0, emptySlots - availableTasks);

    return tasks.length + idles;
}

/*
Approach 3: Max-Heap without a cooldown queue (batch-of-n+1 simulation)

Idea: Instead of tracking exact cycle numbers, process tasks in rounds of size n+1. 
In each round, pop up to n+1 distinct most-frequent tasks, decrement them, and hold them temporarily; push survivors back after the round. 
If a round has no tasks to run, the leftover slots are idle.
Complexity: Time O(n_tasks · log 26) similar to Approach 1. Space O(26).
*/

public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char t : tasks) freq[t - 'A']++;

    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    for (int f : freq) if (f > 0) maxHeap.offer(f);

    int time = 0;
    while (!maxHeap.isEmpty()) {
        List<Integer> temp = new ArrayList<>();
        int cycles = n + 1;
        while (cycles > 0 && !maxHeap.isEmpty()) {
            int count = maxHeap.poll();
            if (count > 1) temp.add(count - 1);
            time++;
            cycles--;
        }
        for (int c : temp) maxHeap.offer(c);
        // if maxHeap empty but cycles > 0, no idle needed since we're done
        if (!maxHeap.isEmpty()) time += cycles; // add remaining idle cycles in this round
    }
    return time;
}

/*

Trade-offs & When to Use
Approach	When to use	Trade-off
- Heap + Cooldown Queue	When you want to actually understand/trace the schedule cycle-by-cycle, or the interviewer wants to see simulation logic	More code, slightly harder to get the queue timing right
- Math Formula	When you just need the answer fast, and the fixed 26-letter alphabet makes closed-form derivation clean	Requires deriving/memorizing the formula; less "obviously correct" without the derivation explained; harder to explain in an interview if asked "why does this work" without walking through the gap-filling logic
- Heap batch-of-n+1	A middle ground — still heap-based, avoids a separate cooldown queue	The "add remaining idle cycles" logic is a bit fiddly to get right at the edges

When NOT to overthink it: Given only 26 possible task types, heap overhead is negligible (O(log 26) ≈ constant) 
you don't need to worry about heap performance at scale here. The real decision is about code clarity vs. one-line elegance.

Mini Example
Dry run with tasks = ["A","A","A","B","C"], n = 3 using Approach 2 (Math):

freq: A=3, B=1, C=1
maxFreq = 3, maxCount = 1 (only A has freq 3)

partCount = maxFreq - 1 = 2
partLength = n - (maxCount - 1) = 3 - 0 = 3
emptySlots = 2 * 3 = 6
availableTasks = 5 - (3*1) = 2
idles = max(0, 6 - 2) = 4

answer = tasks.length + idles = 5 + 4 = 9 

Matches the expected output exactly.

Golden Rule: When you see "the most frequent item causes the most constraint," think about whether you can derive a closed-form formula based on just that item's frequency, 
instead of simulating everything — especially when the "alphabet" of possible types is small and fixed (here, 26 letters bounds the heap size to a constant).
Second rule: Simulation (heap-based) is more intuitive and safer to write correctly under interview pressure; the math formula is faster and more elegant, 
but only worth using if you can also explain why it works — otherwise it looks like a memorized trick rather than understanding.
*/
