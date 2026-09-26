/*
Given the head of a sorted linked list, delete all duplicates such that each element appears only once. Return the linked list sorted as well.

Example 1:
Input: head = [1,1,2]
Output: [1,2]

Example 2:
Input: head = [1,1,2,3,3]
Output: [1,2,3]

Constraints:

    The number of nodes in the list is in the range [0, 300].
    -100 <= Node.val <= 100
    The list is guaranteed to be sorted in ascending order.
*/

//Solution 1: Iterative (recommended)
//It uses O(1) space, reads cleanly, and handles head = null without a separate check.
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode next = curr.next; // 1. save the next node
        curr.next = prev;          // 2. flip the arrow
        prev = curr;               // 3. move prev
        curr = next;               // 4. move curr
    }
    return prev;
}

//Solution 2: Recursive
//The line that trips people up is head.next.next = head. Take the call where head = 2. By then 3 is already reversed, but 2.next still points to 3, so this line means "make 3 point back to 2." Don't forget head.next = null, or 2 and 3 will point at each other and form a cycle.
public ListNode reverseList(ListNode head) {
    if (head == null || head.next == null) {
        return head;                 // empty or single node: already reversed
    }
    ListNode newHead = reverseList(head.next); // trust that the rest is reversed
    head.next.next = head;           // make the next node point back to us
    head.next = null;                // cut the old arrow to avoid a cycle
    return newHead;                  // the old last node, passed all the way up
}

//Solution 3: Stack (baseline)
//This is the easiest to understand because it relies on the stack's LIFO order, but it uses O(n) memory. In an interview, present it as the first step: "Brute force works like this, but we can do it in O(1) space." Then move to Solution 1.
public ListNode reverseList(ListNode head) {
    if (head == null) return null;
    Deque<ListNode> stack = new ArrayDeque<>();
    while (head != null) {
        stack.push(head);
        head = head.next;
    }
    ListNode newHead = stack.pop();
    ListNode curr = newHead;
    while (!stack.isEmpty()) {
        curr.next = stack.pop();
        curr = curr.next;
    }
    curr.next = null; // important: the last node (old 0) still points to 1
    return newHead;
}

/*
A singly linked list only goes one way. A node knows its next node but not its previous one. The simplest approach is to dump all the values into an array or stack and rebuild the list. 
That works, but it costs O(n) extra memory and skips the skill this problem is testing: manipulating pointers in place.

This problem matters because it's a building block for many others, such as 234 Palindrome Linked List, 143 Reorder List, and 25 Reverse Nodes in k-Group. Once this one is solid, those get much easier.

You use three pointers: prev (the part already reversed), curr (the node being processed), and next (a saved reference so the rest of the list isn't lost).

Each iteration does these four steps, always in this order:

next = curr.next saves the next node first.
curr.next = prev flips the arrow.
prev = curr moves prev forward.
curr = next moves curr forward.

Trace with [0,1,2,3]:

Iteration	Before: prev / curr	After: reversed part (starting at prev)
1	null / 0	0 → null
2	0 / 1	1 → 0 → null
3	1 / 2	2 → 1 → 0 → null
4	2 / 3	3 → 2 → 1 → 0 → null
End	curr = null	return prev = 3

Why return prev and not curr? The loop ends when curr is null, so the last real node is prev.

Approach	Time	Space	When to use
Iterative (3 pointers)	O(n)	O(1)	The default in interviews and real code
Recursive	O(n)	O(n) call stack	To show you understand recursion, or when the interviewer asks for it. Very long lists risk a StackOverflowError
Stack / Array	O(n)	O(n)	A baseline while you're thinking, not a final answer

The constraint caps the list at 1000 nodes, so recursion in Java is safe here. In real code with no length bound, iterative is the safer choice.


Save before you cut. Every time you change .next, ask yourself whether cutting it now loses your only path to some node. That causes almost every linked list bug.
Know these four lines cold (next, curr.next = prev, prev, curr). Many medium linked list problems reuse this pattern, such as reversing only the second half of a list or reversing k nodes at a time.
*/
