/*
You are given a string s of zeros and ones, return the maximum score after splitting the string into two non-empty substrings (i.e. left substring and right substring).
The score after splitting a string is the number of zeros in the left substring plus the number of ones in the right substring.

Example 1:
Input: s = "011101"
Output: 5

Explanation: All possible ways of splitting s into two non-empty substrings are:
left = "0" and right = "11101", score = 1 + 4 = 5
left = "01" and right = "1101", score = 1 + 3 = 4
left = "011" and right = "101", score = 1 + 2 = 3
left = "0111" and right = "01", score = 1 + 1 = 2
left = "01110" and right = "1", score = 2 + 1 = 3

Constraints:
    2 <= s.length <= 500
    s consists of characters '0' and '1' only.
*/
class Solution {
    public int maxScore(String s) {
        int totalOnes = 0;
        for (char c : s.toCharArray()) {
            if (c == '1') totalOnes++;
        }
        
        int leftZeros = 0;
        int rightOnes = totalOnes;
        int maxScore = 0;
        
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '0') {
                leftZeros++;
            } else {
                rightOnes--;
            }
            maxScore = Math.max(maxScore, leftZeros + rightOnes);
        }
        
        return maxScore;
    }
}


/*
Core idea: count the total number of '1's in the string upfront (representing "ones on the right" when the cut starts at the very left), 
then slide the cut point left-to-right, updating the score incrementally.
Step-by-step:

Count all '1's in the string, store in totalOnes
Set leftZeros = 0 (nothing on the left yet) and rightOnes = totalOnes (everything still on the right)
Loop i from 0 to length-2 (the cut must never leave a side empty — so we stop before the last character):
Move the character at i from the right side to the left: if '0' → leftZeros++, if '1' → rightOnes--
Compute the score at this cut = leftZeros + rightOnes, update maxScore
Return maxScore

Complexity:

Time: O(n) — two passes over the string (counting 1s, then sliding the cut), still O(n) overall
Space: O(1) — just a few counter variables
*/

//Prefix count + incremental update
//Fastest, least memory (O(1)) — no extra array needed at all.
class Solution {
    public int maxScore(String s) {
        int totalOnes = 0;
        for (char c : s.toCharArray()) {
            if (c == '1') totalOnes++;
        }
        
        int leftZeros = 0;
        int rightOnes = totalOnes;
        int maxScore = 0;
        
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '0') {
                leftZeros++;
            } else {
                rightOnes--;
            }
            maxScore = Math.max(maxScore, leftZeros + rightOnes);
        }
        
        return maxScore;
    }
}

//Full prefix sum arrays (store cumulative counts upfront)
//Same underlying logic, but stores prefix sums explicitly as arrays — 
//easier to follow for those less comfortable with immediate incremental variable updates, but costs unnecessary O(n) extra space, since the full array isn't actually needed.
class Solution {
    public int maxScore(String s) {
        int n = s.length();
        int[] zerosUpTo = new int[n + 1];
        int[] onesUpTo = new int[n + 1];
        
        for (int i = 0; i < n; i++) {
            zerosUpTo[i + 1] = zerosUpTo[i] + (s.charAt(i) == '0' ? 1 : 0);
            onesUpTo[i + 1] = onesUpTo[i] + (s.charAt(i) == '1' ? 1 : 0);
        }
        
        int maxScore = 0;
        for (int i = 1; i < n; i++) {
            int score = zerosUpTo[i] + (onesUpTo[n] - onesUpTo[i]);
            maxScore = Math.max(maxScore, score);
        }
        
        return maxScore;
    }
}

//Brute Force (recompute from scratch at every cut) — included to show why optimization matters
//Time: O(n²), Space: O(1) — works fine for n ≤ 500, but doesn't scale if the constraint were larger.
class Solution {
    public int maxScore(String s) {
        int maxScore = 0;
        for (int i = 1; i < s.length(); i++) {
            int zeros = 0, ones = 0;
            for (int j = 0; j < i; j++) {
                if (s.charAt(j) == '0') zeros++;
            }
            for (int j = i; j < s.length(); j++) {
                if (s.charAt(j) == '1') ones++;
            }
            maxScore = Math.max(maxScore, zeros + ones);
        }
        return maxScore;
    }
}

/*
Naive approach: Try every possible cut position (there are n-1 of them), recounting zeros on the left and ones on the right each time → each count takes O(n), giving O(n²) total.

The issue: even though n ≤ 500 makes O(n²) tolerable (250,000 operations), it's not optimal and does a lot of redundant work — we recompute "how many zeros/ones" from scratch at every cut point, 
even though only one character moves sides each time we shift the cut.

Key insight: When the cut point shifts one position at a time (one character moves from the right side to the left side), the score changes only slightly:

If the character that moved over is '0' → the left side gains one zero → total score +1
If it's '1' → the right side loses one '1' → total score -1

This is a prefix sum / running count pattern, letting us recompute the score in O(1) per cut instead of from scratch → the whole thing becomes just O(n).

This pattern — "slide the split point and update incrementally instead of recomputing" — is called Prefix Sum / Sliding Split Point and shows up often in real work:

Finding a break-even point in sales data analysis: finding the day where splitting the data into before/after gives the best combined result (e.g. max of cumulative profit before today + cumulative loss after today)
Load balancing by splitting work into two groups: finding the optimal cut point to divide tasks into two groups that optimizes some metric

Trace with s = "011101" (totalOnes = 4):

i=0 ('0'): leftZeros=1, rightOnes=4 → score=5 → maxScore=5
i=1 ('1'): leftZeros=1, rightOnes=3 → score=4 → maxScore=5
i=2 ('1'): leftZeros=1, rightOnes=2 → score=3 → maxScore=5
i=3 ('1'): leftZeros=1, rightOnes=1 → score=2 → maxScore=5
i=4 ('0'): leftZeros=2, rightOnes=1 → score=3 → maxScore=5
loop ends (stops at i=4 since length-1=5) → return 5 

Golden Rule: Whenever a problem asks you to "try every cut/split point and find the best one," first check whether shifting the cut by one position changes the target value only slightly (incrementally). 
If so, use a running count/prefix sum at O(1) per point instead of recomputing from scratch (O(n) per point) — this technique drops complexity from O(n²) to O(n) very often in "split array/string" style problems.

*/
