/*
A peak element is an element that is strictly greater than its neighbors.

You are given a 0-indexed integer array nums, find a peak element, and return its index. If the array contains multiple peaks, return the index to any of the peaks.

You may imagine that nums[-1] = nums[n] = -∞. In other words, an element is always considered to be strictly greater than a neighbor that is outside the array.

You must write an algorithm that runs in O(log n) time.
*/

class Solution {
    public int findPeakElement(int[] nums) {
        int low = 0, high = nums.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] < nums[mid + 1]) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }
}

//Time: O(log n), Space: O(1)

/*
The problem: The array isn't sorted, so how could we possibly find something in O(log n)? 
Most people's first instinct is "just loop through and check each element against its neighbors" — that's O(n), which fails the constraint.

Why O(n) isn't enough: The problem explicitly demands O(log n), which is the signature of Binary Search. 
But the array isn't sorted — so why would Binary Search even apply here?
Key insight: Binary Search doesn't require a sorted array — it only requires that at any point, you can confidently discard half the search space based on local information. 
Here, that local information is the slope between adjacent elements.

How It Works
Principle: At mid, compare nums[mid] with nums[mid+1]:

If nums[mid] < nums[mid+1] → the slope is rising → a peak must exist to the right → low = mid + 1
If nums[mid] > nums[mid+1] → the slope is falling → a peak is at mid or to the left → high = mid

Why is it always safe to discard the other half? Because the problem guarantees nums[-1] = nums[n] = -∞. 
That means any side that is currently "rising" is mathematically guaranteed to eventually fall back down to that -∞ boundary — 
so a peak must exist somewhere along that rising path. This guarantee is the invariant that makes halving safe every single time.

Step-by-step:

low = 0, high = n - 1
while low < high:
    mid = low + (high - low) / 2
    if nums[mid] < nums[mid + 1]:
        low = mid + 1      // peak is to the right
    else:
        high = mid          // peak is at mid or to the left
return low   // low == high is the peak's index

Note we use low < high (not <=), since we always compare mid with mid+1, and we need mid+1 to never go out of bounds. The loop naturally terminates exactly when low == high, so there's no separate edge case to handle afterward.


When to use: This pattern — binary-searching on a monotonic property (like slope direction) rather than on sorted values 
applies whenever a problem asks you to find a "state transition point" in a space where some decision condition consistently rules out half the remaining space.
When NOT to use: If the problem needs every peak, or specifically the largest peak, this approach doesn't work 
it only guarantees finding some peak, not the global maximum. For that you'd need an O(n) scan.

Trade-offs: You gain O(log n) time, but you give up determinism — you don't control which peak you land on. That's fine here since the problem explicitly allows returning any valid peak.

Mini Example
Dry run with nums = [1,2,1,3,4,5,0] (n=7, indices 0–6):

low	high	mid	nums[mid] vs nums[mid+1]	action
0	6	3	3 < 4	low = 4
4	6	5	5 > 0	high = 5
4	5	4	4 < 5	low = 5
5	5	—	loop ends	return 5

Key Takeaway
Binary Search isn't tied to "sorted arrays" — it's tied to having an invariant that guarantees half the search space cannot contain the answer. 
Whenever you see an O(log n) requirement on an array that doesn't look sorted, ask yourself: "what monotonic property lets me safely discard half the space?" 
Here, it was the direction of the slope between adjacent elements
  */

public class Solution {
    public int findPeakElement(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                return i;
            }
        }
        return nums.length - 1;
    }
}

/*
Same idea, different complexity.
Both solutions use the exact same invariant: "find the first index where the slope starts falling (nums[i] > nums[i+1]); if none is found, 
the last element must be the peak." The logic is 100% correct in both.

The difference:
Linear Scan	| Binary Search
Approach:	Walk left to right, one step at a time |	Jump to mid, discard half each time
Time:	O(n)	| O(log n)
Space:	O(1)	| O(1)
Meets problem's requirement:	 No (problem demands O(log n))	|  Yes

In other words, Linear Scan is like "hiking up the mountain step by step until you hit the point where it starts sloping down." 
Binary Search is like "jumping to the middle of the mountain first, checking if it's rising or falling, then deciding which half to explore next and discarding the other half entirely."
Both produce correct answers (they'd pass the given test cases), but Linear Scan would TLE (Time Limit Exceeded) for large n if the judge strictly checks time complexity 
worst case, it has to walk almost the entire array (e.g., when the peak sits at the very last index).

Bottom line: correct logic, wrong complexity — it's O(n), not the O(log n) the problem requires.

*/
