//You are given a string s consisting of words and spaces, return the length of the last word in the string.
//A word is a maximal substring consisting of non-space characters only.

//Forward Scan
  public class Solution {
    public int lengthOfLastWord(String s) {
        int length = 0, i = 0;
        while (i < s.length()) {
            if (s.charAt(i) == ' ') {
                while (i < s.length() && s.charAt(i) == ' ') {
                    i++;
                }
                if (i == s.length()) {
                    return length;
                }
                length = 0;
            } else {
                length++;
                i++;
            }
        }
        return length;
    }
}

//Backward Scan
class Solution {
    public int lengthOfLastWord(String s) {
        int i = s.length() - 1;
        
        // Phase 1: skip trailing spaces
        while (i >= 0 && s.charAt(i) == ' ') {
            i--;
        }
        
        // Phase 2: count length of last word
        int length = 0;
        while (i >= 0 && s.charAt(i) != ' ') {
            length++;
            i--;
        }
        
        return length;
    }
}

/*The most straightforward approach is s.trim().split(" ") and taking the last element, which works, but:

split() builds an array of all the words up front, even though we only need the last one — wasting time and space on words we never use
If there are multiple consecutive spaces (see Example 2: " fly me to the moon "), splitting needs extra care to avoid empty strings sneaking into the result

Scanning backward from the end of the string (a backward two-pointer style scan) is more efficient, and gives you tighter control over the logic for skipping spaces.

Workflow:
Set pointer i = s.length() - 1 (start from the end of the string)
Phase 1 — skip trailing spaces: move i backward as long as s.charAt(i) == ' '
Phase 2 — count the word's length: set length = 0, then move i backward while incrementing length++, until you hit another space or i < 0
Return length
s = "   fly me   to   the moon  "
                              ↑ i starts here (last index)

Phase 1: skip the 2 trailing spaces -> i stops at 'n' in "moon"
Phase 2: count backward m-o-o-n -> length = 4
Hit a space before "moon" -> stop

Output: 4
*/

/*

The second code is a **Forward Scan** (scanning from front to back), while the first is a **Backward Scan** (scanning from back to front)
a completely different mindset, but both are valid and produce the correct result.

# Checking the logic: is it correct?
Let's look at the smartest part of your code — handling **trailing spaces**:

```java
while (i < s.length() && s.charAt(i) == ' ') {
    i++;
}
if (i == s.length()) {
    return length;  // <-- returns the "previous" length, not yet reset
}
length = 0;  // only reset if a new word actually follows
```

This is the key detail that makes the logic correct: when you hit a space and skip all the way to the end of the string (`i == s.length()`), 
that means there's no new word coming after it → you must **return the previous `length`, which hasn't been reset yet** 
(because that's the actual length of the last word). You check this condition *before* resetting, so trailing spaces don't corrupt the answer.

## Dry run with Example 2: `"   fly me   to   the moon  "`

```
scan through "fly" -> length=3, hit space -> reset length=0
scan through "me"  -> length=2, hit space -> reset length=0
scan through "to"  -> length=2, hit space -> reset length=0
scan through "the" -> length=3, hit space -> reset length=0
scan through "moon"-> length=4
hit trailing spaces at the end -> skip until i == s.length() -> return length=4 
```

Correct.

## Comparing the two approaches

| | Backward Scan | Forward Scan |
|---|---|---|
| Direction | end → start | start → end |
| Idea | Only look for the "last" word, ignoring everything before it | Walk through every word, "resetting" the counter each time a new word starts, so only the most recent word's length survives |
| Number of iterations (worst case) | May only scan part of the string near the end (if the last word is close to the end) | Always scans the entire string, start to finish |
| Time | O(n) | O(n) |
| Space | O(1) | O(1) |

## The real trade-off

The key difference is that **backward scan doesn't always need to read the whole string** — if the last word is near the end of the string (which is the common case), it only needs to scan a few characters to find the answer. **Forward scan, on the other hand, always walks through every character from start to finish**, no matter how short the last word is.

Both are O(n) in Big-O notation (since Big-O considers the worst case), but **in practice**, backward scan can be slightly faster in typical cases because it skips over irrelevant parts of the string.

**Bottom line:** the first is 100% correct — it's just a different angle on solving the same problem. Backward scan thinks "go directly to the answer," while forward scan thinks "walk through everything, continuously tracking the most recent value." Both are useful patterns worth knowing.*/
