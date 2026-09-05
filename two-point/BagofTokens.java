//You start with an initial power of power, an initial score of 0, and a bag of tokens given as an integer array tokens, where each tokens[i] denotes the value of tokeni.

//Your goal is to maximize the total score by strategically playing these tokens. In one move, you can play an unplayed token in one of the two ways (but not both for the same token):

   // Face-up: If your current power is at least tokens[i], you may play tokeni, losing tokens[i] power and gaining 1 score.
    //Face-down: If your current score is at least 1, you may play tokeni, gaining tokens[i] power and losing 1 score.

//Return the maximum possible score you can achieve after playing any number of tokens.


class Solution {
    public int bagOfTokensScore(int[] tokens, int power) {
        Arrays.sort(tokens);
        int left = 0, right = tokens.length - 1;
        int score = 0, maxScore = 0;
        
        while (left <= right) {
            if (power >= tokens[left]) {
                power -= tokens[left];
                score++;
                left++;
                maxScore = Math.max(maxScore, score);
            } else if (score >= 1) {
                power += tokens[right];
                score--;
                right--;
            } else {
                break;
            }
        }
        
        return maxScore;
    }
}

/*
If you tried to brute-force every possible order of playing tokens (face-up or face-down), it would explode exponentially, since each token has 2 choices (play or not, face-up or face-down) — with tokens.length up to 1000, trying every possibility is completely infeasible.

The key insight is realizing that the order of play doesn't matter as much as "which token to pick when" — and there's actually an optimal pattern if we sort the tokens first.

How It Works
Use Two Pointers + Greedy after sorting:

Workflow:
Sort tokens in ascending order
Set left = 0 (pointing to the cheapest token), right = tokens.length - 1 (pointing to the most expensive)
Set score = 0, maxScore = 0
While left <= right:
If power >= tokens[left] → play face-up: power -= tokens[left], score++, left++, then update maxScore = max(maxScore, score)
If face-up isn't possible (not enough power) but score >= 1 → play face-down: power += tokens[right], score--, right--
If neither move is possible → break out of the loop immediately (stuck)
Return maxScore

When to Use
When to use: Problems involving two exchangeable resources (power ↔ score) where you need to find an optimal sequence — sort + two-pointer greedy fits this pattern very well.
When NOT to use: If the problem has more complex constraints, like dependencies between tokens (must play A before B), this greedy approach won't work — you'd need DP or a graph-based approach instead.
Trade-offs: You need to sort first, which costs O(n log n), but in exchange you reduce the problem from exponential to O(n) after sorting — a very good trade.

Why the greedy approach is correct (Greedy Stays Ahead):

Playing the cheapest token first when trading for score → spends the least power for 1 point of score, leaving the most power available for future moves
Playing the most expensive token first when trading back for power → gets back the most power for the 1 point of score you sacrifice
Both cases are about "maximizing what you gain per unit spent" at each step, which is the core reason greedy is always optimal for this problem

Time: O(n log n) — dominated by the sort; the two-pointer loop itself is O(n)
Space: O(1) extra (not counting the sort's own space, which depends on Arrays.sort's implementation — for a primitive int[] in Java, it uses dual-pivot quicksort, which is O(log n) space)

Why track maxScore separately from score: because the highest score achieved doesn't necessarily happen right at the end of the loop — as you can see in the dry run above, the score goes up and down along the way (up on face-up, down on face-down). You need to track the maximum value throughout, not just the final one.

"Whenever you see a problem with two exchangeable resources (like power ↔ score) where you need to find an optimal sequence, always sort the data first, then check whether a two-pointer greedy approach works — play the smallest to save resources, play the largest to compensate."
This pattern is similar to the "Greedy Stays Ahead" idea we discussed with Append Characters — the same core principle applies: pick the choice that's "most worth it per unit" at each step, and that leads to a globally optimal result.
*/ 

