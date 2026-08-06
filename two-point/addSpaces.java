/*You are given a 0-indexed string s and a 0-indexed integer array spaces that describes the indices in the original string where spaces will be added. 
Each space should be inserted before the character at the given index.

    For example, given s = "EnjoyYourCoffee" and spaces = [5, 9], we place spaces before 'Y' and 'C', which are at indices 5 and 9 respectively. Thus, we obtain "Enjoy Your Coffee".

Return the modified string after the spaces have been added.

Constraints:

    1 <= s.length <= 3 * 105
    s consists only of lowercase and uppercase English letters.
    1 <= spaces.length <= 3 * 105
    0 <= spaces[i] <= s.length - 1
    All the values of spaces are strictly increasing.
*/

/*
## 1. The Big Picture & Analogy
Imagine you're typing up a manuscript with no spaces at all (`"LeetcodeHelpsMeLearn"`), and someone tells you: 
"insert a space right before the character at positions 8, 13, and 15." 
  Your job is to produce a new string with spaces inserted at exactly the right spots, while all the original characters stay in the same order.

In short: **"walk through the characters one by one, check whether this position needs a space inserted before it, then place the character."**

## 2. Why Do We Need It?

If you're not careful, most people (especially in Java) would write something like this:

  */
```java
String result = "";
for (int i = 0; i < s.length(); i++) {
    if (spaces contains i) result += " ";
    result += s.charAt(i);
}
```
/*
The problem is that **`String` in Java (and Python) is immutable**. Every time you do `result += ...`, 
it's not appending in place — it **creates an entirely new String** and copies everything from the old one into it.

If the string has n characters, doing this concat n times, each copying an average of n/2 characters, gives you **O(n²)** total work. 
With the constraint `s.length <= 3 * 10^5`, this will TLE (Time Limit Exceeded) immediately, since (3×10^5)² = 9×10^10 operations.

## 3. Core Logic & How It Works

The fix is to use a **mutable buffer** instead — `StringBuilder` in Java, or a `list` + `join()` in Python.

**Workflow:**
1. Set up a pointer `spaceIdx = 0` to track your position in the `spaces` array
2. Loop through `s` character by character using index `i`
3. Check if `spaceIdx < spaces.length` and `i == spaces[spaceIdx]` → if true, append a space first, then increment `spaceIdx`
4. Append `s[i]` to the buffer
5. After the loop ends, return the result from the buffer

```
s = "icodeinpython"
spaces = [1, 5, 7, 9]

i=0: no space -> "i"
i=1: spaces[0]=1 matches -> add space -> "i c"   spaceIdx=1
i=2: -> "i co"... (continues)
i=5: spaces[1]=5 matches -> add space -> "...code in"  spaceIdx=2
...
```

## 4. Trade-offs & When to Use

- **When to use:** Any time you need to build a string piece by piece inside a loop, 
especially when the input can be large (tens of thousands to hundreds of thousands of characters) — reach for `StringBuilder` (Java) or `list` + `''.join()` (Python) first, instead of direct `+=`.
- **When NOT to use:** If the string is very short (say, under 100 characters), the performance difference is negligible. Using `+=` for readability might be perfectly fine for a quick script.
- **Trade-offs:** `StringBuilder` trades a small amount of readability (you call `.append()` instead of `+`) for a massive performance difference (O(n) vs O(n²)) 
this matters a lot in real work like log processing or large-scale text generation.
*/

//Real-World Scenario / Mini Example

class Solution {
    public String addSpaces(String s, int[] spaces) {
        StringBuilder sb = new StringBuilder();
        int spaceIdx = 0;
        
        for (int i = 0; i < s.length(); i++) {
            if (spaceIdx < spaces.length && i == spaces[spaceIdx]) {
                sb.append(' ');
                spaceIdx++;
            }
            sb.append(s.charAt(i));
        }
        
        return sb.toString();
    }
}

/*
> **Time:** O(n) — a single pass through `s`; `StringBuilder.append()` is O(1) amortized  
> **Space:** O(n) — you need a buffer for the output, which is naturally longer than the input (it includes the added spaces). 
This is necessary space for the output itself, not avoidable extra overhead.

**Important note for Java:** why use a `spaceIdx` pointer instead of checking against `spaces` directly every iteration (e.g. `Arrays.binarySearch` or a nested loop)? 
Because `spaces` is guaranteed to be **strictly increasing** (per the constraints), advancing a single pointer forward (never backward) is enough 
no repeated searching needed. This is the same technique used in **merging two sorted sequences**, a pattern you'll see often in two-pointer problems.

Lead's Key Takeaway

> **"Whenever you're building a String in Java/Python piece by piece inside a loop, never use `+=` directly if the input could be large — switch to `StringBuilder` (Java) or `list` + `''.join()` (Python) right away."**  
> Because strings are immutable, naive concatenation silently becomes O(n²) the logic looks completely correct, but runtime falls apart on large inputs. 
It's a performance bug that code review sometimes misses if you don't understand what's happening under the hood.

  */
