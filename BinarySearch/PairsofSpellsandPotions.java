/*
Successful Pairs of Spells and Potions

You are given two positive integer arrays spells and potions, of length n and m respectively, where spells[i] represents the strength of the ith spell and potions[j] represents the strength of the jth potion.

You are also given an integer success. A spell and potion pair is considered successful if the product of their strengths is at least success.

Return an integer array pairs of length n where pairs[i] is the number of potions that will form a successful pair with the ith spell.

 

Example 1:

Input: spells = [5,1,3], potions = [1,2,3,4,5], success = 7
Output: [4,0,3]
Explanation:
- 0th spell: 5 * [1,2,3,4,5] = [5,10,15,20,25]. 4 pairs are successful.
- 1st spell: 1 * [1,2,3,4,5] = [1,2,3,4,5]. 0 pairs are successful.
- 2nd spell: 3 * [1,2,3,4,5] = [3,6,9,12,15]. 3 pairs are successful.
Thus, [4,0,3] is returned.

Example 2:

Input: spells = [3,1,2], potions = [8,5,8], success = 16
Output: [2,0,2]
Explanation:
- 0th spell: 3 * [8,5,8] = [24,15,24]. 2 pairs are successful.
- 1st spell: 1 * [8,5,8] = [8,5,8]. 0 pairs are successful. 
- 2nd spell: 2 * [8,5,8] = [16,10,16]. 2 pairs are successful. 
Thus, [2,0,2] is returned.

 

Constraints:

    n == spells.length
    m == potions.length
    1 <= n, m <= 105
    1 <= spells[i], potions[i] <= 105
    1 <= success <= 1010
*/

class Solution {
    public int[] successfulPairs(int[] spells, int[] potions, long success) {
        Arrays.sort(potions);
        int m = potions.length;
        int[] result = new int[spells.length];

        for (int i = 0; i < spells.length; i++) {
            long spell = spells[i];
            int low = 0, high = m;

            while (low < high) {
                int mid = low + (high - low) / 2;
                if (spell * (long) potions[mid] >= success) {
                    high = mid;
                } else {
                    low = mid + 1;
                }
            }
            result[i] = m - low;
        }
        return result;
    }
}
//Time: O(m log m + n log m) — sort potions once, then binary search n times
//Space: O(1) (excluding the output array and whatever sort uses internally)

/*
The problem: The brute-force approach loops through every spell (n of them) and checks against every potion (m of them) — O(n × m). With n, m ≤ 10^5, 
that's up to 10^10 operations, which is way too slow (guaranteed TLE).
Why the naive way falls short: Checking every pair is wasteful, because if one potion succeeds with a given spell (product ≥ success), 
any stronger potion must also succeed (a bigger multiplier only makes the product bigger). That's a monotonic property — a clear signal we don't need to check every single potion.

Key insight: If we sort potions first, then for each spell we only need to find "the weakest potion that still succeeds" 
everything stronger than that, all the way to the end of the array, automatically qualifies. This turns the problem into "find the first index where a condition becomes true," which is the classic Binary Search pattern.

How It Works

Step 1: Sort potions first (O(m log m))
Step 2: For each spell in spells (n of them):
We want the weakest potion such that spell * potion >= success
Rearranged, that's potion >= success / spell — but to avoid floating-point issues and overflow, we instead compare spell * potion >= success directly, 
casting to long to prevent integer overflow (since success goes up to 10^10, and spell[i] * potions[j] can exceed the int range)

Use a "lower bound" Binary Search to find the first index where the condition is true
function firstSuccessfulIndex(potions, spell, success):
    low = 0, high = potions.length
    while low < high:
        mid = low + (high - low) / 2
        if (long) spell * potions[mid] >= success:
            high = mid        // this might be the answer — look for something smaller
        else:
            low = mid + 1      // not strong enough — need something bigger
    return low   // the first index where it succeeds

Step 3: Number of successful potions = potions.length - firstSuccessfulIndex


When to use: Reach for the "Binary Search for lower/upper bound" pattern whenever you have a sorted (or sortable) array with a monotonic property 
"if this value satisfies the condition, every larger/smaller value does too" — especially when you're running many queries (n of them) against the same dataset (m potions). 
Sorting once and binary-searching per query is far cheaper than a nested loop.
When NOT to use: If potions were tiny, or you were only querying it once, sorting overhead might not pay off. But here both n and m can reach 10^5, so it's well worth it.

Trade-offs: Watch out for integer overflow — spell and potion each go up to 10^5, so their product can reach 10^10, which exceeds the int range (~2.1 × 10^9). 
Always cast to long before multiplying — this is the single most common mistake people make on this problem.

Mini Example

Dry run: spells = [5,1,3], potions = [1,2,3,4,5] (already sorted), success = 7

For spell = 5: find the first index where 5 * potions[mid] >= 7

low	high	mid	potions[mid]	5*potions[mid]	action
0	5	2	3	15	≥7 → high=2
0	2	1	2	10	≥7 → high=1
0	1	0	1	5	<7 → low=1
1	1	—	loop ends		return 1

→ index 1 is the first success → successful count = 5 - 1 = 4  matches expected output
Whenever you see "many queries against the same dataset," each asking for a "cutoff point" on a monotonic property (bigger/smaller always means passing the condition), 
think sort once + binary search per query. And build the habit of casting to long any time numbers in the problem reach 10^5-scale and get multiplied together — overflow bugs here are silent and easy to miss.

*/



