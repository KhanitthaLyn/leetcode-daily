/*
You are given a 0-indexed string array words.
Let's define a boolean function isPrefixAndSuffix that takes two strings, str1 and str2:
    isPrefixAndSuffix(str1, str2) returns true if str1 is both a prefix and a suffix of str2, and false otherwise.
For example, isPrefixAndSuffix("aba", "ababa") is true because "aba" is a prefix of "ababa" and also a suffix, but isPrefixAndSuffix("abc", "abcd") is false.
Return an integer denoting the number of index pairs (i, j) such that i < j, and isPrefixAndSuffix(words[i], words[j]) is true.

Example 1:
Input: words = ["a","aba","ababa","aa"]
Output: 4

Explanation: In this example, the counted index pairs are:
i = 0 and j = 1 because isPrefixAndSuffix("a", "aba") is true.
i = 0 and j = 2 because isPrefixAndSuffix("a", "ababa") is true.
i = 0 and j = 3 because isPrefixAndSuffix("a", "aa") is true.
i = 1 and j = 2 because isPrefixAndSuffix("aba", "ababa") is true.
Therefore, the answer is 4.

Constraints:

    1 <= words.length <= 50
    1 <= words[i].length <= 10
    words[i] consists only of lowercase English letters.
*/

//Brute force with built-ins
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int count = 0;
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (words[j].startsWith(words[i]) && words[j].endsWith(words[i])) {
                    count++;
                }
            }
        }
        return count;
    }
}

//Brute force with manual char comparison
//The key expression is m - p + k: the start of the suffix plus offset k. Unlike A, you must check p > m yourself, or the index goes negative.
class Solution {
    public int countPrefixSuffixPairs(String[] words) {
        int count = 0;
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (isPrefixAndSuffix(words[i], words[j])) count++;
            }
        }
        return count;
    }

    private boolean isPrefixAndSuffix(String s1, String s2) {
        int p = s1.length(), m = s2.length();
        if (p > m) return false;
        for (int k = 0; k < p; k++) {
            if (s2.charAt(k) != s1.charAt(k)            // check prefix
             || s2.charAt(m - p + k) != s1.charAt(k)) { // check suffix
                return false;
            }
        }
        return true;
    }
}

//Pair Trie
//For part II (3045), change ans and the return type to long, because the number of pairs can overflow int.
class Solution {
    private static class Node {
        Map<Integer, Node> children = new HashMap<>();
        int count = 0; // number of earlier words ending at this node
    }

    public int countPrefixSuffixPairs(String[] words) {
        Node root = new Node();
        int ans = 0;
        for (String w : words) {
            Node node = root;
            int m = w.length();
            for (int k = 0; k < m; k++) {
                int key = (w.charAt(k) - 'a') * 26 + (w.charAt(m - 1 - k) - 'a');
                node = node.children.computeIfAbsent(key, x -> new Node());
                ans += node.count; // query: earlier words that are prefix+suffix
            }
            node.count++;          // insert: always after the query
        }
        return ans;
    }
}


/*
The constraints here are tiny (n ≤ 50, length ≤ 10), so brute force passes easily. For this problem, brute force is the right answer.

A Trie becomes necessary in the sibling problem 3045. Count Prefix and Suffix Pairs II, where n goes up to 10⁵ and O(n²) will TLE. 
That makes this problem a good place to practice the Trie technique before you need it for part II.

A plain Trie won't work directly, because a normal Trie can only check prefixes. You could build two Tries, one normal and one on reversed words, and intersect the results. 
That gets messy, though, because you have to confirm that the matching prefix and the matching suffix come from the same word.

3 Approaches
Approach A: Brute force with startsWith / endsWith
Use a double loop with i < j and check words[j].startsWith(words[i]) && words[j].endsWith(words[i]).
You don't need a separate length check, because startsWith returns false on its own if words[i] is longer.
Time is O(n² · L), which here is about 50 · 50 · 10, so trivial. Space is O(1).

Approach B: Brute force with manual char comparison
The logic is the same as A, but you compare characters yourself. For the prefix, check words[j][k] == words[i][k]. For the suffix, 
check words[j][m-p+k] == words[i][k], where m is the length of words[j] and p is the length of words[i].
This makes you work out the suffix indexing, which is the foundation for Approach C. Some interviewers also disallow built-ins.

Approach C: Pair Trie 
Insight: Instead of storing one character per step, store a pair (word[k], word[m-1-k]): the k-th character from the front together with the k-th character from the back.
If str1 is both a prefix and a suffix of str2, then the pair sequence of str1 is exactly a prefix of the pair sequence of str2. So the "prefix + suffix" problem reduces to an ordinary prefix lookup in a Trie.
Steps: Process the words from left to right. For each word:
Walk down the Trie along the word's pairs, and add the count of every node you pass to the answer. Here count is the number of earlier words that ended at that node.
After the walk, do count++ on the final node.
Querying while inserting gives you the i < j condition for free, because only earlier words are in the Trie.
Children key: Each key is a pair of letters, so you can encode it as an int, e.g. c1 * 26 + c2, and use HashMap<Integer, Node>. An array would need 676 slots per node, which wastes memory.
Time is O(total length of all words).

Tracing Example 1 with ["a","aba","ababa","aa"]:

"a" has pairs (a,a). The Trie is empty, so +0, then node1.count = 1.
"aba" has pairs (a,a)(b,b)(a,a). Passing node1 gives +1, then the end node's count becomes 1.
"ababa" has pairs (a,a)(b,b)(a,a)(b,b)(a,a). Passing node1 gives +1, and passing the end of "aba" gives +1, for +2.
"aa" has pairs (a,a)(a,a). Passing node1 gives +1. The second node is a new branch, so +0.
Total: 0 + 1 + 2 + 1 = 4 ✓

4. ⚖️ Trade-offs & When to Use
	A: built-in	B: manual	C: Pair Trie
Time	O(n²·L)	O(n²·L)	O(ΣL)
Code	Shortest	Medium	Longest
This problem (n ≤ 50)	 Best fit	Over-engineering
Part II (n ≤ 10⁵)	 TLE	 TLE	

Edge case: duplicate words. ["a","a"] must return 1, because a word counts as both a prefix and a suffix of itself. 
Approach C handles this correctly only if you add first, then count++. If you swap the order, a word gets paired with itself.

Read the constraints before choosing a technique. n ≤ 50 means brute force is the answer. In an interview, giving brute force and then saying "if n grows, 
I'd switch to a Pair Trie" scores better than jumping straight to the Trie.
When a condition runs in two directions (front + back), try pairing them into a single key. That often turns a hard problem into a plain prefix problem.
*/
