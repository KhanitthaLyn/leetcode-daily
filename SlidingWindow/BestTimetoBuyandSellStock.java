//You are given an integer array prices where prices[i] is the price of NeetCoin on the ith day.

//You may choose a single day to buy one NeetCoin and choose a different day in the future to sell it.

//Return the maximum profit you can achieve. You may choose to not make any transactions, in which case the profit would be 0.

public int maxProfit(int[] prices) {
    int left = 0;              // buy day
    int right = 1;             // sell day
    int maxProfit = 0;

    while (right < prices.length) {
        if (prices[right] > prices[left]) {
            // profitable -> compute and update the answer
            int profit = prices[right] - prices[left];
            maxProfit = Math.max(maxProfit, profit);
        } else {
            // right is lower than or equal to left -> found a new low
            left = right;
        }
        right++;
    }

    return maxProfit;
}

---
public class Solution {
    public int maxProfit(int[] prices) {
        int maxP = 0;
        int minBuy = prices[0];

        for (int sell : prices) {
            maxP = Math.max(maxP, sell - minBuy);
            minBuy = Math.min(minBuy, sell);
        }
        return maxP;
    }
}

/* 
Brute force: Try every pair (buy day i, sell day j) where j > i, and find the max of prices[j] - prices[i] → nested loop → O(n²)
Why that's not enough: As n grows (even though this problem's constraint caps at 100, the underlying pattern needs to scale), 
checking every pair is wasteful — because for any given day j, all that actually matters is the lowest price before j. You never need to know about higher prices that came before it.

The insight: If you scan left to right just once, keeping track of "the lowest price seen so far," you get the best possible answer at every point without ever needing to look back and recheck.

How It Works
Step-by-step with [10,1,5,6,7,1]:

left	right	prices[left]	prices[right]	What happens
0	1	10	1	1 < 10 → left jumps to right (left=1)
1	2	1	5	profit = 4 → maxProfit = 4
1	3	1	6	profit = 5 → maxProfit = 5
1	4	1	7	profit = 6 → maxProfit = 6
1	5	1	1	equal → left jumps to right (left=5)

Result: 6, matching the expected output.

Use this when: You need to find the max "buy low, sell high" difference where the sell day must come strictly after the buy day (single transaction only).
Don't use this pattern when: The problem allows multiple transactions (Best Time to Buy/Sell Stock II) — that requires summing up profits from every upward swing 
(a different greedy approach), or if there's a cooldown/transaction fee, you'd need DP instead.
Trade-off: You get O(n) time and O(1) space, but you need to correctly understand that left doesn't shrink the window step-by-step (left++) like a normal sliding window 
it "resets" to a new minimum point. If you mistakenly treat it like a standard shrinking window, you either slow things down unnecessarily or get the logic wrong.

This is "one-pass min-tracking," not a classic shrink-style two-pointer window — the key is remembering the running minimum and comparing profit at every point, not managing a complex expand/shrink state.
The tell-tale signal for this pattern: whenever a problem says "single transaction only," think "track running minimum, compute profit at every point" immediately 
it's a distinct variant from the classic expand-shrink sliding window, but still belongs to the same family of single-pass linear scans.
  */
