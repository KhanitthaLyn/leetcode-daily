/* # Reverse String (In-place, O(1) Space)

Question: 
You are given an array of characters which represents a string s. Write a function which reverses a string.

You must do this by modifying the input array in-place with O(1) extra memory.


## 1. The Big Picture & Analogy

Imagine 7 people standing in a line, and you want to reverse their order — the person at the front goes to the back, 
the person at the back comes to the front. The fastest way is to **swap the front and back person directly**, then work your way inward pair by pair 
no need for a second line to rebuild the order.

That's the core idea here: **swap elements in the original array, without creating a new one.**

## 2. Why Do We Need It?

Without the O(1) space constraint, you could just build a new array, loop through and insert values in reverse, or use `s[::-1]` in Python — done. But that approach uses O(n) extra memory.

This problem simulates real situations you'll actually run into:
- Working with very large datasets where copying is expensive (memory-constrained systems, embedded devices)
- Needing to mutate the original data directly to avoid extra allocation/GC overhead

## 3. Core Logic & How It Works

Use the **Two Pointers** technique:

1. Set `left = 0` and `right = n - 1`
2. Swap `s[left]` and `s[right]`
3. Move `left++` and `right--`
4. Repeat until `left >= right`

```
Index:  0   1   2   3   4
        h   e   l   l   o
        ↑               ↑
       left            right

Step 1: swap(0,4) -> o e l l h   | left=1, right=3
Step 2: swap(1,3) -> o l l e h   | left=2, right=2
Step 3: left >= right -> stop
Result: o l l e h ...

(reversing "hello" gives "olleh")
```

## 4. Trade-offs & When to Use

- **When to use:** Anytime the problem says "in-place" or "O(1) extra space" — this is the go-to pattern for modifying an array symmetrically from both ends toward the middle.
- **When NOT to use:** If the input is an immutable string (like a plain Python `str` rather than `list[char]`), you can't do true in-place mutation 
you'd need to convert to a list first. Also, if space isn't actually constrained, a more readable approach (slicing/new array) might be preferable for maintainability.
- **Trade-offs:** You gain space efficiency (O(1)) at the cost of mutating the input directly — this can cause side effects if other references to the same array exist elsewhere in your code.

## 5. Real-World Scenario / Mini Example
*/

#Python
def reverseString(s: list[str]) -> None:
    left, right = 0, len(s) - 1
    while left < right:
        s[left], s[right] = s[right], s[left]  # tuple-unpacking swap
        left += 1
        right -= 1
/*
> Time: O(n) — you only iterate halfway, but it's still linear overall  
> Space: O(1) — no new array created */

#Java
public void reverseString(char[] s) {
    int left = 0, right = s.length - 1;
    while (left < right) {
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;
        left++;
        right--;
    }
}

================
Input: ["n", "e", "e", "t"]

left=0, right=3
  swap(s[0], s[3]) -> ["t", "e", "e", "n"]
  left=1, right=2

left=1, right=2
  swap(s[1], s[2]) -> ["t", "e", "e", "n"]  (สลับ e กับ e ค่าเหมือนเดิม)
  left=2, right=1

left >= right -> stop

Output: ["t", "e", "e", "n"]  
        
================
Input: ["r", "a", "c", "e", "c", "a", "r"]

left=0, right=6 -> swap(r,r) -> unchanged, left=1, right=5
left=1, right=5 -> swap(a,a) -> unchanged, left=2, right=4
left=2, right=4 -> swap(c,c) -> unchanged, left=3, right=3

left >= right -> stop

Output: ["r", "a", "c", "e", "c", "a", "r"]  

 
// **Time:** O(n) — loop - n/2 - linear
// **Space:** O(1) — variable `left`, `right`, `temp` no parallel input
        
/*
> Note for Java: this uses `char[]` (a primitive array), not `Character[]` or `String` — because `char[]` 
is truly mutable and avoids the autoboxing overhead we discussed before with `Character` objects.

## 6. Lead's Key Takeaway
> **"See 'in-place' + an array that needs to be reversed/reordered from both ends → think Two Pointers first."**  
> This is a foundational building block that shows up again and again — Palindrome checks, Valid Palindrome II, Reverse Vowels, Sort Colors. 
Nail this pattern and you'll move much faster through related problems. */
