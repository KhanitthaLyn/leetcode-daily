/*
Design Add and Search Word Data Structure
Medium Topics Company Tags
Hints

Design a data structure that supports adding new words and searching for existing words.

Implement the WordDictionary class:

    void addWord(word) Adds word to the data structure.
    bool search(word) Returns true if there is any string in the data structure that matches word or false otherwise. word may contain dots '.' where dots can be matched with any letter.

Example 1:

Input:
["WordDictionary","addWord","addWord","addWord","search","search","search","search"]
[[],["day"],["bay"],["may"],["say"],["day"],[".ay"],["b.."]]

Output:
[null, null, null, null, false, true, true, true]

Explanation:
WordDictionary wordDictionary = new WordDictionary();
wordDictionary.addWord("day");
wordDictionary.addWord("bay");
wordDictionary.addWord("may");
wordDictionary.search("say"); // return false
wordDictionary.search("day"); // return true
wordDictionary.search(".ay"); // return true
wordDictionary.search("b.."); // return true

Constraints:

    1 <= word.length <= 25
    word in addWord consists of lowercase English letters.
    word in search consist of '.' or lowercase English letters.
    There will be at most 2 dots in word for search queries.
    At most 10,000 calls will be made to addWord and search.
*/


//Trie + recursive DFS (the standard answer)
//Recursion depth never exceeds the word length (≤ 25), so stack overflow isn't a concern. Because it returns true as soon as it finds a match, it doesn't have to explore every branch.
class WordDictionary {
    private static class Node {
        Node[] children = new Node[26];
        boolean isEnd = false;
    }

    private final Node root = new Node();

    public WordDictionary() {}

    public void addWord(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) node.children[i] = new Node();
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return dfs(root, word, 0);
    }

    private boolean dfs(Node node, String word, int i) {
        if (i == word.length()) return node.isEnd;

        char c = word.charAt(i);
        if (c == '.') {
            for (Node child : node.children) {
                if (child != null && dfs(child, word, i + 1)) return true;
            }
            return false;
        }

        Node child = node.children[c - 'a'];
        return child != null && dfs(child, word, i + 1);
    }
}
//B: Trie + iterative BFS (level by level)
//The queue holds every node that is still possible at the current level, and each pass of the outer loop consumes one character. 
//The downside compared with DFS is that it must walk every branch to the last character before it can check isEnd. It can't stop early.
class WordDictionary {
    private static class Node {
        Node[] children = new Node[26];
        boolean isEnd = false;
    }

    private final Node root = new Node();

    public WordDictionary() {}

    public void addWord(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) node.children[i] = new Node();
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Deque<Node> queue = new ArrayDeque<>();
        queue.add(root);

        for (char c : word.toCharArray()) {
            int size = queue.size();
            if (size == 0) return false;          // every branch has dead-ended

            for (int s = 0; s < size; s++) {
                Node node = queue.poll();
                if (c == '.') {
                    for (Node child : node.children) {
                        if (child != null) queue.add(child);
                    }
                } else {
                    Node child = node.children[c - 'a'];
                    if (child != null) queue.add(child);
                }
            }
        }

        for (Node node : queue) {
            if (node.isEnd) return true;
        }
        return false;
    }
}
//Bucket by length (no Trie)
//The insight here is that . matches exactly one letter, so a matching word must always have the same length. 
//That lets you discard every word of a different length up front. It's simple to write and useful as a baseline, but if thousands of words share one length, every search scans all of them, which risks TLE on large tests.
class WordDictionary {
    private final Map<Integer, List<String>> byLength = new HashMap<>();

    public WordDictionary() {}

    public void addWord(String word) {
        byLength.computeIfAbsent(word.length(), k -> new ArrayList<>()).add(word);
    }

    public boolean search(String word) {
        for (String candidate : byLength.getOrDefault(word.length(), List.of())) {
            if (matches(candidate, word)) return true;
        }
        return false;
    }

    private boolean matches(String candidate, String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            char p = pattern.charAt(i);
            if (p != '.' && p != candidate.charAt(i)) return false;
        }
        return true;
    }
}


/*
HashSet checks words without . quickly. With a ., though, you either compare against every stored word or generate every possible substitution (26^d words) and look each one up.
Trie wins because a . only branches into children that actually exist, not all 26 letters. Any branch that leads to no words is cut off immediately (pruning).

How It Works
addWord: identical to insert in problem 208.

search(word) looks at the character at position i on the current node:

If i == word.length(), return node.isEnd.
If it's a regular letter, move to that child. If the child doesn't exist, return false.
If it's a ., try every non-null child. If any branch returns true, return true right away. If every branch fails, return false.

Trace the example after adding day, bay, may:

"say": the root has no s child, so false.
".ay": . branches into d, b, m. Trying d first, walk a → y, where isEnd = true, so true immediately. b and m are never tried.
"b..": walk to b. The first . branches into a, the second . branches into y, where isEnd = true, so true.

Complexity

addWord: O(L)
search with no .: O(L)
search with .: worst case O(26^d · L), where d is the number of dots. This problem caps d at 2, so that's comfortably fine. Without that cap, the worst case is visiting every node in the Trie.


Trade-offs & When to Use
	A: Trie + DFS	B: Trie + BFS	C: Bucket by length
search without .	O(L)	O(L)	O(k·L), k = words of the same length
search with .	Branches only into existing children	Branches only into existing children	Still O(k·L)
Memory	Trie	Trie + queue	Plain strings
Strength	Short, readable, stops at the first match	No recursion	No Trie needed
Risk	-	Must walk every branch to the end before checking isEnd	May TLE when many words share a length

Edge case: if . is the last character, e.g. "da.", at least one child must have isEnd = true. A path existing is not enough.

A wildcard in a Trie means DFS into every child that exists. The Trie's advantage is that branches with no words get pruned automatically.
If you only need "is there at least one match?", prefer DFS over BFS. DFS stops at the first answer. BFS suits problems that need every answer or a shortest distance.

*/
