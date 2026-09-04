//Given a string s, return true if it is a palindrome, otherwise return false.

//A palindrome is a string that reads the same forward and backward. It is also case-insensitive and ignores all non-alphanumeric characters.

//Note: Alphanumeric characters consist of letters (A-Z, a-z) and numbers (0-9).

class Solution {
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        
        while (left < right) {
            char cLeft = s.charAt(left);
            char cRight = s.charAt(right);
            
            if (!Character.isLetterOrDigit(cLeft)) {
                left++;
                continue;
            }
            if (!Character.isLetterOrDigit(cRight)) {
                right--;
                continue;
            }
            
            if (Character.toLowerCase(cLeft) != Character.toLowerCase(cRight)) {
                return false;
            }
            
            left++;
            right--;
        }
        
        return true;
    }
}

/*The most straightforward approach is:
Filter out only the alphanumeric characters
Convert everything to lowercase
Build a new string (or StringBuilder) and reverse it
Compare the original filtered string to the reversed one

How It Works
Use Two Pointers moving toward each other from both ends of the original string, without building any new string:
Workflow:
Set left = 0, right = s.length() - 1
While left < right:
If s.charAt(left) is not alphanumeric → left++ and continue
If s.charAt(right) is not alphanumeric → right-- and continue
Compare toLowerCase(s.charAt(left)) with toLowerCase(s.charAt(right)) → if they don't match, return false immediately
If they match → left++, right--
If the loop finishes with no mismatch → return true
s = "Was it a car or a cat I saw?"

left=0 ('W'), right=28 ('?')
  '?' is not alphanumeric -> right-- (move to 'w')
  
left=0 ('W'), right=27 ('w')
  compare 'w' == 'w' -> match! -> left++, right--

left=1 ('a'), right=26 ('a')
  compare 'a' == 'a' -> match! -> left++, right--

... (continues, skipping spaces encountered along the way)

Loop finishes with no mismatch -> return true
When to Use
When to use: Whenever a palindrome check involves "noise" (spaces, punctuation) mixed in — Two Pointers avoids the need for a separate pass to filter/clean the string first.
When NOT to use: If the string is very short and readability matters more than performance, filtering then comparing directly (filtered.equals(new StringBuilder(filtered).reverse().toString())) is more readable and fine for code that doesn't need heavy optimization.
Trade-offs: Two Pointers achieves O(1) space at the cost of slightly more complex code (you write the skip-non-alphanumeric logic yourself), compared to the filter-then-reverse approach, which is more straightforward but uses O(n) space. 

Time: O(n) — each character is visited at most once (not n² even with the continue statements, since left/right always move forward, never backward or repeat)
Space: O(1) — no new array/string is created; only pointer variables and temporary char variables are used

Java-specific note: Character.isLetterOrDigit() and Character.toLowerCase() are built-in methods that handle Unicode automatically, making them safer than manual checks like (c >= 'a' && c <= 'z'), which would miss edge cases like digits or other Unicode letters.

"When you see a palindrome check with noise (spaces/punctuation) mixed in, think Two Pointers that 'skip over' irrelevant characters as you go, rather than filtering the string clean first and checking afterward."
This approach genuinely saves space, and it's the same pattern you'll see in "Valid Palindrome II" (which allows deleting one character) — nail this one, and tackling harder variations becomes much easier.
*/

public class Solution {
    public boolean isPalindrome(String s) {
        StringBuilder newStr = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                newStr.append(Character.toLowerCase(c));
            }
        }
        return newStr.toString().equals(newStr.reverse().toString());
    }
}
/* This code produces the correct result, but the underlying logic has a subtlety that's worth being cautious about for readability — it's worth explaining exactly why it works.

The thing to watch out for: reverse() mutates in-place
java
return newStr.toString().equals(newStr.reverse().toString());

StringBuilder.reverse() does not return a new string — it reverses newStr itself in place and returns a reference back to itself (this).

So the question is: why does this code still work correctly, given that newStr gets mutated?

Why it's still correct: Java's evaluation order

Java evaluates expressions left to right, in the order written in the code:

First, Java calls newStr.toString() (the left side of .equals()) → at this point newStr has not been reversed yet → this gives string A = the original value (not yet reversed)
Second, Java calls newStr.reverse() → this mutates newStr into its reversed form → then .toString() is called on it → this gives string B = the reversed value
Compare A.equals(B) → correct, exactly what we want (comparing original vs. reversed)
newStr = "wasitacaroracatisaw"

Step 1: A = newStr.toString() -> "wasitacaroracatisaw" (before reverse)
Step 2: newStr.reverse() -> newStr becomes "wasitacaroracatisaw" (reversed)
        B = newStr.toString() (after reverse) -> "wasitacaroracatisaw"
Step 3: A.equals(B) -> true (since it's a palindrome)

Bottom line: this code works because Java guarantees the first .toString() gets evaluated before .reverse(), due to left-to-right evaluation order. But this is a risky thing to rely on if someone reads the code without knowing about this evaluation order — they might mistakenly assume newStr.toString() returns the already-reversed value (since newStr.reverse() appears in the same expression), which is a misunderstanding that could make debugging painful if a real bug ever showed up in more complex code like this.

Comparison with Two Pointers
	This code (filter + reverse)	Two Pointers
Time	O(n)	O(n)
Space	O(n) (builds newStr to hold filtered chars)	O(1)
Readability	Requires understanding Java's evaluation order to be confident it's correct	Straightforward — the logic is clear at a glance
Risk	If someone edits the code and slightly reorders the expression, it could silently break	No such risk
Key takeaway

This code is correct, but relying on the "evaluation order" of a method call with a mutating side effect like this is something worth avoiding in production code — even though it compiles and passes the tests, a code reviewer on a real team might flag this, since it's hard to maintain if someone edits it later without knowing this trick.

If you want to write the filter-then-compare approach in a more readable way, it's better to separate the steps explicitly:

java
public boolean isPalindrome(String s) {
    StringBuilder newStr = new StringBuilder();
    for (char c : s.toCharArray()) {
        if (Character.isLetterOrDigit(c)) {
            newStr.append(Character.toLowerCase(c));
        }
    }
    String original = newStr.toString();
    String reversed = newStr.reverse().toString();
    return original.equals(reversed);
}

This works exactly the same way, but separates original and reversed into clearly named variables — it's immediately understandable without needing to think about evaluation order at all. That said, both versions still cost O(n) space. If you actually want O(1) space, you'd need to go back to the Two Pointers approach.*/
