/*
Given an array of strings strs, group all anagrams together into sublists. You may return the output in any order.
An anagram is a string that contains the exact same characters as another string, but the order of the characters can be different.

Example 1:
Input: strs = ["act","pots","tops","cat","stop","hat"]
Output: [["hat"],["act", "cat"],["stop", "pots", "tops"]]

Example 2:
Input: strs = ["x"]
Output: [["x"]]

Constraints:
    1 <= strs.length <= 10000.
    0 <= strs[i].length <= 100
    strs[i] is made up of lowercase English letters.
*/

class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        
        return new ArrayList<>(map.values());
    }
}

/*
Naive approach: Compare every word against every other word to check if they're anagrams (requires sorting or counting characters on every comparison) 
→ O(n² · k), where n is the number of words and k is the average word length.

The problem: with n ≤ 10000, comparing every pair means ~10^8 comparisons, on top of the string sort/compare cost each time — too slow.
Key insight: If two strings are anagrams of each other, they share some "key" that is exactly identical, such as:

The sorted string ("tops" → "opst", "stop" → "opst" — identical!)
Or a character frequency count (how many a's, b's, c's, ... z's appear)

Once you have a matching key, you can use a HashMap to group them instantly in O(1) per word (excluding the cost of building the key) 
→ the whole thing finishes in O(n · k) or O(n · k log k) if you use sorting to build the key.


Core idea: use a HashMap<String, List<String>> where the key is the "anagram signature" (here, the sorted string) and the value is the list of words sharing that key.

Step-by-step:
Create an empty HashMap<String, List<String>>
Loop through each word s in strs:
Convert s to a char array and sort the characters → get a standardized key (e.g. "tops" → "opst")
Use that key to find/create a list in the map and add the original word s (not the sorted version) to it
Return map.values() converted to a List<List<String>>

Complexity:
Time: O(n · k log k) — n words, each sorted in O(k log k) where k = word length
Space: O(n · k) — storing every word in the map


When to use: Almost always — this is the standard pattern for "grouping items by a shared property"
Alternative: use a character-count key instead of sorting
Instead of sorting letters, count the frequency of each letter (since the problem guarantees lowercase English letters only 
→ just 26 possible characters), then encode it as a string like "1#0#0#...#2" (count of a, count of b, ..., count of z)

Time: O(n · k) — faster, since you skip sorting (drops the log k factor)
Trade-off: slightly more complex code (you build the encoding yourself), but genuinely faster when k is large
When NOT to use the sorted-key approach: if the average word length is very long (large k), sorting every word becomes the bottleneck → switch to character counting instead
Main trade-off: ease of writing (sorted key is simpler to read) versus a small performance cost (the log k factor) —
for k ≤ 100 as given in this problem, the difference is negligible, so the sorted key is perfectly fine to use

Mini Example
This pattern — "build a canonical key to group things that look different but are actually the same" — shows up constantly in real work:

Deduplicating data that looks different but has the same content: e.g. normalizing addresses before comparing them for duplicates (trim whitespace, lowercase, sort tokens)
File content fingerprinting: checking whether two files have identical content regardless of metadata (using a hash of the content as the key)

Trace example with strs = ["act","pots","tops","cat","stop","hat"]:
"act" → sorted "act" → map: {"act": ["act"]}
"pots" → sorted "opst" → map: {"act": ["act"], "opst": ["pots"]}
"tops" → sorted "opst" → map: {"act": ["act"], "opst": ["pots","tops"]}
"cat" → sorted "act" → map: {"act": ["act","cat"], "opst": ["pots","tops"]}
"stop" → sorted "opst" → map: {"act": ["act","cat"], "opst": ["pots","tops","stop"]}
"hat" → sorted "aht" → map: {"act": ["act","cat"], "opst": [...], "aht": ["hat"]}
Result: [["act","cat"], ["pots","tops","stop"], ["hat"]]  (order may vary since HashMap doesn't guarantee ordering — the problem allows any order)


Golden Rule: When a problem asks you to "group things that look different but share some underlying property," 
find a way to build a canonical key (a standardized signature) so that items belonging together produce the exact same key, then throw them into a HashMap. 
This technique generalizes far beyond anagrams — dedup, clustering, and matching problems all use the same idea.

*/
