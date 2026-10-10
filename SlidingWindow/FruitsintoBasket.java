/*
You are visiting a farm that has a single row of fruit trees arranged from left to right. The trees are represented by an integer array fruits where fruits[i] is the type of fruit the i-th tree produces.

You want to collect as much fruit as possible. However, the owner has some strict rules that you must follow:

    You only have two baskets, and each basket can only hold a single type of fruit. There is no limit on the amount of fruit each basket can hold.
    Starting from any tree of your choice, you must pick exactly one fruit from every tree (including the start tree) while moving to the right. The picked fruits must fit in one of your baskets.
    Once you reach a tree with fruit that cannot fit in your baskets, you must stop.

Given the integer array fruits, return the maximum number of fruits you can pick.

Example 1:
Input: fruits = [1,2,1]
Output: 3
Explanation: We can pick from all trees.

Example 2:
Input: fruits = [0,1,2,2]
Output: 3
Explanation: We can pick from trees [1,2,2].
If we had started at the first tree, we would only pick from trees [0,1].

Constraints:

    1 <= fruits.length <= 100,000
    0 <= fruits[i] < fruits.length

*/

// Solution 1: Sliding Window + HashMap (standard)
public int totalFruit(int[] fruits) {
    Map<Integer, Integer> count = new HashMap<>();
    int left = 0, maxLen = 0;

    for (int right = 0; right < fruits.length; right++) {
        count.merge(fruits[right], 1, Integer::sum);

        while (count.size() > 2) {
            int f = fruits[left];
            count.put(f, count.get(f) - 1);
            if (count.get(f) == 0) {
                count.remove(f);
            }
            left++;
        }
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}

// Solution 2: Sliding Window + int[] counts (faster, no autoboxing)
// Valid because of the constraint: 0 <= fruits[i] < fruits.length
public int totalFruit(int[] fruits) {
    int[] count = new int[fruits.length];
    int types = 0, left = 0, maxLen = 0;

    for (int right = 0; right < fruits.length; right++) {
        if (count[fruits[right]]++ == 0) {
            types++;
        }

        while (types > 2) {
            if (--count[fruits[left]] == 0) {
                types--;
            }
            left++;
        }
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}

// Solution 3: Non-shrinking window (the window never shrinks, it just slides as a block)
// Only tracks "the best size ever seen"; doesn't shrink until perfectly valid
public int totalFruit(int[] fruits) {
    int[] count = new int[fruits.length];
    int types = 0, left = 0;

    for (int right = 0; right < fruits.length; right++) {
        if (count[fruits[right]]++ == 0) {
            types++;
        }

        if (types > 2) {   // 'if', not 'while'
            if (--count[fruits[left]] == 0) {
                types--;
            }
            left++;
        }
    }
    return fruits.length - left;   // final window size = answer
}

/*
Brute force: Try every starting point i, then walk right until you hit a third type → O(n²) (worst case, e.g. the whole array has only 2 types, so every start walks to the end).

Why that’s not enough: n goes up to 100,000 → O(n²) = 10,000,000,000 → timeout.

The insight: When right hits a third type, you don’t need to restart. Just move left forward until one type disappears from your baskets (count = 0), and the window is valid again. 
It’s monotonic, as we discussed: shrinking the window always improves the condition.

How It Works
Steps (Solution 1: HashMap counting):

right advances one tree at a time → count[fruits[right]]++
If map.size() > 2 → advance left one at a time: decrement the count of fruits[left], and if it hits 0, remove it from the map. Repeat until size ≤ 2.
Update maxLen = max(maxLen, right - left + 1)

Dry run of Solution 2 with [1,2,3,2,2]:

right	fruit	types	window	maxLen
0	1	1	[1]	1
1	2	2	[1,2]	2
2	3	3 → over! shrink: drop 1 (types=2), left=1	[2,3]	2
3	2	2	[2,3,2]	3
4	2	2	[2,3,2,2]	4

Result: 4 

Trade-offs
Solution 1 (HashMap)	Solution 2 (int[])	Solution 3 (non-shrinking)
Time	O(n)	O(n)	O(n)
Space	O(1) (map ≤ 3 entries)	O(n) (array of size n)	O(n)
Pros	Works for any fruit value (not tied to the constraint); easy to read	Fastest in practice, no boxing	Shortest code
Cons	Autoboxing + hashing overhead	Relies on the constraint fruits[i] < n; breaks with larger values	Harder to understand: left and the window mid-run aren’t a truly valid window, so it’s harder to debug

Use when: finding the longest subarray under a “number of distinct types ≤ k” condition.
Don’t use when: the problem is about subsequences (non-contiguous) or the condition isn’t monotonic.
Note on space: Solution 1 is truly O(1) because the map holds at most 3 entries. Solution 2 trades O(n) space for speed.

Mini Example
An analytics system that needs to find “the longest continuous period where a user used at most 2 features” from an event stream. 
It’s the same longest-window-with-at-most-k-distinct-keys problem (for a different k, just change > 2 to > k).

“Longest subarray with distinct ≤ k” = variable window + a frequency counter. Always expand right, shrink left when the condition breaks, 
and don’t forget to remove the key when its count hits 0 (or decrement types), otherwise you’ll count types wrong.
This problem is “Longest Substring with At Most K Distinct Characters” told as a story. When you see “two baskets / two types / at most k”, strip away the story and look for this pattern first.
*/
