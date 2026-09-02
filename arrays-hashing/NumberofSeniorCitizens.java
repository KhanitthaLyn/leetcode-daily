//You are given a 0-indexed array of strings details. Each element of details provides information about a given passenger compressed into a string of length 15. The system is such that:

    //The first ten characters consist of the phone number of passengers.
    //The next character denotes the gender of the person.
    //The following two characters are used to indicate the age of the person.
    //The last two characters determine the seat allotted to that person.

class Solution {
    public int countSeniors(String[] details) {
        int count = 0;
        for (String d : details) {
            int age = Integer.parseInt(d.substring(11, 13));
            if (age > 60) count++;
        }
        return count;
    }
}

/* Core Logic & How It Works

Layout (0-indexed):

Index	0-9	10	11-12	13-14
Meaning	phone number	gender	age	seat

Steps:
Loop through every string in details
Extract characters at index 11-12 using substring(11, 13)
Parse it to an integer
If it's > 60, increment the counter
Return the final count

Complexity: Time O(n) where n = number of passengers (substring on a fixed-length string is effectively O(1) per element), Space O(1).

Trade-offs & When to Use
When to use: When the data format is fixed and guaranteed (fixed-width records) — e.g. legacy mainframe logs, certain binary/text protocols without delimiters.
When NOT to use: If the format isn't guaranteed (variable-length records, optional fields), this approach breaks immediately. Use delimiter-based parsing or proper serialization (JSON, CSV) instead.
Trade-offs: Very fast and simple, but fragile — if the schema ever changes (e.g. an extra country code digit gets added), the code silently produces wrong results instead of throwing a clear error.
*/
