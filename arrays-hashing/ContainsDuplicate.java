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

//[2] Sort - Time: O(n log n) | Space: O(1)
import java.util.Arrays;

class Solution {
    public boolean containsDuplicate(int[] nums) {
        Arrays.sort(nums);   
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) {   
                return true;
            }
        }
        
        return false;
    }
}

//[3] Brute Force - Time: O(n²) | Space: O(1)
class ContainsDuplicateSolution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (!seen.add(num)) {
                return true; 
            }
        }
        return false;
    }
}
