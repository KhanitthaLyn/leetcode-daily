//You are given two strings, word1 and word2. Construct a new string by merging them in alternating order, starting with word1 — take one character from word1, then one from word2, and repeat this process.
//If one string is longer than the other, append the remaining characters from the longer string to the end of the merged result.

//Return the final merged string.

class Solution {
    public String mergeAlternately(String word1, String word2) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        
        while (i < word1.length() || i < word2.length()) {
            if (i < word1.length()) {
                sb.append(word1.charAt(i));
            }
            if (i < word2.length()) {
                sb.append(word2.charAt(i));
            }
            i++;
        }
        
        return sb.toString();
    }
}

/* Using String += directly in a loop runs into the same issue we discussed in "Add Spaces" — strings are immutable, so concatenation becomes O(n²). 
In this problem, length <= 100 won't actually cause a TLE, but building the habit of using StringBuilder whenever you're constructing a string piece by piece in a loop is good practice.

The extra thing to think through here is that the two strings can have different lengths, so you need logic to handle what happens once one runs out first.

Workflow:
Set i = 0 as a pointer that moves through both strings together (a single pointer is enough since they advance in lockstep)
Loop while i < word1.length() or i < word2.length() (use OR since the lengths can differ)
If i < word1.length() → append word1.charAt(i)
If i < word2.length() → append word2.charAt(i)
i++

When to use: Any time you need to "alternate" pulling data from two sources at once, especially when their lengths differ — use a single pointer moving through both, checking each source's bound independently.
When NOT to use: If the merge logic is more complex (e.g. merging based on comparison, like in merge sort), separate pointers (i, j) are a better fit, since each side might advance at a different rate.
Trade-offs: Using a single pointer (i) makes the code more compact than using two, but only works when both sides advance "exactly one position at a time" in lockstep. 
If the logic is more complex (e.g. needing to skip certain elements), you'd need separate pointers.

Time: O(n + m) — n and m are the lengths of word1 and word2 respectively; a single pass until both are exhausted
Space: O(n + m) — the buffer holds a result as long as the combined length of both strings (necessary space for the output)

Note: notice the two if statements are separate (not if-else), because both conditions need to be checked independently 
if word1 runs out but word2 hasn't yet, you still need to append from word2 in that same iteration.

"When a problem asks you to 'alternate' pulling from two sources of different lengths, use a single pointer moving through both, 
and check each side's bound independently with separate if statements (not if-else) — this automatically handles 'the remainder of the longer side' without writing a separate loop to deal with the tail."
*/ 

public class Solution {
    public String mergeAlternately(String word1, String word2) {
        StringBuilder res = new StringBuilder();
        int i = 0, j = 0;
        while (i < word1.length() && j < word2.length()) {
            res.append(word1.charAt(i++));
            res.append(word2.charAt(j++));
        }
        res.append(word1.substring(i));
        res.append(word2.substring(j));
        return res.toString();
    }
}

/* Complexity
Time: O(n + m) — same as before, one full pass through both strings
Space: O(n + m) for the buffer, plus extra allocation from substring()
The notable trade-off

String.substring() in current Java versions (since Java 7 update 6) copies a brand-new character array 
it doesn't share memory with the original string like older versions did. So calling word1.substring(i) and word2.substring(j) each creates a new String object before it gets appended into the StringBuilder. 
That's extra allocation the single-pointer version (using if/if inside one loop) doesn't have, since it appends character by character straight into the same buffer the whole way through.

With length <= 100 in this problem, the impact is negligible in practice — but it's worth knowing this cost exists if you were dealing with much larger strings.*/
