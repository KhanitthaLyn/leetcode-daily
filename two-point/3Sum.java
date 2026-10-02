/*
Given an integer array nums, return all the triplets [nums[i], nums[j], nums[k]] where nums[i] + nums[j] + nums[k] == 0, and the indices i, j and k are all distinct.

The output should not contain any duplicate triplets. You may return the output and the triplets in any order.

Example 1:

Input: nums = [-1,0,1,2,-1,-4]

Output: [[-1,-1,2],[-1,0,1]]

Explanation:
nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0.
nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0.
nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0.
The distinct triplets are [-1,0,1] and [-1,-1,2].

Example 2:
Input: nums = [0,1,1]
Output: []
Explanation: The only possible triplet does not sum up to 0.


Constraints:
    3 <= nums.length <= 3000
    -10^5 <= nums[i] <= 10^5

*/

//Approach 1: Brute Force (for baseline understanding only)
//Time: O(n³) — three nested loops
//Space: O(n) for the HashSet dedup (worst case all triplets unique)
//This will TLE at n = 3000 — included only as the "obvious first idea" to compare against.
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Set<List<Integer>> resultSet = new HashSet<>();
        Arrays.sort(nums); // sort so duplicate triplets look identical
        int n = nums.length;
        
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        resultSet.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    }
                }
            }
        }
        return new ArrayList<>(resultSet);
    }
}

//Approach 2: Sort + Two Pointers (Optimal)
//Time: O(n²) — O(n log n) for sort, then O(n) outer loop × O(n) two-pointer scan
//Space: O(1) extra (excluding output and sort's internal space) — no auxiliary data structure needed for dedup, since sorting + pointer-skipping handles it naturally
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        
        for (int i = 0; i < n - 2; i++) {
            if (nums[i] > 0) break; // smallest number already positive, no way to reach 0
            if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicate "first" value
            
            int left = i + 1, right = n - 1;
            int target = -nums[i];
            
            while (left < right) {
                int sum = nums[left] + nums[right];
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) left++;
                    while (left < right && nums[right] == nums[right + 1]) right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }
}

//Approach 3: Sort + HashSet (no two pointers)
//Time: O(n²) — same asymptotic complexity as two pointers
//Space: O(n) — needs a HashSet per outer iteration to track seen values

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        
        for (int i = 0; i < n - 2; i++) {
            if (nums[i] > 0) break;
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            Set<Integer> seen = new HashSet<>();
            for (int j = i + 1; j < n; j++) {
                int complement = -nums[i] - nums[j];
                if (seen.contains(complement)) {
                    result.add(Arrays.asList(nums[i], complement, nums[j]));
                    while (j + 1 < n && nums[j] == nums[j + 1]) j++; // skip duplicates
                }
                seen.add(nums[j]);
            }
        }
        return result;
    }
}
/*
The naive approach is three nested loops checking every possible triplet — simple to think of, but it's O(n³), and with nums.length up to 3000, that's 3000³ ≈ 2.7 × 10¹⁰ operations — way too slow (TLE for sure).

The real challenge, though, isn't just speed — it's avoiding duplicate triplets in the output. If you don't handle duplicates carefully, 
[-1, -1, 2] might show up multiple times from different index combinations that happen to have the same values.

How It Works
The standard efficient approach: Sort first, then fix one number and use Two Pointers for the other two (this builds directly on the 2Sum-style two-pointer pattern you've been using).

Workflow:

Sort nums ascending
Loop i from 0 to n-3, treating nums[i] as the "fixed" first number
Skip i if nums[i] == nums[i-1] (avoids duplicate triplets starting with the same value)
If nums[i] > 0, break immediately — since sorted, no triplet summing to 0 is possible anymore (all remaining numbers are ≥ nums[i] > 0)
For each i, run two pointers left = i+1, right = n-1 searching for nums[left] + nums[right] == -nums[i]
If sum == target → record triplet, then skip duplicates on both left and right, move both inward
If sum < target → left++ (need a bigger sum)
If sum > target → right-- (need a smaller sum)
nums = [-1,0,1,2,-1,-4] -> sorted: [-4,-1,-1,0,1,2]

i=0 (-4): left=1,right=5 -> target=4
  -1+2=1 <4 -> left++
  -1+2=1... (continues, never reaches 4) -> no triplet found

i=1 (-1): left=2,right=5 -> target=1
  -1+2=1 == target -> found [-1,-1,2] -> skip dup, left++, right--
  left=3,right=4: 0+1=1==target -> found [-1,0,1] -> left++,right--
  left=4,right=3 -> loop ends (left>=right)

i=2 (-1): nums[2]==nums[1] -> skip (avoid duplicate)

i=3 (0): nums[i]>=0, left=4,right=5 -> 1+2=3 != 0 -> no match

Output: [[-1,-1,2],[-1,0,1]] 

When to use: Any "find k numbers summing to target" problem (2Sum, 3Sum, 4Sum) — sort + fix outer loop(s) + two-pointer inner search is the standard pattern once k ≥ 3, since it beats brute force significantly.
When NOT to use: If you need to preserve original indices (not just values) — sorting destroys index order, so if the problem asked for indices instead of values (like plain 2Sum does), you'd need a HashMap-based approach instead.
Trade-offs: Sorting costs O(n log n) and changes the original array order, but in exchange you get O(n²) instead of O(n³), plus sorting makes duplicate-skipping trivial (duplicates sit next to each other).

Comparing the Three
	Brute Force	Two Pointers	HashSet
Time	O(n³)	O(n²)	O(n²)
Space	O(n) (dedup set)	O(1) extra	O(n) (HashSet per i)
Dedup strategy	HashSet of triplets	Sorted-array pointer skipping	Manual duplicate-skip in inner loop
Readability	Simplest to understand	Clean once you know the pattern	Slightly less intuitive (complement logic)

Two Pointers is the clear winner — same O(n²) time as HashSet, but O(1) extra space since it leverages the sorted array directly instead of building a new HashSet per outer iteration.


"Whenever a 'sum to target' problem involves 3+ numbers, think: sort first, then fix the outer number(s) and reduce the inner search to a 2-pointer problem. 
Sorting isn't just for correctness here — it's also what makes duplicate-skipping nearly free, since identical values end up adjacent."
This pattern extends directly to 4Sum (fix two numbers, two-pointer the remaining two) — recognizing this family of problems saves a lot of time once you've internalized it.
*/
