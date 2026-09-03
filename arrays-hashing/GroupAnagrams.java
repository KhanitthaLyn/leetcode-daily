//Given an array of strings strs, group all anagrams together into sublists. You may return the output in any order.

//An anagram is a string that contains the exact same characters as another string, but the order of the characters can be different.

/* Why Do We Need It?

If we checked every pair of strings to see if they're anagrams of each other (comparing each string against every other string), that's O(n² × k log k) — way too slow when n can be up to 10,000. We need a way to group everything in a single pass, without doing pairwise comparisons.

The key insight: if we sort the characters of each word, anagrams will produce the exact same resulting string (e.g. "act" → "act", "cat" → "act"). We can use this sorted string as a key in a HashMap to group words together.

How It Works
Create a HashMap<String, List<String>> to hold the groups
Loop through every word in strs
Sort the characters of that word to get a "key" (e.g. "tops" → "opst")
Use that key to look up the map — if it doesn't exist yet, create a new list; if it does, add to the existing list
Return all the values (lists) from the map

Complexity: Time O(n × k log k) — n is the number of words, k is the average word length (since we sort each word). Space O(n × k) to store everything in the map.

When to Use
When to use: Whenever you need to group data by some "signature" that's invariant to ordering (permutation-invariant grouping) — anagrams are the classic example.
When NOT to use: If words are very long (large k), sorting every word gets expensive. In that case, use a different key strategy instead.
Trade-offs:
Sort-as-key approach (shown above): simple to write and understand, but sorting costs O(k log k)
Character-count-as-key approach (count frequency of a-z into an array, then convert to a string like "1#0#0...#1"): gets you O(k) per word — faster when k is large — but the code is slightly more complex and the key is always a fixed 26-length representation even for short words

This pattern shows up often when you need to "group data by fingerprint" — for example, deduplicating documents that have the same content but different formatting (using a hash of the normalized content as the key), or grouping log events that share the same fields but in different JSON key order.
*/

public class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> res = new HashMap<>();
        for (String s : strs) {
            char[] charArray = s.toCharArray();
            Arrays.sort(charArray);
            String sortedS = new String(charArray);
            res.putIfAbsent(sortedS, new ArrayList<>());
            res.get(sortedS).add(s);
        }
        return new ArrayList<>(res.values());
    }
}


/*Same logic, just a different way of inserting into the map.

**Same:**
- Sort characters of each word to form the key, group using HashMap
- Time complexity O(n × k log k), Space O(n × k) — identical
- 100% identical output

Both work the same way, but there are a couple of minor differences:

1. **Number of hash lookups** — `computeIfAbsent` does a single lookup (check + insert/retrieve in one operation). `putIfAbsent` + `get` does **two** lookups (one to check/insert, another to retrieve the list). 
This doesn't change the Big-O, but `computeIfAbsent` is slightly faster in practice due to a smaller constant factor.

2. **Wasted object creation** — `putIfAbsent(sortedS, new ArrayList<>())` creates a new `ArrayList` on every iteration, even when the key already exists 
that unused list just gets discarded and garbage collected. `computeIfAbsent` only creates a new ArrayList when the key is genuinely absent. Minor overhead, doesn't affect correctness.

3. **Readability** — second version is arguably a bit easier to follow for someone unfamiliar with `computeIfAbsent`, since each step is explicit.

Bottom line: both are correct and will pass all test cases. The difference is a micro-optimization (constant factor), not Big-O. For production code, `computeIfAbsent` is generally preferred since it's more concise and has slightly less overhead. */
