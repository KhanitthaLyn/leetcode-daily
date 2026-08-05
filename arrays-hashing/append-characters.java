
/*You are given two strings `s` and `t` consisting of only lowercase English letters.
Return the minimum number of characters that need to be appended to the end of `s` so that `t` becomes a subsequence of `s`.
A subsequence is a string that can be derived from another string by deleting some or no characters without changing the order of the remaining characters.*/

/*The Core Problem & Intuition
Good question — let's break down **why greedy (leftmost match) is provably optimal** here.

## The Core Argument: "Matching Earlier Never Hurts"

The key insight is this: **if you can match `t[j]` with `s[i]`, doing so as early as possible (at the very first occurrence) never makes things worse — and can only make things better (or equal) for matching the rest of `t`.**

Let's prove this with an **exchange argument** (a classic technique for proving greedy correctness):

### Suppose greedy is NOT optimal

Imagine there's some "smarter" strategy that skips the first match of `s[i] == t[j]` and instead matches `t[j]` with a *later* occurrence of the same character in `s`, say at position `s[k]` where `k > i`.

```
s = a  b  a  c  d
    ↑        ↑
    i        k
t = a  ...
    ↑
    j
```

Both `s[i]` and `s[k]` equal `t[j]`, so either one is a valid match. But think about what happens *next*:

- If you match at `i` (earlier), you now search for `t[j+1]` starting from position `i+1`.
- If you match at `k` (later), you now search for `t[j+1]` starting from position `k+1`.

Since `i < k`, the remaining substring of `s` available for matching `t[j+1], t[j+2], ...` is **strictly larger (or equal)** when you match early:

```
s[i+1:]  ⊇  s[k+1:]     (the earlier match leaves a superset of characters to work with)
```

**Any character the "later match" strategy could find in `s[k+1:]` can also be found in `s[i+1:]`**, because `s[i+1:]` contains `s[k+1:]` as a subset (everything after position k is also after position i, plus you get the bonus range between i+1 and k).

So matching early never restricts your future options — it only ever preserves or expands them. This means greedy's leftmost match can never lead to a worse outcome than any alternative strategy.

## Why This Matters: No "Lookahead" Needed

This is exactly why we **don't need DP** here. DP would be necessary if there were a real trade-off — situations where matching now costs you a better match later (forcing you to weigh options). But in subsequence matching:

- There's no cost to matching early.
- There's no benefit to "saving" a character in `s` for later.
- The problem has **no competing choices** — it's monotonic and one-directional.

This property is sometimes called the **greedy choice property**: a locally optimal choice (match ASAP) leads to a globally optimal solution, because no future decision is ever hurt by an earlier greedy decision.

## Contrast with Problems Where Greedy Fails

Compare this to something like the classic **Longest Common Subsequence (LCS)** between two arbitrary strings (where both strings can be modified/deleted from). There, greedy leftmost-matching *can* fail:

```
s = "abcbdab"
t = "bdab"
```

Here, whether you match the first `b` in `s` or wait for a later `b` genuinely changes what's achievable afterward — because you're comparing two *independent* sequences with no directional constraint tying them together. That's why LCS needs full DP: it has to consider both "take" and "skip" branches and compare outcomes.

**The difference in our problem:** `s` is fixed and can only be *extended*, never edited — so there's a strict, unbreakable directionality that removes all ambiguity from the matching process. That directional constraint is what turns a potential DP problem into a simple linear scan.


  */


class Solution {
    public int appendCharacters(String s, String t) {
        int i = 0, j = 0;
        
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {   
                j++;
            }
            i++;                                 
        }
        
        return t.length() - j;
    }
}

/*can you predict whether a similar two-pointer greedy approach would still work if the problem allowed you to also delete characters from s (not just append to the end)? Why or why not?
-> 
**Intuition:**

> **Matching as early as possible leaves the most options for the rest of the string.**

Suppose we are searching for the character `t[j]` in string `s`, and it appears at indices $i_1$ and $i_2$ (where $i_1 < i_2$).

If we greedily match `t[j]` with `s[i_1]` (the first occurrence):

* The search space for the next character, `t[j+1]`, spans from index $i_1 + 1$ to the end of string `s`.
* If we skip $i_1$ and match with `s[i_2]` instead, the search space shrinks to $[i_2 + 1, \text{end}]$.

Since the search space $[i_1 + 1, \text{end}]$ fully covers $[i_2 + 1, \text{end}]$, choosing $i_1$ can **never yield a worse result** than choosing $i_2$.

---

**Short Proof (Exchange Property):**
Let $G = [g_1, g_2, \dots, g_k]$ be the sequence of matched indices in `s` chosen by the Greedy approach, and $A = [a_1, a_2, \dots, a_k]$ be any valid matching sequence.

Since Greedy always picks the earliest possible index for each character, by induction we have $g_m \le a_m$ for all $m$.

This guarantees that Greedy leaves a search space at least as large as any other strategy, proving that picking the first matching character immediately is strictly optimal.
*/
