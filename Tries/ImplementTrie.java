/*
A prefix tree (also known as a trie) is a tree data structure used to efficiently store and retrieve keys in a set of strings. 
Some applications of this data structure include auto-complete and spell checker systems.

Implement the PrefixTree class:
    PrefixTree() Initializes the prefix tree object.
    void insert(String word) Inserts the string word into the prefix tree.
    boolean search(String word) Returns true if the string word is in the prefix tree (i.e., was inserted before), and false otherwise.
    boolean startsWith(String prefix) Returns true if there is a previously inserted string word that has the prefix prefix, and false otherwise.

Example 1:
Input:
["Trie", "insert", "dog", "search", "dog", "search", "do", "startsWith", "do", "insert", "do", "search", "do"]
Output:
[null, null, true, false, true, null, true]

Explanation:
PrefixTree prefixTree = new PrefixTree();
prefixTree.insert("dog");
prefixTree.search("dog");    // return true
prefixTree.search("do");     // return false
prefixTree.startsWith("do"); // return true
prefixTree.insert("do");
prefixTree.search("do");     // return true

Constraints:
    1 <= word.length, prefix.length <= 1000
    word and prefix are made up of lowercase English letters.
*/


//Array children 
class PrefixTree {
    private static class Node {
        Node[] children = new Node[26];
        boolean isEnd = false;
    }

    private final Node root = new Node();

    public PrefixTree() {}

    public void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) node.children[i] = new Node();
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) return false;
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) return false;
        }
        return true;
    }
}
//HashMap children
class PrefixTree {
    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isEnd = false;
    }

    private final Node root = new Node();

    public PrefixTree() {}

    public void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return false;
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return false;
        }
        return true;
    }
}
//Array children + shared find helper
class PrefixTree {
    private static class Node {
        Node[] children = new Node[26];
        boolean isEnd = false;
    }

    private final Node root = new Node();

    public PrefixTree() {}

    public void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) node.children[i] = new Node();
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Node node = find(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }

    // Walks the path; returns the last node, or null if the path breaks
    private Node find(String s) {
        Node node = root;
        for (char c : s.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) return null;
        }
        return node;
    }
}

/*
HashSet answers search in O(L), but startsWith forces you to scan every stored word, which is O(N·L).
Sorted list + binary search can handle prefixes, but inserting means shifting elements.

With a Trie, all three operations cost O(L), where L is the length of the word. The cost depends on the word's length, not on how many words are stored.

How It Works
After inserting "dog" and then "do", the Trie looks like this:

(root) → d → o* → g*

* means isEnd = true: a word actually ends at this node.

Each node holds two things:

children: links to the nodes for the next characters.
isEnd: whether a word ends at this node.

The three operations:
insert: Walk from the root one character at a time, creating any missing child along the way. At the last character, set isEnd = true.
search: Walk the characters. If a child is missing, return false. If you reach the end, return node.isEnd.
startsWith: Same walk, but if you reach the end, return true without checking isEnd.

Why the example returns false: after inserting only "dog", search("do") is false. The path d → o exists, but o.isEnd is still false. Once "do" is inserted, the same search returns true.

Trade-offs & When to Use
Children as Node[26] vs HashMap

	Node[26]	HashMap<Character, Node>
Speed	Faster (direct index)	Slightly slower (hashing, boxing char)
Memory	26 slots per node, even if most are empty	Pays only for children that exist
Fits	Fixed small alphabet (lowercase a-z, like here)	Large or unknown alphabets

When NOT to use a Trie: if you only need exact lookups, a HashSet is simpler and uses less memory.

Complexity (all three): each operation is O(L) time. Space is O(total inserted characters) nodes. With the array versions, each node also carries 26 slots.

A path is not a word. The path tells you a prefix exists; isEnd tells you the whole word exists. Most Trie bugs come from mixing the two up.
Choose children by alphabet size. Use an array for a fixed small alphabet and a HashMap for anything larger or unknown.

*/
