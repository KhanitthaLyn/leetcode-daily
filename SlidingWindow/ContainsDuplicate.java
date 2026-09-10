//You are given an integer array nums and an integer k, return true if there are two distinct indices i and j in the array such that nums[i] == nums[j] and abs(i - j) <= k, otherwise return false.

public boolean containsNearbyDuplicate(int[] nums, int k) {
    Set<Integer> window = new HashSet<>();

    for (int i = 0; i < nums.length; i++) {
        // check first whether this element duplicates anything in the window
        if (window.contains(nums[i])) {
            return true;
        }

        window.add(nums[i]);

        // shrink the window: if size exceeds k, remove the oldest element
        if (window.size() > k) {
            window.remove(nums[i - k]);
        }
    }

    return false;
}


/*
Brute force: Check every pair (i, j) where nums[i] == nums[j] and see if abs(i - j) <= k → nested loop → O(n²)
Why that's not enough: n can be up to 100,000, so O(n²) = 10,000,000,000 operations — that will definitely time out.
The insight: You never need to look back further than k positions, because a duplicate pair farther apart than k doesn't count anyway. So you just "remember only the last k elements" in a HashSet, 
and check whether the current element duplicates anything in there — a constant-time check (O(1) amortized) instead of scanning back through every previous element.

Step-by-step with nums = [1,2,3,1], k = 3:

i	nums[i]	Check window first	Action	window after
0	1	{} no 1	add 1	{1}
1	2	{1} no 2	add 2	{1,2}
2	3	{1,2} no 3	add 3	{1,2,3} (size=3, not over k)
3	1	{1,2,3} has 1!	→ return true	—

Result: true, matching the expected output.

Checking with nums = [2,1,2], k = 1:

i	nums[i]	Check	Action	window
0	2	{} none	add 2	{2}
1	1	{2} no 1	add 1, size=2 > k(1) → remove nums[1-1]=nums[0]=2	{1}
2	2	{1} no 2	add 2, size=2 > k(1) → remove nums[2-1]=nums[1]=1	{2}

No duplicate found → false, matching the expected output (because the two 2s at index 0 and 2 are abs(0-2)=2 > k=1 apart).

Use this when: You need to check whether a duplicate value exists within a bounded distance (k) — problems involving a "distance constraint" between two indices holding the same value.
Don't use this when: You actually need the "closest positions" of a duplicate pair (not just true/false) — in that case you'd likely want a HashMap storing the last seen index instead of a HashSet, to actually compute the real distance.

Trade-off: You get O(n) average time, but the cost is O(min(n, k)) space, since the HashSet can hold up to k elements (unlike the pure O(1) two-pointer/index-based approaches from before). This is a good example that sliding window doesn't always mean O(1) auxiliary space — sometimes you trade space for faster lookups.

Real-World Scenario 
A fraud detection system that needs to check whether "the same transaction ID was submitted again within the last 5 seconds" (to prevent duplicate requests/replay attacks) 
using a sliding window of recent transactions in memory instead of querying the entire database every time.

A window doesn't have to store just a number (sum/count) — it can store a "set of members" instead. Whenever a problem mentions "within distance k," 
immediately think of a fixed-size window backed by a HashSet/HashMap instead of an array-based running sum.
Order of operations matters a lot: you must "check before adding" (contains, then add), not "add then check" 
because if you add first, the element would match itself (i == j), but the problem explicitly requires distinct indices.
*/


public class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        for (int L = 0; L < nums.length; L++) {
            for (int R = L + 1; R < Math.min(nums.length, L + k + 1); R++) {
                if (nums[L] == nums[R]) {
                    return true;
                }
            }
        }
        return false;
    }
}

/*
different time complexity, even though the condition-checking logic is similar.
**Comparing point by point:**

| Point | Bounded Brute Force | Sliding window + HashSet version |
|---|---|---|
| Time complexity | **O(n × k)** | O(n) average |
| Structure | Nested loop: iterate every `L`, then search for `R` in range `[L+1, L+k]` | Single pass, checking a HashSet that only tracks the last `k` elements |
| Space | O(1) | O(min(n, k)) |

**The key difference:** Bounded Brute Force is a brute force approach that has **bounded its search range using `k`** 
(smarter than a full brute force checking every pair in the whole array), but it's still not a true sliding window — because for every position `L`, 
it uses an inner loop to **recheck everything from scratch** in the range `[L+1, L+k]`, instead of "remembering" what was already checked from the previous `L`.

To visualize it: when moving from `L=0` to `L=1`, the range `[L+1, L+k]` overlaps almost entirely (only the far end shifts by one), 
but this code rechecks everything each time instead of reusing the knowledge of which elements were already in the previous range.

**Real impact:** with `n <= 100,000` and `k` potentially as large as `100,000` too, this code's worst case approaches **O(n × k)** = 100,000 × 100,000 = 10,000,000,000 operations → definitely times out. Compare that to sliding window's O(n) = 100,000 operations.

the logic is correct (passes correctness) and it's better than a full brute force since it already bounds the search using `k` — but it's **still not sliding window**, because there's no "state carried across" iterations of `L`. It's just a bounded brute force, not an incremental technique.

*/


/*
Comparing: Bounded Brute Force (O(n×k)) vs Sliding Window + HashSet (O(n))

Approach 1: Bounded brute force — O(n × k)
**Pros:**
- Very readable, straightforward structure — the outer/inner loop relationship is clear
- O(1) space — no auxiliary data structure needed at all
- Fast to write, no complex edge cases (no need to worry about which element to remove from a window)
**Cons:**
- Significantly worse time complexity — worst case O(n × k), which with this problem's constraints (`n, k` up to 100,000) can reach 10 billion operations → **will definitely time out**
- **In an interview:** a negative signal that you haven't spotted the incremental pattern yet — even though it's smarter than full brute force (already bounded by k), the interviewer will immediately ask: "can you reduce the time complexity further?"

Approach 2: Sliding window + HashSet — O(n)

**Pros:**
- Best possible time complexity — a single pass through the array
- Amortized O(1) per check, since HashSet lookups are fast
- **In an interview:** demonstrates that you understand why you shouldn't need to recheck data you've already seen
**Cons:**
- Trades in O(min(n, k)) space — not O(1) like the first approach, but in practice this is a very worthwhile trade-off since k is bounded by n anyway
- Need to be careful to get the check-before-add order right, otherwise you'd get a false positive (an element matching itself)

---
which is better?

**Sliding window + HashSet is clearly better** for this problem, because:
1. **Correctness is identical, but performance differs enormously** — with the constraint `n, k <= 100,000`, the first approach will genuinely time out on a grading system (LeetCode/interview), while the second will pass comfortably.
2. **The space traded (O(k)) is cheap compared to the time saved** — O(k) space (worst case O(n) if k is very large) is easily manageable with modern memory, unlike time, which has no fix if the algorithm is fundamentally slow.

**Decision rule:** whenever you notice your inner loop is "re-searching" a range that overlaps almost entirely with the previous iteration's range (like here, where `[L+1, L+k]` shifts by just one position each time), 
immediately suspect this is a sign of redundant work that should be converted to a sliding window — regardless of whether it costs extra space, because time complexity usually matters more than space when `n` is large.

*/

