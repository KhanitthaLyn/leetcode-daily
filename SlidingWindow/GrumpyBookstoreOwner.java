/* There is a bookstore owner that has a store open for n minutes. You are given an integer array customers of length n where customers[i] is the number of the customers that enter the store at the start of the ith minute and all those customers leave after the end of that minute.

During certain minutes, the bookstore owner is grumpy. You are given a binary array grumpy where grumpy[i] is 1 if the bookstore owner is grumpy during the ith minute, and is 0 otherwise.

When the bookstore owner is grumpy, the customers entering during that minute are not satisfied. Otherwise, they are satisfied.

The bookstore owner knows a secret technique to remain not grumpy for minutes consecutive minutes, but this technique can only be used once.

Return the maximum number of customers that can be satisfied throughout the day.*/ 


public int maxSatisfied(int[] customers, int[] grumpy, int minutes) {
    int n = customers.length;

    // 1. Baseline: customers satisfied even without using the technique
    int baseSatisfied = 0;
    for (int i = 0; i < n; i++) {
        if (grumpy[i] == 0) {
            baseSatisfied += customers[i];
        }
    }

    // 2. Sliding window over the "at-risk" customers (only counted when grumpy == 1)
    int windowGain = 0;
    int maxGain = 0;

    for (int right = 0; right < n; right++) {
        // Expand: add this minute's at-risk customers if grumpy
        if (grumpy[right] == 1) {
            windowGain += customers[right];
        }

        // Shrink: once window exceeds size 'minutes', remove the leftmost minute
        if (right >= minutes) {
            int left = right - minutes;
            if (grumpy[left] == 1) {
                windowGain -= customers[left];
            }
        }

        maxGain = Math.max(maxGain, windowGain);
    }

    return baseSatisfied + maxGain;
}

/* Brute force: For every possible starting position of the "calm technique" window (there are n - minutes + 1 of them), 
sum up the customers in that window who were originally lost to grumpiness → for each position that's an O(minutes) sum → total O(n × minutes).

Why that's not enough: With n up to 20,000, if minutes is also large, O(n × minutes) can blow up to O(n²) in the worst case — way too slow. 
Recomputing the sum from scratch every time you shift the window by one position is wasteful, since most of the window's content doesn't change between adjacent positions.

The insight: When you slide a fixed-size window one step to the right, you only lose one element (the one falling off the left) and gain one element (the new one on the right). 
You can update the window sum in O(1) instead of recomputing it — that's the whole point of the fixed-size sliding window pattern. 


Step-by-step with customers = [1,0,1,2,1,1,7,5], grumpy = [0,1,0,1,0,1,0,1], minutes = 3:

baseSatisfied = sum where grumpy==0 → indices 0,2,4,6 → 1+1+1+7 = 10
At-risk values (grumpy==1) → indices 1,3,5,7 → values 0,2,1,5
Slide a window of size 3 over the at-risk contribution:
window [0,1,2] (idx 1 contributes 0) → gain = 0
window [1,2,3] (idx 1,3 contribute) → gain = 0+2 = 2
window [2,3,4] → gain = 2
window [3,4,5] (idx 3,5 contribute) → gain = 2+1 = 3
window [4,5,6] → gain = 1
window [5,6,7] (idx 5,7 contribute) → gain = 1+5 = 6 ← max
maxGain = 6
Result = 10 + 6 = 16 matches expected output

Use this when: You need to find the best fixed-length contiguous window to apply some transformation or bonus, and the rest of the array's contribution is independent of where that window sits
(i.e., you can separate "fixed baseline" from "the part you're optimizing").
Don't use this when: The window size itself is variable/unknown (that's variable sliding window, not fixed), 
or the window position affects the baseline calculation too (then you can't cleanly separate baseline vs. window gain).
Trade-off: You get O(n) time and O(1) extra space, at the cost of needing a mental separation step first — 
you must recognize that the answer decomposes into baseline + best_window_gain. If you try to track raw customer satisfaction directly inside the sliding window without this decomposition, 
the logic gets tangled (you'd need to conditionally add/subtract based on grumpy state in a less clean way).

Real-World Scenario 
A customer support team has one "surge staffing" boost they can deploy for a fixed block of time (say, a 2-hour window) during the day. 
Ticket volume and current staffing quality vary by hour. You want to find the best 2-hour window to deploy the surge staff to maximize the number of additionally resolved tickets 
tickets that would already get resolved on time don't need the boost; you're optimizing where to place the boost among the currently-understaffed hours.

Key Takeaway
Decompose first, then slide: Whenever a problem gives you "guaranteed good outcomes" plus "one chance to fix a fixed-size bad segment," 
split the answer into baseline (always guaranteed) + max(fixable gain via fixed window). This decomposition is often the hardest conceptual step — the sliding window mechanics themselves are simple once you see it.
Fixed window = O(1) update per slide: The moment you know the window size is exactly minutes (constant), you should immediately think "expand by one, shrink by one, no need to recompute the sum from scratch."
*/

public class Solution {
    public int maxSatisfied(int[] customers, int[] grumpy, int minutes) {
        int res = 0, n = customers.length;
        for (int i = 0; i < n; i++) {
            if (grumpy[i] == 0) {
                res += customers[i];
            }
        }

        int satisfied = res;
        for (int i = 0; i <= n - minutes; i++) {
            int cur = 0;
            for (int j = i; j < i + minutes; j++) {
                if (grumpy[j] == 1) {
                    cur += customers[j];
                }
            }
            res = Math.max(res, satisfied + cur);
        }

        return res;
    }
}

/*

Different — different time complexity, even though both produce the correct answer.

**Comparing point by point:**

| Point | First code | Sliding window version |
|---|---|---|
| Time complexity | **O(n × minutes)** | O(n) |
| How each window is computed | Recomputes the full sum from scratch every time (nested loop) | Updates in O(1) by adding the new element and removing the old one |
| Total iterations | (n - minutes + 1) positions × minutes elements each | n iterations total (single pass) |

**The key difference:** first code is a **partially-optimized brute force**, not a true sliding window — because for every starting position `i`, it **recomputes that window's sum entirely** with an inner loop from `j = i` to `j = i + minutes - 1`, instead of reusing what was already computed for the previous window.

To visualize it: when sliding from window `[i, i+minutes-1]` to `[i+1, i+minutes]`, there are `minutes - 1` overlapping elements — but this code discards all previously computed information and recounts everything from zero each time. That throws away exactly the reuse that is the whole point of sliding window.

**Real impact:** given the constraint `n <= 20,000` and `minutes` potentially close to `n` (`1 <= minutes <= n`), the worst case for this code can approach **O(n²)** = 20,000² = 400 million operations, which is tens of thousands of times slower than sliding window's O(n) = 20,000 operations. 
In an actual interview, this might pass if the test cases aren't large enough, but an interviewer who catches this will immediately ask: "can you reduce the complexity further?"
*/

/*
Comparing both approaches: O(n × minutes) vs O(n)

Approach 1: Nested loop  — O(n × minutes)
**Pros:**
- Very readable and easy to understand — it clearly shows "for each starting position, sum up the range"
- Fast to write, no edge cases to worry about around index boundaries (the inner loop already has clear bounds)
- If `minutes` is very small (e.g. 2-3), in practice the speed difference from O(n) might not be huge
**Cons:**
- Genuinely worse time complexity — worst case approaches O(n²) when `minutes` is close in size to `n`
- With this problem's constraint of `n <= 20,000`, it could time out if the test cases are deliberately designed to stress-test (`minutes` ≈ n/2)
- **In an interview:** a clear negative signal that you haven't recognized the sliding window pattern yet — the interviewer will see this as not fully grasping the core of the technique, even though the logic is correct

Approach 2: Sliding window (expand/shrink) — O(n)
**Pros:**
- The best possible time complexity for this problem — you need to read the data at least once anyway (O(n) is the lower bound)
- Same constant O(1) space
- **In an interview:** demonstrates that you understand why incremental updates eliminate redundant work — exactly what interviewers want to see
**Cons:**
- Slightly trickier to write, you need to be careful with indices when shrinking (`right - minutes` must be checked against `right >= minutes` first)
- Harder to debug if you get it wrong — if you forget to subtract the element leaving the window, the result becomes silently incorrect (no crash, just a wrong number)

 which is best?

**For production code and interviews → Sliding window (O(n)) wins decisively**, because:
1. Correctness is identical, but performance is unambiguously better, especially when `minutes` is close to `n`
2. There's no readability downside significant enough to justify the performance you're giving up — both versions are roughly the same length
*/
