//Easy Mode
//Given an integer array nums, return true if any value appears more than once in the array, otherwise return false


//[1] Hash Set - Time: O(n) | Space: O(n)
import java.util.HashSet;

class Solution {
    public boolean containsDuplicate(int[] nums) {
        HashSet<Integer> seen = new HashSet<>();
        
        for (int num : nums) {
            if (seen.contains(num)) {  
                return true;     
            }
            seen.add(num);    
        }
        
        return false; 
    }
}

/*

Naive approach (Brute Force): Compare every pair in the array to see if any two values match (nested loop) → O(n²)

The problem: if n reaches 10^5 (100,000), comparing every pair means ~10^10 comparisons (10 billion) — way too slow. This will Time Limit Exceed (TLE) for sure.

Another approach: sort first, then check adjacent elements → O(n log n). Better, but not the fastest possible, and it costs you the original order of the array.

This is where the Hash Set comes in — it gives O(1) average-case lookup, bringing the whole problem down to O(n).

How It Works
Core idea: use a HashSet<Integer> as your "notepad," then loop through the array once:

For each num in nums:
If num is already in the set → duplicate found! Return true immediately.
If not → add num to the set.
If the loop finishes with no duplicate found → return false.

Why is HashSet.contains() O(1)? Internally, it uses a hash function to convert the value (int) directly into a memory "slot," instead of scanning one by one like an array or list would.

Complexity:
Time: O(n) — one pass through the array, contains/add are O(1) on average
Space: O(n) — worst case (no duplicates at all), you end up storing every value in the set

When to use: Almost always, when you want the fastest duplicate check and don't mind the extra memory — this is the default go-to for this kind of problem
When NOT to use:
If memory is heavily constrained (embedded systems, memory-limited environments) → consider in-place sorting instead (O(n log n) time but O(1) extra space if sorted in-place)
If the array is already sorted → just check adjacent elements directly, no need to spend space on a set
Main trade-off: O(n) time in exchange for O(n) space — this is the classic "space-for-time trade-off" you'll see constantly in Hashing problems

Mini Example

Checking duplicate emails during signup: Before inserting into the database, 
keep a set of emails seen in an import file to catch duplicates before hitting the database with real queries (reduces DB load)
Deduplication in log processing: Checking whether a request ID has already been processed (idempotency check) before processing it again

Trace example with nums = [1,2,3,3]:

num=1: seen={} → not found → add → seen={1}
num=2: seen={1} → not found → add → seen={1,2}
num=3: seen={1,2} → not found → add → seen={1,2,3}
num=3: seen={1,2,3} → already there! → return true 

Golden Rule: Whenever a problem asks "have I seen this value before" or "count frequency," think Hash Set/Map first. 
It's trading extra space to buy significantly faster time (from O(n²) or O(n log n) down to O(n)) — and in most real-world systems, that trade is well worth it.

*/
