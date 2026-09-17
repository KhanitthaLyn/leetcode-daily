/*
You are given an array of integers stones where stones[i] represents the weight of the ith stone.

We want to run a simulation on the stones as follows:

    At each step we choose the two heaviest stones, with weight x and y and smash them togethers
    If x == y, both stones are destroyed
    If x < y, the stone of weight x is destroyed, and the stone of weight y has new weight y - x.

Continue the simulation until there is no more than one stone remaining.

Return the weight of the last remaining stone or return 0 if none remain.

Example 1:

Input: stones = [2,3,6,2,4]

Output: 1

Explanation:
We smash 6 and 4 and are left with a 2, so the array becomes [2,3,2,2].
We smash 3 and 2 and are left with a 1, so the array becomes [1,2,2].
We smash 2 and 2, so the array becomes [1].

Example 2:

Input: stones = [1,2]

Output: 1

Constraints:

    1 <= stones.length <= 20
    1 <= stones[i] <= 100

*/

public int lastStoneWeight(int[] stones) {
    // Java's PriorityQueue is a Min-Heap by default — must reverse it
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    
    for (int stone : stones) {
        maxHeap.offer(stone);
    }
    
    while (maxHeap.size() > 1) {
        int y = maxHeap.poll(); // heaviest
        int x = maxHeap.poll(); // second heaviest
        if (y != x) {
            maxHeap.offer(y - x);
        }
    }
    
    return maxHeap.isEmpty() ? 0 : maxHeap.poll();
}
//Java gotcha: PriorityQueue defaults to a Min-Heap — you must pass Collections.reverseOrder() or (a, b) -> b - a to flip it into a Max-Heap.


/*
The naive approach: re-sort the entire array every round, then take the last two (largest) elements.

java
// Naive approach
Arrays.sort(stones); // O(n log n) every single round!

The problem: there can be up to n-1 rounds of smashing (each round removes at least one stone), 
and each round requires re-sorting the whole array (O(n log n)) → total O(n² log n), which is wasteful. We don't actually need "the full ordering" — we only need the top 2 each round.

Sorting the entire array every time is over-engineering, because the relative order of the other (n-2) elements never changes between rounds.

How It Works
Idea: Put all stones into a Max-Heap, then pop two at a time (always the largest available), smash them, and push the remainder back if there is one.

Step-by-step workflow:

Insert all stones into a Max-Heap → O(n log n)
While the heap has more than 1 element:
y = poll() → pull out the heaviest (O(log n))
x = poll() → pull out the second heaviest (O(log n))
If y != x → push(y - x) back into the heap (O(log n))
If y == x → do nothing (both are destroyed)

When to use a Heap:
Whenever you repeatedly need "the max/min value" from a dataset that keeps changing (inserts/removals over time)
When you don't need the full ordering — just the Top-K or the extreme value

When NOT to use:
If you only need "the max" once from static data → a linear scan O(n) is enough; don't pay the overhead of building a heap
If you need frequent binary search or range queries → a TreeMap/TreeSet is better, since it maintains full ordering and supports more flexible queries

Trade-offs:
You gain: Insert and Extract-Max/Min both run in O(log n), instead of re-sorting O(n log n) every time
You give up: A heap doesn't maintain full sorted order (unlike a sorted array) — if you need to traverse everything in order, you have to poll one at a time, not just iterate

Complexity summary:
Approach	Time	Space
Naive (sort every round)	O(n² log n)	O(1) or O(n) depending on sort
Max-Heap	O(n log n)	O(n)

Mini Example

Dry run with stones = [2,3,6,2,4]:

Initial heap: [6,4,3,2,2] (shown sorted just for clarity)

Round 1: poll 6, poll 4 → 6≠4 → push (6-4)=2
Heap: [3,2,2,2]

Round 2: poll 3, poll 2 → 3≠2 → push (3-2)=1
Heap: [2,2,1]

Round 3: poll 2, poll 2 → 2==2 → push nothing
Heap: [1]

Heap size = 1 → return 1 
Matches the expected output.

Golden Rule: Whenever a problem talks about "the max/min value at any given moment" and the data changes repeatedly (inserts/updates), 
think Heap first. It's the structure specifically optimized to answer "what's the extreme value right now, fast" — without paying the cost of a full sort every time.

Also remember: Java's PriorityQueue defaults to a Min-Heap — forgetting to reverse the comparator is one of the most common bugs with this data structure.
*/


public class Solution {
    public int lastStoneWeight(int[] stones) {
        List<Integer> stoneList = new ArrayList<>();
        for (int stone : stones) {
            stoneList.add(stone);
        }

        while (stoneList.size() > 1) {
            Collections.sort(stoneList);
            int cur = stoneList.remove(stoneList.size() - 1) -
                      stoneList.remove(stoneList.size() - 1);
            if (cur != 0) {
                stoneList.add(cur);
            }
        }

        return stoneList.isEmpty() ? 0 : stoneList.get(0);
    }
}
/*
This code is the **naive approach** .

**Correctness:** logic is correct
- `Collections.sort()` sorts ascending
- First `remove(size()-1)` → gets the largest (`y`)
- Second `remove(size()-1)` (list is now one shorter) → gets the second largest (`x`)
- `y - x` correctly follows the smashing logic

**Worth flagging (though not a bug here):** `remove(int)` vs `remove(Object)` on `List<Integer>` are different overloads — since `stoneList.size() - 
1` is a plain `int`, Java correctly resolves to `remove(int index)` (removes by position, not by value), which is what you want here. 
But if you ever accidentally wrote `remove(Integer.valueOf(x))`, it would remove by value instead — a classic bug people run into with `List<Integer>`.

**Where they differ is complexity:**

| | This code (ArrayList + sort) | Max-Heap |
|---|---|---|
| Time | **O(n² log n)** — re-sorts the whole list every round | **O(n log n)** — poll/offer only cost O(log n) |
| Space | O(n) | O(n) |

Every round of while loop re-sorts the entire list (O(n log n)), even though only **one value actually changed** (the new value pushed back in) — 
the other (n-2) elements never changed relative order. That's the exact "wasted work" a Heap eliminates: instead of re-sorting everything, 
it only repositions the value that was just inserted/removed (O(log n)).

With this problem's constraint of `n <= 20`, the performance difference is negligible in practice. 
But if you hit a similar problem with a much larger n (say 10^5), the sort-every-round approach would become too slow, while the Heap approach would still hold up fine.

  */
